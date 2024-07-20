package com.example.plantdoc.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class PeriodicTriggerWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val workManager = WorkManager.getInstance(context)

        val plantsPeriodicWorkRequest = OneTimeWorkRequestBuilder<PlantsDownloadWorker>()
            .build()

        val diseasesPeriodicWorkRequest = OneTimeWorkRequestBuilder<DiseasesDownloadWorker>()
            .build()

//        workManager.enqueueUniquePeriodicWork(
//            "DataDownloadWork",
//            ExistingPeriodicWorkPolicy.KEEP,
//            plantsPeriodicWorkRequest
//        )

        workManager
            .beginUniqueWork("ChainedDataProcessingWork", ExistingWorkPolicy.KEEP, listOf(plantsPeriodicWorkRequest))
            .then(diseasesPeriodicWorkRequest)
            .enqueue()

        return Result.success()
    }
}