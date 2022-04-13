package com.paris.girl.easycook.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.perpetio.knowledgesharingapp.model.User


class PrefUtils {


    companion object {
        private var singleton: PrefUtils? = null
        private lateinit var preferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private var gsonConverter: Gson? = null
        const val KEY_USER: String = "user"

        fun with(context: Context): PrefUtils {
            if (null == singleton)
                singleton = Builder(context).build()
            return singleton as PrefUtils
        }

    }

    private class Builder(val context: Context) {

        fun build(): PrefUtils {
            return PrefUtils(context)
        }
    }

    constructor()

    @SuppressLint("CommitPrefEdits")
    constructor(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        editor = preferences.edit()
        gsonConverter = GsonBuilder().create()
    }

    fun setUser(user: User?) {
        preferences.edit()
            .putString(KEY_USER, Gson().toJson(user)).apply()
    }

    fun getUser(): User? {
        val userStr = preferences.getString(KEY_USER, null)
        return if (userStr != null) Gson().fromJson(userStr, User::class.java) else null
    }


    fun save(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun save(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun save(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun save(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun save(key: String, value: String?) {
        editor.putString(key, value).apply()
    }

    fun save(key: String, value: Set<String>) {
        editor.putStringSet(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return try {
            preferences.getFloat(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toFloat()
        }
    }

    fun getInt(key: String, defValue: Int): Int {
        return try {
            preferences.getInt(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toInt()
        }
    }

    fun getLong(key: String, defValue: Long): Long {
        return try {
            preferences.getLong(key, defValue)
        } catch (ex: ClassCastException) {
            preferences.getString(key, defValue.toString())!!.toLong()
        }
    }

    fun getString(key: String, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    fun getStringSet(key: String, defValue: Set<String>): Set<String>? {
        return preferences.getStringSet(key, defValue)
    }

    fun getAll(): MutableMap<String, *>? {
        return preferences.all
    }

    fun remove(key: String) {
        editor.remove(key).apply()
    }

    fun clear() {
        if (editor != null) {
            preferences.edit().clear().apply()
        }
    }


}