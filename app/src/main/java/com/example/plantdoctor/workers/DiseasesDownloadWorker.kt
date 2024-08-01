package com.example.plantdoctor.workers

import android.content.Context
import androidx.work.WorkerParameters
import com.example.plantdoctor.data.entities.disease.Disease
import com.example.plantdoctor.data.network.responses.GeneralResponse
import com.example.plantdoctor.utils.DownloadWorker
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