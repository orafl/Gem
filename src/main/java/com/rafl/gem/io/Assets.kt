package com.rafl.gem.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rafl.gem.core.Entity
import com.rafl.gem.core.GameState
import com.rafl.gem.utils.startAsync
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileInputStream

@Suppress("EXPERIMENTAL_API_USAGE")
inline class Asset<out T> (private val value: Deferred<T>) {
    fun get() : T? = try {
        value.getCompleted()
    } catch (e: IllegalStateException) { null }

    companion object {

        inline fun <T> load(crossinline f: AssetReader.() -> T) =
            Asset(startAsync(Dispatchers.IO) { AssetReader.f() })

        fun overwrite(path: String, content: ByteArray) =
            load { File(path).writeBytes(content) }

        fun overwrite(path: String, content: String)
                = overwrite(path, content.toByteArray())
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
        FileInputStream(Any::class.java.getResource(path).file).use {
            var next = it.read()
            while (next != -1) {
                if (next == '{'.toInt()) count++
                if (count > 0) sb.append(next.toChar())
                if (next == '}'.toInt()) {
                    if (--count == 0) {
                        val src = sb.toString()
                        val ent = mapper.parse<MutableMap<String, Any?>>(src)
                        parseEntity(ent)
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