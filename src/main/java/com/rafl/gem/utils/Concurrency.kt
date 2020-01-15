package com.rafl.gem.utils

import kotlinx.coroutines.*

inline fun <T> startAsync(type: CoroutineDispatcher,
                          crossinline f: suspend () -> T)
        = runBlocking { async(type) { f() } }
inline fun <T> startLaunch(type: CoroutineDispatcher,
                          crossinline f: suspend () -> T)
        = runBlocking { launch (type) { f() } }

fun <T> defer(value: T) = startAsync(Dispatchers.Default) { value }

inline fun <T, U> Deferred<T>.merge(crossinline f:(T) -> U): Deferred<U> =
    startAsync(Dispatchers.Default) { f(await()) }