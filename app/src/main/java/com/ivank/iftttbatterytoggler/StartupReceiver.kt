package com.ivank.iftttbatterytoggler;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import java.time.Duration


class StartupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val batteryWorkerRequest: WorkRequest =
            PeriodicWorkRequestBuilder<BatteryWorker>(Duration.ofMinutes(15))
                .setConstraints(myConstraints)
                .build()
        WorkManager.getInstance(context!!).enqueue(batteryWorkerRequest)
    }
}
