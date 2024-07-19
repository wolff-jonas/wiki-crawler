package it.jwolff.crawler.crawler

import org.jsoup.nodes.Document

/**
 * Holds the result from requesting a page
 */
data class ScrapeResult(
    val url: Url, val document: Document, val nonVisitedLinks: List<LinkResult>, val visitedLinks: List<LinkResult>
)

data class LinkResult(
    val url: Url, val text: String
)