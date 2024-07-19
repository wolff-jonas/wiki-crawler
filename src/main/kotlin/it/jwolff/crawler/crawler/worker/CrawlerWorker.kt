package it.jwolff.crawler.crawler.worker

import it.jwolff.crawler.crawler.LinkResult
import it.jwolff.crawler.crawler.ScrapeResult
import it.jwolff.crawler.crawler.Url
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Get URL from queue, and scrapes that page
 */
class CrawlerWorker(
    private val name: String,
    val queue: ConcurrentLinkedQueue<String>,
    val visitedLinks: MutableSet<String>,
    val resultQueue: ConcurrentLinkedQueue<ScrapeResult>
) : Runnable {
    private val logger: Logger = LoggerFactory.getLogger(CrawlerWorker::class.java)
    var stop = false

    // We are only interested in articles, everything else we ignore
    val ignoredUrlStartsWith =
        arrayOf(
            "Portal:",
            "Wikipedia:",
            "Help:",
            "Special:",
            "Main_Page",
            "Talk:",
            "File:",
            "Template:",
            "Category:"
        )

    override fun run() {
        try {
            logger.info("Crawler-$name starting")
            while (!stop) {
                if (queue.isEmpty() || resultQueue.size > 10) {
                    sleep(100)
                    continue
                }
                val url: Url = queue.poll() ?: continue
                try {
                    val document = Jsoup.connect(url).get()
                    visitedLinks.add(url)
                    val links =
                        document.getElementsByTag("a").map { LinkResult(it.attr("href"), it.text()) }
                    val wikiLinks = links.filter { it.url.startsWith("/wiki/") }
                        .filter { l -> !l.url.contains("#") }
                        .filter { l -> !ignoredUrlStartsWith.any { f -> l.url.startsWith("/wiki/$f") } }
                    val nonVisitedWikiLinks =
                        wikiLinks.filter { !visitedLinks.contains("https://en.wikipedia.org${it.url}") }
                    queue.addAll(nonVisitedWikiLinks.map { "https://en.wikipedia.org${it.url}" })
                    logger.debug("Crawler-$name added ${nonVisitedWikiLinks.size} new links")

                    val result =
                        ScrapeResult(
                            url.replace("https://en.wikipedia.org", ""),
                            document,
                            nonVisitedWikiLinks,
                            wikiLinks.filter { visitedLinks.contains(it.url) })
                    resultQueue.add(result)
                } catch (e: HttpStatusException) {
                    logger.info("Crawler-$name: Status exception", e)
                }
            }
        } catch (e: InterruptedException) {
            logger.info("Crawler-$name received interrupt. Stopping...")
        } catch (e: Exception) {
            logger.error("Crawler-$name: Unhandled exception during Crawler", e)
        }
        logger.info("Crawler-$name stopping")
    }
}