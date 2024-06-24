package it.jwolff.crawler.crawler.worker

import it.jwolff.crawler.crawler.ScrapeResult
import it.jwolff.crawler.crawler.repository.PageRepo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.measureTimeMillis

open class ResultSaver(val resultQueue: ConcurrentLinkedQueue<ScrapeResult>, val pageRepo: PageRepo) : Runnable {
    var stop = false
    private val logger: Logger = LoggerFactory.getLogger(ResultSaver::class.java)

    val tx = ResultSaverTransactional()

    override fun run() {
        while (!stop || resultQueue.isNotEmpty()) {
            if (resultQueue.isEmpty()) {
                sleep(100)
                continue
            }
            logger.info("Result queue size ${resultQueue.size}")
            val result = resultQueue.poll() ?: continue

            val timeMillis = measureTimeMillis { tx.extracted(result, pageRepo) }
            logger.info("Saving took $timeMillis ms")
        }
        logger.info("ResultSaver stopping")
    }


}