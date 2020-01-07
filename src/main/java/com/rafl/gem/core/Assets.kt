package com.rafl.gem.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import java.io.FileOutputStream

@Suppress("EXPERIMENTAL_API_USAGE")
inline class Asset<T> (private val value: Deferred<T>) {
    fun get() : T? = try {
        value.getCompleted()
    } catch (e: IllegalStateException) { null }

    companion object {
        inline fun <T> load(crossinline f: AssetReader.() -> T) = Asset(
            GlobalScope.async(Dispatchers.IO) { AssetReader.f() })

        fun overwrite(path: String, content: ByteArray) =
            load {
                File(Any::class.java.getResource(path).toURI()).writeBytes(content)
                /*FileOutputStream(File(Any::class.java.getResource(path).toURI()), false)
                    .use { it.write(content) }*/
            }

        fun overwrite(path: String, content: String)
                =  load {
            File(Any::class.java.getResource(path).toURI()).printWriter()
                .use { out -> out.print(content) }
            /*FileOutputStream(File(Any::class.java.getResource(path).toURI()), false)
                .use { it.write(content) }*/
        }
    }
}

typealias Task = Asset<Unit>
fun Task.finished() = get() != null

object AssetReader {
    private fun<T> ObjectMapper.parse(src: String) : T
            = readValue<T>(src, object : TypeReference<T>() {})

    fun gameStateFromJson(path: String): GameState {
        val mapper = ObjectMapper()
        val sb = StringBuilder()
        val ents = ArrayList<Entity>()
        var count = 0
        Any::class.java.getResourceAsStream(path).use {
            var next = it.read()
            while (next != -1) {
                if (next == '{'.toInt()) count++
                if (count > 0) sb.append(next.toChar())
                if (next == '}'.toInt()) {
                    if (--count == 0) {
                        val src = sb.toString()
                        val ent = mapper.parse<MutableMap<String, Any?>>(src)
                        ent.keys.forEach { k ->
                            if (ent[k] is ArrayList<*>)
                                ent[k] = (ent[k] as ArrayList<*>).toPersistentList()
                        }
                        ents += ent.toPersistentMap()
                        sb.clear()
                    }
                }
                next = it.read()
            }
        }
        return ents.toPersistentList()
    }
}

object AssetWritter {
    fun writeToJson(entity: Entity): String =
        ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(entity)
}
