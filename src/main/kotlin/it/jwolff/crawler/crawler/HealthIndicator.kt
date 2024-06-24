package it.jwolff.crawler.crawler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component


@Component("Result")
class ResultHealthIndicator(
    @Autowired
    val threadService: ThreadService
) : HealthIndicator {
    override fun health(): Health {
        val health = Health.up()
        health.withDetail("resultQueueSize", threadService.resultQueue.size)
        return health.build()
    }

}