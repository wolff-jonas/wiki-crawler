package it.jwolff.crawler.crawler

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


typealias Url = String

@SpringBootApplication
class CrawlerApplication(private val threadService: ThreadService) {


    @Bean
    fun crawlerApplicationRunner(): CommandLineRunner {
        return CommandLineRunner {
            threadService.start()
        }
    }

}

fun main(args: Array<String>) {
    runApplication<CrawlerApplication>(*args)
    //exitProcess(0)
}