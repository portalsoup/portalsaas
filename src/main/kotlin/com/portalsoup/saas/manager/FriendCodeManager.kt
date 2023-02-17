package com.portalsoup.saas.manager

import discord4j.core.`object`.entity.User

sealed class FriendCodeAction {
//
//    abstract fun run(): String?
//
//    data class Find(val user: User) : FriendCodeAction() {
//        override fun run(): String? = transaction {
//            FriendCode
//                .select { FriendCode.discordUserId eq user.id }
//                .map { it[FriendCode.ntdoCode] }
//                .singleOrNull()
//        }
//    }
//
//    data class Add(val user: User, val code: String) : FriendCodeAction() {
//        override fun run(): String = transaction {
//            FriendCode
//                .insert {
//                    it[discordUserId] = user.id
//                    it[ntdoCode] = code
//                }
//
//            "Code added!"
//        }
//    }
//
//    data class Remove(val user: User) : FriendCodeAction() {
//        override fun run(): String = transaction {
//            FriendCode
//                .deleteWhere { FriendCode.discordUserId eq user.id }
//
//            "Code deleted"
//        }
//    }
}
