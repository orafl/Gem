@file:Suppress("UNCHECKED_CAST")

package com.rafl.gem.io

import com.rafl.gem.utils.defer
import com.rafl.gem.utils.startAsync
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.*
import java.io.IOException

const val gemComponent = "_gemComponent"

internal fun parseEntity(ent: MutableMap<String, Any?>) {
   val components = arrayListOf<Job>()
    ent.keys.forEach { k ->
        val v = ent[k]
        if (v is ArrayList<*>)
            ent[k] = v.toPersistentList()
        if (v is Map<*, *>) {
            val component = gemComponent
            if (v.keys.contains(component)) {
                val type = v[component]
                if (type !is String) throw IOException("attribute `$component` can only point to a String.")
                val provider =
                    ComponentFactory[type] ?: throw IOException("Type `$type` isn't registered as a component.")
                val input = (v as Map<String, *>).toMutableMap()
                input.remove(component)
                components += GlobalScope.launch(Dispatchers.Default) {
                    try {
                        ent[k] = provider(ComponentParser(ent, input))
                    } catch (e: ClassCastException) {
                        throw IOException("Invalid type for value of key $component", e)
                    }
                }
            }
        }
    }
    runBlocking { components.forEach { it.join() } }
}

class ComponentParser internal constructor(
    val parent: Map<String, Any?>, val component: Map<String, Any?>)
{
    fun subParse(m: Map<String, *>) = ComponentParser(component, m)

    inline fun <reified T> findInParent(key: String): Deferred<T> =
        startAsync(Dispatchers.Default) {
            val msg = "$key:${T::class.qualifiedName} is not a field of $parent"
            when(val v = parent[key]) {
                is Map<*, *> -> {
                    if (v[gemComponent] != null) {
                        while(parent[key] !is T) {}
                        parent[key] as T
                    }
                    else throw IllegalArgumentException(msg)
                }
                is T -> v
                else -> throw IllegalArgumentException(msg)
            }
        }

    inline fun <reified T> findReferenceOrElse
                (field: String, f:(Any?) -> T): Deferred<T> {
        return when (val it = component[field]) {
            is String -> findInParent(it); else -> defer(f(it))
        }
    }

    inline fun <reified T> findReferenceOrNode
                (field: String, f:(Map<String, *>) -> T): Deferred<T> {
        return findReferenceOrElse(field) {
            if (it is Map<*, *>) f(it as Map<String, *>)
            else throw IllegalArgumentException("Field $field in $component is not a Node")
        }
    }
}