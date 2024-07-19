package it.jwolff.crawler.crawler.repository

import it.jwolff.crawler.crawler.model.Link
import org.springframework.data.repository.CrudRepository

interface LinkRepo : CrudRepository<Link, Long?> {


}