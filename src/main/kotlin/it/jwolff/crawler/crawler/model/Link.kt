package it.jwolff.crawler.crawler.model

import jakarta.persistence.*

@Entity
class Link() {

    @Id
    @GeneratedValue
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "source_id")
    lateinit var source: Page

    @ManyToOne
    @JoinColumn(name = "target_id")
    lateinit var target: Page

    lateinit var text: String

    constructor(sourceId: Long, targetId: Long, text: String) : this() {
        this.source = Page().apply { this.id = sourceId }
        this.target = Page().apply { this.id = targetId }
        this.text = text
    }
}