package com.example.usersettingsapp

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var textWelcome: TextView
    private lateinit var editStudentId: EditText
    private lateinit var editFullName: EditText
    private lateinit var spinnerDepartment: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var editEmail: EditText
    private lateinit var btnSaveProfile: Button

    private val departments = listOf("CSE", "EEE", "BBA", "English", "Law")
    private val years = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile Setup"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        setupSpinners()

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun initViews() {
        textWelcome = findViewById(R.id.textWelcome)
        editStudentId = findViewById(R.id.editStudentId)
        editFullName = findViewById(R.id.editFullName)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        spinnerYear = findViewById(R.id.spinnerYear)
        editEmail = findViewById(R.id.editEmail)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
    }

    private fun setupSpinners() {
        val departmentAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            departments
        )
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDepartment.adapter = departmentAdapter

        val yearAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            years
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter
    }

    private fun saveProfile() {
        val studentId = editStudentId.text.toString().trim()
        val fullName = editFullName.text.toString().trim()
        val department = spinnerDepartment.selectedItem.toString()
        val year = spinnerYear.selectedItem.toString()
        val email = editEmail.text.toString().trim()

        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        with(profilePrefs.edit()) {
            putString(PrefKeys.KEY_STUDENT_ID, studentId)
            putString(PrefKeys.KEY_STUDENT_NAME, fullName)
            putString(PrefKeys.KEY_DEPARTMENT, department)
            putString(PrefKeys.KEY_YEAR, year)
            putString(PrefKeys.KEY_EMAIL, email)
            apply()
        }

        updateWelcomeBanner(fullName)
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfile() {
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        val savedName = profilePrefs.getString(PrefKeys.KEY_STUDENT_NAME, "") ?: ""
        val savedId = profilePrefs.getString(PrefKeys.KEY_STUDENT_ID, "") ?: ""
        val savedDepartment = profilePrefs.getString(PrefKeys.KEY_DEPARTMENT, "CSE") ?: "CSE"
        val savedYear = profilePrefs.getString(PrefKeys.KEY_YEAR, "1st Year") ?: "1st Year"
        val savedEmail = profilePrefs.getString(PrefKeys.KEY_EMAIL, "") ?: ""

        editFullName.setText(savedName)
        editStudentId.setText(savedId)
        editEmail.setText(savedEmail)

        val departmentIndex = departments.indexOf(savedDepartment)
        spinnerDepartment.setSelection(if (departmentIndex >= 0) departmentIndex else 0)

        val yearIndex = years.indexOf(savedYear)
        spinnerYear.setSelection(if (yearIndex >= 0) yearIndex else 0)

        updateWelcomeBanner(savedName)
    }

    private fun updateWelcomeBanner(name: String) {
        textWelcome.text =
            if (name.isBlank()) {
                "Welcome back!"
            } else {
                "Welcome back, $name!"
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}