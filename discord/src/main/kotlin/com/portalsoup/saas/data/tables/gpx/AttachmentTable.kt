package com.portalsoup.saas.data.tables.gpx

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object AttachmentTable: IntIdTable("attachment") {
    val filename = varchar("filename", 255)
    val uri = varchar("uri", 255)
}

class Attachment(id: EntityID<Int>): IntEntity(id) {

    companion object : IntEntityClass<Attachment>(AttachmentTable)

    var filename by AttachmentTable.filename
    var uri by AttachmentTable.uri

    var blogPosts by BlogPost via BlogPostAttachmentTable
}