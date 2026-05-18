package com.example.usersettingsapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewerActivity : AppCompatActivity() {

    private lateinit var rowsContainer: LinearLayout
    private lateinit var textEmptyMessage: TextView
    private lateinit var btnBack: Button
    private lateinit var btnEdit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_viewer)

        supportActionBar?.title = "Saved Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rowsContainer = findViewById(R.id.rowsContainer)
        textEmptyMessage = findViewById(R.id.textEmptyMessage)
        btnBack = findViewById(R.id.btnBack)
        btnEdit = findViewById(R.id.btnEdit)

        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        loadSavedSettings()
    }

    private fun loadSavedSettings() {
        val appSettings = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        rowsContainer.removeAllViews()

        val hasSettings =
            appSettings.contains(PrefKeys.KEY_LAST_SAVED) || profilePrefs.all.isNotEmpty()

        if (!hasSettings) {
            textEmptyMessage.visibility = View.VISIBLE
            rowsContainer.addView(textEmptyMessage)
            return
        }

        textEmptyMessage.visibility = View.GONE

        addSettingRow("Student Name", profilePrefs.getString(PrefKeys.KEY_STUDENT_NAME, "Not saved") ?: "Not saved")
        addSettingRow("Student ID", profilePrefs.getString(PrefKeys.KEY_STUDENT_ID, "Not saved") ?: "Not saved")
        addSettingRow("Department", profilePrefs.getString(PrefKeys.KEY_DEPARTMENT, "Not saved") ?: "Not saved")
        addSettingRow("Year of Study", profilePrefs.getString(PrefKeys.KEY_YEAR, "Not saved") ?: "Not saved")
        addSettingRow("Email", profilePrefs.getString(PrefKeys.KEY_EMAIL, "Not saved") ?: "Not saved")

        val themeValue = when (appSettings.getString(PrefKeys.KEY_THEME, "light")) {
            "dark" -> "Dark"
            "system" -> "System Default"
            else -> "Light"
        }

        val notificationsValue =
            if (appSettings.getBoolean(PrefKeys.KEY_NOTIFICATIONS, true)) {
                "Enabled"
            } else {
                "Disabled"
            }

        addSettingRow("Theme", themeValue)
        addSettingRow("Notifications", notificationsValue)
        addSettingRow("Language", appSettings.getString(PrefKeys.KEY_LANGUAGE, "English") ?: "English")
        addSettingRow("Font Size", "${appSettings.getInt(PrefKeys.KEY_FONT_SIZE, 16)}sp")

        val lastSaved = appSettings.getLong(PrefKeys.KEY_LAST_SAVED, 0L)
        val lastSavedText =
            if (lastSaved == 0L) {
                "Not saved"
            } else {
                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(lastSaved))
            }

        addSettingRow("Last Saved", lastSavedText)
    }

    private fun addSettingRow(label: String, value: String) {
        val cardView = CardView(this)
        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.setMargins(0, 0, 0, dpToPx(12))
        cardView.layoutParams = cardParams
        cardView.radius = dpToPx(10).toFloat()
        cardView.cardElevation = dpToPx(3).toFloat()
        cardView.setContentPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14))

        val rowLayout = LinearLayout(this)
        rowLayout.orientation = LinearLayout.VERTICAL

        val labelText = TextView(this)
        labelText.text = label
        labelText.textSize = 15f
        labelText.setTypeface(null, Typeface.BOLD)
        labelText.setTextColor(android.graphics.Color.parseColor("#1E3A5F"))

        val valueText = TextView(this)
        valueText.text = value
        valueText.textSize = 17f
        valueText.setTextColor(android.graphics.Color.parseColor("#333333"))
        valueText.setPadding(0, dpToPx(4), 0, 0)

        rowLayout.addView(labelText)
        rowLayout.addView(valueText)
        cardView.addView(rowLayout)
        rowsContainer.addView(cardView)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}