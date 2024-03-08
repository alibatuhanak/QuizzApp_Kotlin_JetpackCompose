package com.alibatuhanak.quizzapp.service;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun saveNumbers(key:String, value: Int){
        val editor = sharedPreferences.edit()
        editor.putInt(key, value )
        editor.apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    fun getData(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue) ?: defaultValue
    }
    fun clearAllData(){
        sharedPreferences.edit().clear().apply()
    }
}
