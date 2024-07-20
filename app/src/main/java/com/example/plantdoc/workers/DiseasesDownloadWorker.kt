package com.example.plantdoc.workers

import android.content.Context
import androidx.work.WorkerParameters
import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.data.network.responses.GeneralResponse
import com.example.plantdoc.utils.DownloadWorker
import retrofit2.Response

class DiseasesDownloadWorker(
    ctx: Context,
    params: WorkerParameters
) :
    DownloadWorker<Disease>(ctx, params) {

    override suspend fun makeCall(): Response<GeneralResponse<Disease>> {
        return apiService.getDiseases()
    }

    override suspend fun insertData(data: List<Disease>) {
        diseaseRepository.insert(data)
    }
}