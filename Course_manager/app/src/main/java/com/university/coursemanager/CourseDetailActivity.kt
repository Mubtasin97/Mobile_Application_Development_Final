package com.university.coursemanager

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var course: Course

    private lateinit var textCourseName: TextView
    private lateinit var textCourseCode: TextView
    private lateinit var textInstructor: TextView
    private lateinit var textCredits: TextView
    private lateinit var textSchedule: TextView
    private lateinit var textRoom: TextView
    private lateinit var textSemester: TextView
    private lateinit var fabEdit: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        @Suppress("DEPRECATION")
        val receivedCourse = intent.getSerializableExtra("course") as? Course

        if (receivedCourse == null) {
            Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        course = receivedCourse

        textCourseName = findViewById(R.id.textCourseName)
        textCourseCode = findViewById(R.id.textCourseCode)
        textInstructor = findViewById(R.id.textInstructor)
        textCredits = findViewById(R.id.textCredits)
        textSchedule = findViewById(R.id.textSchedule)
        textRoom = findViewById(R.id.textRoom)
        textSemester = findViewById(R.id.textSemester)
        fabEdit = findViewById(R.id.fabEdit)

        showCourseDetails()

        fabEdit.setOnClickListener {
            val intent = Intent(this, EditCourseActivity::class.java)
            intent.putExtra("course", course)
            startActivity(intent)
            finish()
        }
    }

    private fun showCourseDetails() {
        textCourseName.text = course.courseName
        textCourseCode.text = "Course Code: ${course.courseCode}"
        textInstructor.text = "Instructor: ${course.instructor}"
        textCredits.text = "Credit Hours: ${course.creditHours}"
        textSchedule.text = "Schedule: ${course.schedule}"
        textRoom.text = "Room: ${course.room}"
        textSemester.text = "Semester: ${course.semester}"
    }
}