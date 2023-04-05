package com.digiwh.tprod.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

const val PREF_NAME = "name"
const val PREF_BAG_NO = "bagNo"

object PrefManager{

    fun saveLong(context : Context, key : String, value : Long){
        val sharedPreferences = context.getSharedPreferences("tprod", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(context : Context, key : String): Long {
        val sharedPreferences = context.getSharedPreferences("tprod", MODE_PRIVATE)
        return sharedPreferences.getLong(key, 1L)
    }

    fun saveString(context : Context, key : String, value : String){
        val sharedPreferences = context.getSharedPreferences("tprod", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context : Context, key : String): String? {
        val sharedPreferences = context.getSharedPreferences("tprod", MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

}