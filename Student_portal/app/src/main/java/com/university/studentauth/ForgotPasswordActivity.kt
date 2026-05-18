package com.university.studentauth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editEmail: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var btnReset: Button
    private lateinit var textBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        editEmail = findViewById(R.id.editEmail)
        progressBar = findViewById(R.id.progressBar)
        btnReset = findViewById(R.id.btnReset)
        textBack = findViewById(R.id.textBack)

        textBack.setOnClickListener {
            finish()
        }

        btnReset.setOnClickListener {
            sendResetEmail()
        }
    }

    private fun sendResetEmail() {
        val email = editEmail.text.toString().trim()

        if (email.isEmpty()) {
            showMessage("Email is required")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage("Enter a valid email address")
            return
        }

        setLoading(true)

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                setLoading(false)

                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    showMessage(task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnReset.isEnabled = !isLoading
    }

    private fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
}