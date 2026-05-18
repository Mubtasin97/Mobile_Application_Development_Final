package com.university.coursemanager

import android.content.Context
import java.util.UUID

object StudentNodeHelper {

    private const val PREF_NAME = "StudentPrefs"
    private const val KEY_STUDENT_NODE = "KEY_STUDENT_NODE"

    fun getStudentNode(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var studentNode = prefs.getString(KEY_STUDENT_NODE, null)

        if (studentNode == null) {
            studentNode = "student_" + UUID.randomUUID().toString()
            prefs.edit().putString(KEY_STUDENT_NODE, studentNode).apply()
        }

        return studentNode
    }
}