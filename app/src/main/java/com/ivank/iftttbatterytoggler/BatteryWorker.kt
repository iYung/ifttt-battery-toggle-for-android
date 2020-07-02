package com.ivank.iftttbatterytoggler

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class BatteryWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val batteryPct = batteryPct()

        batteryPct ?: return Result.success()

        val sharedPref = applicationContext.getSharedPreferences(
            applicationContext.resources.getString(R.string.pref_file_key), Context.MODE_PRIVATE)

        val turnOffWebhook = sharedPref.getString(
            applicationContext.resources.getString(R.string.turn_off_webhook),
            null
        )

        turnOffWebhook ?: return Result.success()

        val turnOnWebhook = sharedPref.getString(
            applicationContext.resources.getString(R.string.turn_on_webhook),
            null
        )

        turnOnWebhook ?: return Result.success()

        val minBatteryLevel = sharedPref.getLong(
            applicationContext.resources.getString(R.string.min_battery_lvl),
            0
        )

        val maxBatteryLevel = sharedPref.getLong(
            applicationContext.resources.getString(R.string.max_battery_lvl),
            100
        )

        if (minBatteryLevel > batteryPct && !isPowered()) {
            callWebhook(turnOnWebhook)
        } else if (maxBatteryLevel < batteryPct && isPowered()) {
            callWebhook(turnOffWebhook)
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun batteryPct(): Long? {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }
        return batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toLong()
        }
    }

    fun isPowered(): Boolean {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }
        val plugged = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
    }

    private fun callWebhook(webhook: String) {
        val queue = Volley.newRequestQueue(applicationContext)
        val stringRequest = StringRequest(
            Request.Method.GET, webhook,
            null,
            null
        )
        queue.add(stringRequest)
    }
}
