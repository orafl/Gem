package com.rafl.gem.io

import com.rafl.gem.gfx.parseSprite
import com.rafl.gem.gfx.parseView
import com.rafl.gem.utils.parseTransform
import com.rafl.gem.utils.parseVectwo

typealias Deserializer = suspend (ComponentParser) -> Any?
inline fun sus(crossinline f: (ComponentParser) -> Any?): Deserializer = { f(it) }

object ComponentFactory {
    private val map = hashMapOf<String, Deserializer>()
    fun bind(vararg bindings: Pair<String, Deserializer>) {
        bindings.forEach { (k, v) -> map[k] = v }
    }

    operator fun get(componentType: String) = map[componentType]
}

internal fun internalBindings() {
    ComponentFactory.bind(
        "_gemVectwo" to sus(::parseVectwo),
        "_gemTransform" to ::parseTransform,
        "_gemSprite" to sus(::parseSprite),
        "_gemView" to ::parseView
    )
}