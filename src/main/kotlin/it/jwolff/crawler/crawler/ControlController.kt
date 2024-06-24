package it.jwolff.crawler.crawler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControlController(
    @Autowired val threadService: ThreadService
) {

    @GetMapping("/status")
    fun status(): Map<String, String> {
        val alive = threadService.saverThread?.isAlive ?: false
        return mapOf(
            "resultQueueSize" to threadService.resultQueue.size.toString(),
            "scraperThreads" to threadService.futures.filter { !it.isDone }.size.toString(),
            "saverThreadRunning" to alive.toString(),
        )
    }

    @PostMapping("/start")
    fun start() {
        threadService.start()
    }

    @PostMapping("/stop")
    fun stop() {
        Thread {
            threadService.shutdown()
        }.start()
    }

}