package com.portalsoup.saas.core.db.tables.gpx

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object BlogPostTable : IntIdTable("blog_post") {
    val title = varchar("title", 255)
    val body = text("body")
    val created = date("created_date")
}

class BlogPost(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<BlogPost>(BlogPostTable)

    var title by BlogPostTable.title
    var body by BlogPostTable.body
    var created by BlogPostTable.created

    var attachments by Attachment via BlogPostAttachmentTable
    var routes by Route via BlogPostRouteTable
}