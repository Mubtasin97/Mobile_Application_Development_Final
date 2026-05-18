package com.university.coursemanager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class AddCourseActivity : AppCompatActivity() {

    private lateinit var textTitle: TextView
    private lateinit var editCourseName: EditText
    private lateinit var editCourseCode: EditText
    private lateinit var editInstructor: EditText
    private lateinit var spinnerCredits: Spinner
    private lateinit var editSchedule: EditText
    private lateinit var editRoom: EditText
    private lateinit var spinnerSemester: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val credits = listOf("1", "2", "3", "4")
    private val semesters = listOf("Spring 2025", "Summer 2025", "Fall 2025")

    private val databaseRef by lazy {
        FirebaseDatabase.getInstance()
            .getReference("courses")
            .child(StudentNodeHelper.getStudentNode(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        initViews()
        setupSpinners()

        textTitle.text = "Add Course"

        btnSave.setOnClickListener {
            saveCourse()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        textTitle = findViewById(R.id.textTitle)
        editCourseName = findViewById(R.id.editCourseName)
        editCourseCode = findViewById(R.id.editCourseCode)
        editInstructor = findViewById(R.id.editInstructor)
        spinnerCredits = findViewById(R.id.spinnerCredits)
        editSchedule = findViewById(R.id.editSchedule)
        editRoom = findViewById(R.id.editRoom)
        spinnerSemester = findViewById(R.id.spinnerSemester)
        progressBar = findViewById(R.id.progressBar)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun setupSpinners() {
        spinnerCredits.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            credits
        )

        spinnerSemester.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            semesters
        )
    }

    private fun saveCourse() {
        val courseName = editCourseName.text.toString().trim()
        val courseCode = editCourseCode.text.toString().trim()
        val instructor = editInstructor.text.toString().trim()
        val schedule = editSchedule.text.toString().trim()
        val room = editRoom.text.toString().trim()

        if (courseName.isEmpty()) {
            editCourseName.error = "Course name required"
            return
        }

        if (courseCode.isEmpty()) {
            editCourseCode.error = "Course code required"
            return
        }

        if (instructor.isEmpty()) {
            editInstructor.error = "Instructor name required"
            return
        }

        setLoading(true)

        val courseId = databaseRef.push().key ?: return

        val course = Course(
            id = courseId,
            courseName = courseName,
            courseCode = courseCode,
            instructor = instructor,
            creditHours = spinnerCredits.selectedItem.toString(),
            schedule = schedule,
            room = room,
            semester = spinnerSemester.selectedItem.toString()
        )

        databaseRef.child(courseId).setValue(course)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSave.isEnabled = !isLoading
        btnCancel.isEnabled = !isLoading
    }
}