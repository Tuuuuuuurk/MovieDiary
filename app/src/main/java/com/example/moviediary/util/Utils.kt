package com.example.moviediary.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList

val <T> T.exhaustive: T
    get() = this

suspend fun <T> Flow<List<T>>.flattenToList() =
        flatMapConcat { it.asFlow() }.toList()