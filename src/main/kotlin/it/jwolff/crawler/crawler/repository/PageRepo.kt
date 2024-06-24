package it.jwolff.crawler.crawler.repository

import it.jwolff.crawler.crawler.Url
import it.jwolff.crawler.crawler.model.Page
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import java.time.Instant

interface PageRepo : Neo4jRepository<Page, String?> {

    /**
     * Avoid cyclic references
     */
    @Query("MATCH (n) WHERE n.url IN \$urls RETURN n")
    fun findByUrlWithoutReferences(urls: List<Url>): List<Page>

    @Query("MATCH (n) WHERE n.url = \$url RETURN n")
    fun findByUrl(url: Url): Page?

    /**
     * Only saves/updates the nodes, not the relationships
     */
    @Query(
        "MATCH (n:Page) WHERE elementId(n) = \$id SET n.title = \$title, n.last_crawled = \$lastCrawled RETURN n"
    )
    fun updatePage(id: String, title: String, lastCrawled: Instant): Page

    @Query(
        "match (a), (b) where elementId(a) = \$startId AND elementId(b) = \$endId merge (a)-[:LINK {text: \$linkText}]->(b) finish"
    )
    fun saveLink(startId: String?, endId: String?, linkText: String)

    @Query("match (n) where not( n.title starts with \"Not visited\") return n.url\n")
    fun getVisited(): HashSet<String>

    @Query("match (n) where n.title starts with \"Not visited\" return n.url\n")
    fun getYetToCheck(): HashSet<String>
}