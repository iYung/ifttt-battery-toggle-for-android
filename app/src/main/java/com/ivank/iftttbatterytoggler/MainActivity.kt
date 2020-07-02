package com.ivank.iftttbatterytoggler

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.core.widget.addTextChangedListener
import androidx.work.ListenableWorker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = applicationContext.getSharedPreferences(
            applicationContext.resources.getString(R.string.pref_file_key), Context.MODE_PRIVATE)

        val turnOffWebhook = sharedPref.getString(
            applicationContext.resources.getString(R.string.turn_off_webhook),
            null
        )

        val turnOnWebhook = sharedPref.getString(
            applicationContext.resources.getString(R.string.turn_on_webhook),
            null
        )

        val minBatteryLevel = sharedPref.getLong(
            applicationContext.resources.getString(R.string.min_battery_lvl),
            0
        )

        val maxBatteryLevel = sharedPref.getLong(
            applicationContext.resources.getString(R.string.max_battery_lvl),
            100
        )

        maxBattery.setText(maxBatteryLevel.toString())
        minBattery.setText(minBatteryLevel.toString())
        turnOnWebhook?.let {
            onWebhook.setText(it)
        }
        turnOffWebhook?.let {
            offWebhook.setText(it)
        }

        maxBattery.addTextChangedListener {
            if (it.toString().isEmpty()) {
                return@addTextChangedListener
            }
            with (sharedPref.edit()) {
                putLong(getString(R.string.max_battery_lvl), it.toString().toLong())
                apply()
            }
        }

        minBattery.addTextChangedListener {
            if (it.toString().isEmpty()) {
                return@addTextChangedListener
            }
            with (sharedPref.edit()) {
                putLong(getString(R.string.min_battery_lvl), it.toString().toLong())
                apply()
            }
        }

        offWebhook.addTextChangedListener {
            with (sharedPref.edit()) {
                putString(getString(R.string.turn_off_webhook), it.toString())
                apply()
            }
        }

        onWebhook.addTextChangedListener {
            with (sharedPref.edit()) {
                putString(getString(R.string.turn_on_webhook), it.toString())
                apply()
            }
        }
    }
}