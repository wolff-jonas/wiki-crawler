package it.jwolff.crawler.crawler.worker

import it.jwolff.crawler.crawler.Link
import it.jwolff.crawler.crawler.ScrapeResult
import it.jwolff.crawler.crawler.model.Page
import it.jwolff.crawler.crawler.repository.PageRepo
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

open class ResultSaverTransactional {

    @Transactional
    open fun extracted(result: ScrapeResult, pageRepo: PageRepo) {
        val url = result.url
        val existingPage = pageRepo.findByUrl(url)
        var page = existingPage ?: Page(null, result.document.title() ?: "Untitled", url)
        page.title = result.document.title()
        page = saveWithoutRelationShips(page, pageRepo)

        val wikiLinks = (result.nonVisitedLinks + result.visitedLinks).filter { it.url != url }.distinctBy { it.url }
        val existingLinkedPages = pageRepo.findByUrlWithoutReferences(wikiLinks.map { it.url })
        val linkedPages = emptySet<Pair<Page, Link>>().toMutableSet()
        wikiLinks.forEach { link ->
            val linkedPage =
                existingLinkedPages.find { it.url == link.url } ?: saveWithoutRelationShips(
                    Page(
                        null,
                        "Not visited ${link.url}",
                        link.url
                    ), pageRepo
                )
            linkedPages.add(Pair(linkedPage, link))
        }
        linkedPages.forEach { pageRepo.saveLink(page.id, it.first.id, it.second.text) }
    }

    fun saveWithoutRelationShips(page: Page, pageRepo: PageRepo): Page {
        return if (page.id != null) {
            pageRepo.updatePage(page.id, page.title, Instant.now())
        } else {
            pageRepo.save(page)
        }
    }
}