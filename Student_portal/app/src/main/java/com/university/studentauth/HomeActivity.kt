package com.university.studentauth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textAvatar: TextView
    private lateinit var textEmail: TextView
    private lateinit var textUid: TextView
    private lateinit var textCreated: TextView
    private lateinit var editNewPassword: EditText
    private lateinit var editConfirmPassword: EditText
    private lateinit var btnUpdatePassword: Button
    private lateinit var btnLogout: Button
    private lateinit var btnDeleteAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_home)

        textAvatar = findViewById(R.id.textAvatar)
        textEmail = findViewById(R.id.textEmail)
        textUid = findViewById(R.id.textUid)
        textCreated = findViewById(R.id.textCreated)
        editNewPassword = findViewById(R.id.editNewPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword)
        btnLogout = findViewById(R.id.btnLogout)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)

        loadUserInfo()

        btnUpdatePassword.setOnClickListener {
            updatePassword()
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }

        btnDeleteAccount.setOnClickListener {
            confirmDeleteAccount()
        }
    }

    private fun loadUserInfo() {
        val user = auth.currentUser ?: return
        val email = user.email ?: "No email"
        val uidShort = user.uid.take(8)

        val createdTime = user.metadata?.creationTimestamp ?: 0L
        val createdDate = if (createdTime > 0L) {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(createdTime))
        } else {
            "Not available"
        }

        textAvatar.text = email.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
        textEmail.text = "Email: $email"
        textUid.text = "UID: $uidShort"
        textCreated.text = "Account Created: $createdDate"
    }

    private fun updatePassword() {
        val newPassword = editNewPassword.text.toString().trim()
        val confirmPassword = editConfirmPassword.text.toString().trim()

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Both password fields are required")
            return
        }

        if (newPassword.length < 8) {
            showMessage("Password must be at least 8 characters")
            return
        }

        if (newPassword != confirmPassword) {
            showMessage("Passwords do not match")
            return
        }

        auth.currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    editNewPassword.text.clear()
                    editConfirmPassword.text.clear()
                    showMessage("Password updated successfully")
                } else {
                    showMessage(task.exception?.message ?: "Password update failed")
                }
            }
    }

    private fun logoutUser() {
        auth.signOut()
        goToLogin()
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete this account?")
            .setPositiveButton("Delete") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAccount() {
        auth.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToLogin()
                } else {
                    showMessage(task.exception?.message ?: "Account delete failed")
                }
            }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
}