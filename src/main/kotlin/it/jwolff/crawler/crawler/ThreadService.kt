package it.jwolff.crawler.crawler

import it.jwolff.crawler.crawler.repository.PageRepo
import it.jwolff.crawler.crawler.worker.CrawlerWorker
import it.jwolff.crawler.crawler.worker.ResultSaver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.*

@Service
class ThreadService(
    @Autowired val pageRepo: PageRepo
) {

    private val logger: Logger = LoggerFactory.getLogger(ThreadService::class.java)

    val resultQueue = ConcurrentLinkedQueue<ScrapeResult>()
    val linksToCheck = ConcurrentLinkedQueue<Url>()
    val visitedLinks = ConcurrentHashMap.newKeySet<Url>()
    val futures = mutableSetOf<Future<Unit>>()

    val executorService = Executors.newThreadPerTaskExecutor(Executors.defaultThreadFactory())

    var saverThread: Thread? = null
    var saverWorker: ResultSaver? = null

    fun addScraperThread() {
        val worker = CrawlerWorker("Worker", linksToCheck, visitedLinks, resultQueue)
        futures.add(executorService.submit(Callable { worker.run() }))
        logger.info("Added crawler")
    }

    fun startSaverThread() {
        saverWorker = ResultSaver(resultQueue, pageRepo)
        saverThread = Thread(saverWorker)
        saverThread!!.start()
    }

    fun start() {
        //for debug only!
//        pageRepo.deleteAll()

        linksToCheck.addAll(pageRepo.getYetToCheck().map { "https://en.wikipedia.org$it" })
        visitedLinks.addAll(pageRepo.getVisited().map { "https://en.wikipedia.org$it" })

        if (linksToCheck.isEmpty()) {
            linksToCheck.add("https://en.wikipedia.org/wiki/Kotlin_(programming_language)")
        }

        for (i in 1..2) {
            addScraperThread()
        }
        startSaverThread()
    }

    fun shutdown() {
        futures.forEach { it.cancel(true) }
        executorService.shutdown()


        logger.info("Told crawlers to stop")

        if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
            logger.info("Crawlers didn't stop, shutting down now")
            executorService.shutdownNow()
            executorService.awaitTermination(30, TimeUnit.SECONDS)
        }

        logger.info("Stopping saver")
        saverWorker?.stop = true
        saverThread?.join()

        logger.info("Everything stopped")
    }
}