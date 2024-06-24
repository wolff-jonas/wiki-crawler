package it.jwolff.crawler.crawler

import org.neo4j.cypherdsl.core.renderer.Configuration
import org.neo4j.cypherdsl.core.renderer.Dialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories


typealias Url = String

@SpringBootApplication
@EnableNeo4jRepositories
class CrawlerApplication {

    @Bean
    fun demo(@Autowired threadService: ThreadService): CommandLineRunner {
        return CommandLineRunner {
            threadService.start()
        }
    }

    @Bean
    fun cypherDslConfiguration(): Configuration {
        return Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
    }
}

fun main(args: Array<String>) {
    runApplication<CrawlerApplication>(*args)
    //exitProcess(0)
}