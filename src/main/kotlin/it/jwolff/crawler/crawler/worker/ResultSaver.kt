package it.jwolff.crawler.crawler.worker

import it.jwolff.crawler.crawler.LinkResult
import it.jwolff.crawler.crawler.ScrapeResult
import it.jwolff.crawler.crawler.model.Link
import it.jwolff.crawler.crawler.model.Page
import it.jwolff.crawler.crawler.repository.LinkRepo
import it.jwolff.crawler.crawler.repository.PageRepo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.measureTimeMillis

/**
 * Saves the results to a DB
 */
open class ResultSaver(
    val resultQueue: ConcurrentLinkedQueue<ScrapeResult>,
    val pageRepo: PageRepo,
    val linkRepo: LinkRepo
) : Runnable {

    var stop = false
    private val logger: Logger = LoggerFactory.getLogger(ResultSaver::class.java)

    override fun run() {
        // Don't stop until all results have been saved
        while (!stop || resultQueue.isNotEmpty()) {
            if (resultQueue.isEmpty()) {
                sleep(100)
                continue
            }
            logger.debug("Result queue size ${resultQueue.size}")
            val result =
                resultQueue.poll() ?: continue // In case something else already polled the queue and it is now empty

            val timeMillis = measureTimeMillis { save(result) }
            logger.debug("Saving took $timeMillis ms")
        }
        logger.info("ResultSaver stopping")
    }

    private fun save(result: ScrapeResult) {
        val url = result.url
        // Sometimes different links have different capitalisation for the same page
        // i.e. /APK vs /Apk
        // this is why we always convert a URL to lowercase before comparing them
        // however, not all capitalisation are valid for a page
        // i.e. /aPk might 404, therefore we cannot just save urls as all lowercase
        val existingPage = pageRepo.findByLowercaseUrl(url)

        var page = existingPage ?: Page(result.document.title() ?: "Untitled", url)
        page.title = result.document.title()
        page.visited = true
        page = pageRepo.save(page)

        val wikiLinks =
            (result.nonVisitedLinks + result.visitedLinks).filter { !it.url.equals(url, true) }
                .distinctBy { it.url.lowercase() }
        val existingLinkedPages = pageRepo.findAllByUrlInLowercase(wikiLinks.map { it.url.lowercase() })
        val linkedPages = emptySet<Pair<Page, LinkResult>>().toMutableSet()

        // Create Page and Link entities for every link on this page
        wikiLinks.forEach { link ->
            val linkedPage =
                existingLinkedPages.find { it.url.equals(link.url, true) } ?: pageRepo.save(
                    Page(
                        "Not visited: ${link.url}",
                        link.url
                    )
                )
            linkedPages.add(Pair(linkedPage, link))
        }
        linkedPages.forEach { linkRepo.save(Link(page.id!!, it.first.id!!, it.second.text)) }
    }

}