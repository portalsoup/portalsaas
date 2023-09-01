package com.portalsoup.saas.util

public inline fun <T, C : Iterable<T>> C.onEachIf(predicate: (T) -> Boolean, action: (T) -> Unit): C {
    return apply {
        for (element in this) {
            if (predicate(element)) {
                action(element)
            }
        }
    }
}