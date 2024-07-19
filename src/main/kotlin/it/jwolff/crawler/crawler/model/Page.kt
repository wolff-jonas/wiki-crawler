package it.jwolff.crawler.crawler.model

import it.jwolff.crawler.crawler.Url
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    indexes = [
        Index(name = "idx_url", columnList = "url")
    ]
)
open class Page() {
    @Id
    @GeneratedValue
    open var id: Long? = null

    open lateinit var title: String

    open lateinit var url: Url

    open var first_crawled: Instant = Instant.now()
    open var last_crawled: Instant = Instant.now()

    open var visited: Boolean = false

    @OneToMany(mappedBy = "source")
    open var outboundLinks: MutableList<Link> = mutableListOf()

    constructor(title: String, url: Url) : this() {
        this.title = title
        this.url = url
    }
}

