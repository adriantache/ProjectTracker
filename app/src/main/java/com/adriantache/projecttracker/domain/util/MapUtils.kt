package com.adriantache.projecttracker.domain.util

@Suppress("UNCHECKED_CAST", "kotlin:S6530")
operator fun <K, V> Map<K, V>.plus(other: V): Map<K, V> = this.toMutableMap().apply {
    val key = (other as IdClass<*>).id
    this[key as K] = other
}

private class IdClass<K>(val id: K)
