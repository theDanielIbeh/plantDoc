package com.example.plantdoctor.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.plantdoctor.workers.PeriodicTriggerWorker
import java.util.concurrent.TimeUnit

class WorkManagerRepository(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun download() {

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<PeriodicTriggerWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "PeriodicTriggerWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        workManager.enqueueUniquePeriodicWork(
            "PeriodicTriggerWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}