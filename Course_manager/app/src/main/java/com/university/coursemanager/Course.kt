package com.university.coursemanager

import java.io.Serializable

data class Course(
    var id: String = "",
    var courseName: String = "",
    var courseCode: String = "",
    var instructor: String = "",
    var creditHours: String = "",
    var schedule: String = "",
    var room: String = "",
    var semester: String = ""
) : Serializable