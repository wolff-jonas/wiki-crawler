package it.jwolff.crawler.crawler.model

import it.jwolff.crawler.crawler.Url
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import java.time.Instant

@Node(labels = ["Page"])
open class Page(
    @Id @GeneratedValue val id: String?,

    var title: String, val url: Url,

    var first_crawled: Instant = Instant.now(),
    var last_crawled: Instant = Instant.now()

) {
    /**
     * Not automatically populated, to avoid cyclic dependencies when reading from the DB
     */
    var outboundLinks: MutableList<Page> = mutableListOf()
}

