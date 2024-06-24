package it.jwolff.crawler.crawler.worker

import it.jwolff.crawler.crawler.Link
import it.jwolff.crawler.crawler.ScrapeResult
import it.jwolff.crawler.crawler.Url
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentLinkedQueue

class CrawlerWorker(
    private val name: String,
    val queue: ConcurrentLinkedQueue<String>,
    val visitedLinks: MutableSet<String>,
    val resultQueue: ConcurrentLinkedQueue<ScrapeResult>
) : Runnable {
    private val logger: Logger = LoggerFactory.getLogger(CrawlerWorker::class.java)
    var stop = false

    val notAllowedUrlStartsWith =
        arrayOf("Portal:", "Wikipedia:", "Help:", "Special:", "Main_Page", "Talk:", "File:", "Template:", "Category:")

    override fun run() {
        try {
            logger.info("Crawler worker $name starting")
            while (!stop) {
                if (queue.isEmpty() || resultQueue.size > 10) {
                    sleep(100)
                    continue
                }
                val url: Url = queue.poll() ?: continue
                val document = Jsoup.connect(url).get()
                visitedLinks.add(url)
                val links = document.getElementsByTag("a").map { Link(it.attr("href"), it.text()) }
                val wikiLinks = links.filter { it.url.startsWith("/wiki/") }
                    .filter { l -> !l.url.contains("#") }
                    .filter { l -> !notAllowedUrlStartsWith.any { f -> l.url.startsWith("/wiki/$f") } }
//                    .map { "https://en.wikipedia.org$it" }
                val nonVisitedWikiLinks =
                    wikiLinks.filter { !visitedLinks.contains("https://en.wikipedia.org${it.url}") }
                queue.addAll(nonVisitedWikiLinks.map { "https://en.wikipedia.org${it.url}" })
                logger.info("Worker-$name added ${nonVisitedWikiLinks.size} new links")

                val result =
                    ScrapeResult(
                        url.replace("https://en.wikipedia.org", ""),
                        document,
                        nonVisitedWikiLinks,
                        wikiLinks.filter { visitedLinks.contains(it.url) })
                resultQueue.add(result)

            }
        } catch (e: InterruptedException) {
            logger.info("Crawler worker received interrupt. Stopping...")
        }
        logger.info("Crawler worker $name stopping")
    }
}