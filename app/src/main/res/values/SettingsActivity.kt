package com.example.gestaoderisco.view

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gestaoderisco.R
import com.example.gestaoderisco.utils.ReportSchedulerUtils
import com.example.gestaoderisco.worker.WeeklyReportWorker
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchNotifications: MaterialSwitch
    private lateinit var switchHighValue: MaterialSwitch
    private lateinit var etEmail: TextInputEditText
    private lateinit var spnDay: Spinner
    private lateinit var btnTime: Button
    private lateinit var btnSave: Button

    private var selectedHour = 8
    private var selectedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switchNotifications = findViewById(R.id.switch_notifications)
        switchHighValue = findViewById(R.id.switch_high_value)
        etEmail = findViewById(R.id.et_coordinator_email)
        spnDay = findViewById(R.id.spn_report_day)
        btnTime = findViewById(R.id.btn_report_time)
        btnSave = findViewById(R.id.btn_save_settings)

        setupDaySpinner()
        loadSettings()
        setupListeners()
    }

    private fun setupDaySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.days_of_week,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnDay.adapter = adapter
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        // Notificações
        switchNotifications.isChecked = prefs.getBoolean("notifications_enabled", true)
        switchHighValue.isChecked = prefs.getBoolean("notifications_high_value_only", false)
        switchHighValue.isEnabled = switchNotifications.isChecked

        // Relatórios
        etEmail.setText(prefs.getString("pp_coordinator_email", ""))
        val day = prefs.getInt("report_day", Calendar.MONDAY)
        spnDay.setSelection(day - 1)
        
        selectedHour = prefs.getInt("report_hour", 8)
        selectedMinute = prefs.getInt("report_minute", 0)
        btnTime.text = String.format("%02d:%02d", selectedHour, selectedMinute)
    }

    private fun setupListeners() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            switchHighValue.isEnabled = isChecked
        }

        btnTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                btnTime.text = String.format("%02d:%02d", hour, minute)
            }, selectedHour, selectedMinute, true).show()
        }

        btnSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedDay = spnDay.selectedItemPosition + 1

        prefs.edit().apply {
            putBoolean("notifications_enabled", switchNotifications.isChecked)
            putBoolean("notifications_high_value_only", switchHighValue.isChecked)
            putString("pp_coordinator_email", etEmail.text.toString())
            putInt("report_day", selectedDay)
            putInt("report_hour", selectedHour)
            putInt("report_minute", selectedMinute)
            apply()
        }

        val delay = ReportSchedulerUtils.calculateInitialDelay(System.currentTimeMillis(), selectedDay, selectedHour, selectedMinute)
        val workRequest = PeriodicWorkRequestBuilder<WeeklyReportWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("weekly_report")
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("WeeklyReportWork", ExistingPeriodicWorkPolicy.REPLACE, workRequest)

        Toast.makeText(this, getString(R.string.configure_report_saved), Toast.LENGTH_SHORT).show()
        finish()
    }
}