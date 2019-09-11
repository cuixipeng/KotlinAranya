package com.basic.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.basic.library.base.MyApplication
import java.io.*
import java.lang.ClassCastException
import kotlin.reflect.KProperty

/**
 * @author cuixipeng
 * @date 2019-08-20.
 * @description  kotlin 委托属性+sharedPreference
 */
class Preference<T>(val name: String, private val default: T) {

    companion object {
        private const val file_name = "kotlin_file"
        private val prefs: SharedPreferences by lazy {
            MyApplication.context.getSharedPreferences(file_name, Context.MODE_PRIVATE)
        }

        /**
         * 删除全部数据
         */
        fun clearPreference() {
            prefs.edit().clear().apply()
        }

        fun clearPreference(key: String) {
            prefs.edit().remove(key).apply()
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSharedPreferences(name, default)
    }


    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSharedPreferences(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun putSharedPreferences(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getSharedPreferences(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(getString(name, serialize(default)))
        }
        return res as T
    }

    /**
     * 序列化对象
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassCastException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val bytArrayInputStream = ByteArrayInputStream(redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(bytArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        bytArrayInputStream.close()
        return obj
    }

    /**
     * 查询某个key是否已经存在
     */
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    /**
     * 返回所有的键值对
     */
    fun getAll(): Map<String, *> {
        return prefs.all
    }
}