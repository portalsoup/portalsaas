package com.portalsoup.saas.data

import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.ResultSet

/**
 * A convenience extension function to allow pure SQL queries as Strings to be executed and mapped.
 */
fun <T:Any> String.execAndMap(transform : (ResultSet) -> T) : List<T> {
    val result = arrayListOf<T>()
    TransactionManager.current().exec(this) { rs ->
        while (rs.next()) {
            result += transform(rs)
        }
    }
    return result
}