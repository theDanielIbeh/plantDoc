package com.example.plantdoc.workers

import android.content.Context
import androidx.work.WorkerParameters
import com.example.plantdoc.data.entities.plant.Plant
import com.example.plantdoc.data.network.responses.GeneralResponse
import com.example.plantdoc.utils.DownloadWorker
import retrofit2.Response


private const val TAG = "PlantsDownloadWorker"

class PlantsDownloadWorker(
    ctx: Context,
    params: WorkerParameters
) :
    DownloadWorker<Plant>(ctx, params) {

    override suspend fun makeCall(): Response<GeneralResponse<Plant>> {
        return apiService.getPlants()
    }

    override suspend fun insertData(data: List<Plant>) {
        plantRepository.insert(data)
    }
}