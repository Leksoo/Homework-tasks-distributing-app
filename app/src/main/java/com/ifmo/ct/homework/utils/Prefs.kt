package com.ifmo.ct.homework.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ifmo.ct.homework.model.Distribution
import com.ifmo.ct.homework.model.Student
import com.ifmo.ct.homework.model.Teacher
import com.ifmo.ct.homework.model.User
import java.lang.reflect.Type

class Prefs(context: Context) {

    companion object {
        private const val SHARED_PREF_NAME = "SHARED_PREF_NAME"
        private const val USER_KEY = "USER"
        private const val USER_TYPE = "USER_TYPE"
        private const val DISTRIBUTION_KEY = "DISTRIBUTION"

        val gson: Gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        val STUDENT_TYPE: Type = object : TypeToken<Student>() {}.type
        val TEACHER_TYPE: Type = object : TypeToken<Teacher>() {}.type
        val DISTRIBUTION_TYPE: Type = object : TypeToken<Distribution>() {}.type


    }

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    var student: Student
        get() = gson.fromJson<Student>(sharedPreferences.getString(USER_KEY, null), STUDENT_TYPE)
                ?: {
                    Log.e("SharedPrefs", "NULL")
                    throw NullPointerException("User is null")
                }()
        set(user) = sharedPreferences.edit().putString(USER_KEY, gson.toJson(user, STUDENT_TYPE)).apply()

    var teacher: Teacher
        get() = gson.fromJson<Teacher>(sharedPreferences.getString(USER_KEY, null), TEACHER_TYPE) ?: {
            Log.e("SharedPrefs", "NULL")
            throw NullPointerException("User is null")
        }()
        set(user) = sharedPreferences.edit().putString(USER_KEY, gson.toJson(user, TEACHER_TYPE)).apply()


    fun getUserStatus(): Int {
        if(sharedPreferences.getString(USER_KEY, null) == null) {
            return -1
        }
        return sharedPreferences.getInt(USER_TYPE, -1)
    }

    fun saveUser(user : User){
        if(user.type ==User.STUDENT_TYPE){
            student = user as Student
        }
        else if(user.type ==User.TEACHER_TYPE){
            teacher = user as Teacher
        }
        sharedPreferences.edit().putInt(USER_TYPE,user.type).apply()
    }

    fun cleanUser() {
        sharedPreferences.edit().clear().apply()
    }

    var distribution: Distribution?
        get() = gson.fromJson<Distribution>(
                sharedPreferences.getString(DISTRIBUTION_KEY, null), DISTRIBUTION_TYPE)
        set(distribution) = sharedPreferences.edit().putString(DISTRIBUTION_KEY
                , gson.toJson(distribution, DISTRIBUTION_TYPE)).apply()
}