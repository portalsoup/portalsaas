package com.portalsoup.saas.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

/**
 * A mixin interface that adds the log() method to the class's scope, reducing the boilerplate involved to setup logging
 * on a class
 */
interface Logging

/**
 * Log a message using the calling class' type information
 */
inline fun <reified T : Logging> T.log(): Logger = getLogger(getClassForLogging(T::class.java))

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> =
    javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
