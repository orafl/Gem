@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.rafl.gem.core

import kotlinx.collections.immutable.*

const val stubField = "_gemStub"

typealias Entity = PersistentMap<String, *>

inline fun entity(builder: (MutableMap<String, Any?>) -> Unit): Entity
        = HashMap<String, Any?>().apply(builder).toPersistentMap()

inline fun Entity.update(builder: (MutableMap<String, Any?>) -> Unit): Entity
        = this.toMutableMap().apply(builder).toPersistentMap()

inline fun <reified T> Entity.get(key: String): T? = get(key) as? T
inline fun <reified T> Entity.get(): T? = values.find { it is T } as T
inline fun <reified T> Entity.getAll() = values.filterIsInstance<T>()

val stubEntity = entity { it[stubField] = Unit }

typealias GameState = PersistentList<Entity>
fun gameStateWith(vararg entities: Entity) : GameState = persistentListOf(*entities)
fun stubState()= gameStateWith(stubEntity)