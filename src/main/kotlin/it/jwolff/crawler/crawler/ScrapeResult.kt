package it.jwolff.crawler.crawler

import org.jsoup.nodes.Document

data class ScrapeResult(
    val url: Url, val document: Document, val nonVisitedLinks: List<Link>, val visitedLinks: List<Link>
)

data class Link(
    val url: Url, val text: String
)