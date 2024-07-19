package it.jwolff.crawler.crawler.repository

import it.jwolff.crawler.crawler.Url
import it.jwolff.crawler.crawler.model.Page
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface PageRepo : CrudRepository<Page, Long?> {

    fun save(page: Page): Page

    @Query("select p from Page p where lower(p.url) = lower(:url)")
    fun findByLowercaseUrl(url: Url): Page?

    @Query("SELECT p FROM Page p WHERE lower(p.url) IN :url")
    fun findAllByUrlInLowercase(url: Collection<Url>): List<Page>

    fun findAllByVisitedTrue(): HashSet<Page>

    fun findAllByVisitedFalse(): HashSet<Page>
}