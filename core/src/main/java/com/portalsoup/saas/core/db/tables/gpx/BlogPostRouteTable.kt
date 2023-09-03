package com.portalsoup.saas.core.db.tables.gpx

import org.jetbrains.exposed.sql.Table

object BlogPostRouteTable: Table("blog_post_attachment") {
    val blogPost = reference("blog_post_id", BlogPostTable.id)
    val route = reference("route_id", RouteTable.id)
    override val primaryKey = PrimaryKey(blogPost, route)
}