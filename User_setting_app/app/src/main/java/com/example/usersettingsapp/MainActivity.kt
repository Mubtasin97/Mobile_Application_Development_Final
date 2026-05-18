package com.example.usersettingsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var editStudentName: EditText
    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var rbLight: RadioButton
    private lateinit var rbDark: RadioButton
    private lateinit var rbSystem: RadioButton
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var spinnerLanguage: Spinner
    private lateinit var textFontSize: TextView
    private lateinit var seekBarFontSize: SeekBar
    private lateinit var btnSaveSettings: Button
    private lateinit var btnReset: Button
    private lateinit var btnViewSaved: Button
    private lateinit var fabProfile: FloatingActionButton

    private val languages = listOf("English", "Bangla", "Arabic", "French")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Student Portal Settings"

        initViews()
        setupLanguageSpinner()
        setupFontSeekBar()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        restoreSavedValues()
    }

    private fun initViews() {
        editStudentName = findViewById(R.id.editStudentName)
        radioGroupTheme = findViewById(R.id.radioGroupTheme)
        rbLight = findViewById(R.id.rbLight)
        rbDark = findViewById(R.id.rbDark)
        rbSystem = findViewById(R.id.rbSystem)
        switchNotifications = findViewById(R.id.switchNotifications)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        textFontSize = findViewById(R.id.textFontSize)
        seekBarFontSize = findViewById(R.id.seekBarFontSize)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
        btnReset = findViewById(R.id.btnReset)
        btnViewSaved = findViewById(R.id.btnViewSaved)
        fabProfile = findViewById(R.id.fabProfile)
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languages
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter
    }

    private fun setupFontSeekBar() {
        seekBarFontSize.max = 12
        seekBarFontSize.progress = 4
        updateFontSizeText(16)

        seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val fontSize = progress + 12
                updateFontSizeText(fontSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun setupButtons() {
        btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        btnReset.setOnClickListener {
            resetPreferences()
        }

        btnViewSaved.setOnClickListener {
            startActivity(Intent(this, SettingsViewerActivity::class.java))
        }

        fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun saveSettings() {
        val selectedTheme = when (radioGroupTheme.checkedRadioButtonId) {
            R.id.rbDark -> "dark"
            R.id.rbSystem -> "system"
            else -> "light"
        }

        val selectedLanguage = spinnerLanguage.selectedItem.toString()
        val selectedFontSize = seekBarFontSize.progress + 12
        val studentName = editStudentName.text.toString().trim()

        val appSettings = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        with(appSettings.edit()) {
            putString(PrefKeys.KEY_THEME, selectedTheme)
            putBoolean(PrefKeys.KEY_NOTIFICATIONS, switchNotifications.isChecked)
            putString(PrefKeys.KEY_LANGUAGE, selectedLanguage)
            putInt(PrefKeys.KEY_FONT_SIZE, selectedFontSize)
            putLong(PrefKeys.KEY_LAST_SAVED, System.currentTimeMillis())
            apply()
        }

        with(profilePrefs.edit()) {
            putString(PrefKeys.KEY_STUDENT_NAME, studentName)
            apply()
        }

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun restoreSavedValues() {
        val appSettings = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        editStudentName.setText(
            profilePrefs.getString(PrefKeys.KEY_STUDENT_NAME, "") ?: ""
        )

        when (appSettings.getString(PrefKeys.KEY_THEME, "light")) {
            "dark" -> rbDark.isChecked = true
            "system" -> rbSystem.isChecked = true
            else -> rbLight.isChecked = true
        }

        switchNotifications.isChecked =
            appSettings.getBoolean(PrefKeys.KEY_NOTIFICATIONS, true)

        val savedLanguage = appSettings.getString(PrefKeys.KEY_LANGUAGE, "English") ?: "English"
        val languageIndex = languages.indexOf(savedLanguage)
        spinnerLanguage.setSelection(if (languageIndex >= 0) languageIndex else 0)

        val savedFontSize = appSettings.getInt(PrefKeys.KEY_FONT_SIZE, 16)
        seekBarFontSize.progress = savedFontSize - 12
        updateFontSizeText(savedFontSize)
    }

    private fun resetPreferences() {
        val appSettings = getSharedPreferences(PrefFiles.APP_SETTINGS, Context.MODE_PRIVATE)
        val profilePrefs = getSharedPreferences(PrefFiles.PROFILE_PREFS, Context.MODE_PRIVATE)

        appSettings.edit().clear().apply()
        profilePrefs.edit().clear().apply()

        editStudentName.setText("")
        rbLight.isChecked = true
        switchNotifications.isChecked = true
        spinnerLanguage.setSelection(0)
        seekBarFontSize.progress = 4
        updateFontSizeText(16)

        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
    }

    private fun updateFontSizeText(fontSize: Int) {
        textFontSize.text = "Font Size: ${fontSize}sp"
    }
}