package com.portalsoup.saas.core.service

import com.portalsoup.saas.core.db.tables.FriendCode
import com.portalsoup.saas.core.db.tables.FriendCodeTable
import com.portalsoup.saas.core.extensions.Logging
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

object FriendCodeManager: Logging {

    fun lookupFriendCode(user: User): String = transaction {
        FriendCode.find { FriendCodeTable.user eq user.id }
            .singleOrNull()
            ?.code
            ?: "I didn't find that Friend Code"
    }

    fun addFriendCode(user: User, code: String?): String = transaction {
        val userMaybeExists = FriendCodeTable
            .select { FriendCodeTable.user eq user.id }
            .firstOrNull()

        if (userMaybeExists != null) {
            "I already have your code, use the \"update\" action if you want to change it"
        } else if (code == null) {
            "The code you provided was null!"
        } else {
            FriendCodeTable.insert {
                it[FriendCodeTable.user] = user.id
                it[FriendCodeTable.code] = code
                it[createdOn] = LocalDate.now()
            }
            "I added your friend code to my database"
        }
    }

    fun removeFriendCode(user: User): String = transaction {
        FriendCodeTable.deleteWhere { FriendCodeTable.user eq user.id }
        "Removed"
    }

    fun updateFriendCode(user: User, code: String?): String = transaction {
        if (code == null) {
            return@transaction "That's an invalid code"
        }
        FriendCodeTable.update({ FriendCodeTable.user eq user.id }) {
            it[FriendCodeTable.code] = code
        }
        "Updated your code"
    }
}
