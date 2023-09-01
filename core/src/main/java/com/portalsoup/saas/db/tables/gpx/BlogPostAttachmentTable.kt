package com.portalsoup.saas.db.tables.gpx

import org.jetbrains.exposed.sql.Table


object BlogPostAttachmentTable: Table("blog_post_attachment") {
    val blogPost = reference("blog_post_id", BlogPostTable)
    val attachment = reference("attachment_id", AttachmentTable)
    override val primaryKey = PrimaryKey(blogPost, attachment)
}