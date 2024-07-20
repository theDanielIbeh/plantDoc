package com.example.plantdoc.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantdoc.data.PlantDocDatabase
import com.example.plantdoc.data.PlantDocPreferencesRepository
import com.example.plantdoc.data.dataStore
import com.example.plantdoc.data.entities.disease.DiseaseRepositoryImpl
import com.example.plantdoc.data.entities.history.HistoryRepositoryImpl
import com.example.plantdoc.data.entities.plant.PlantRepositoryImpl
import com.example.plantdoc.data.network.PlantDocApiService
import com.example.plantdoc.data.network.responses.GeneralResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "DownloadWorker"

abstract class DownloadWorker<T>(
    ctx: Context,
    params: WorkerParameters
) :
    CoroutineWorker(appContext = ctx, params = params) {
    private val baseUrl = "http://10.0.2.2:5000/"
    // Setup logging using OkHttp
    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(logger)
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .callTimeout(5, TimeUnit.MINUTES)

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient.build())
        .build()

    val apiService: PlantDocApiService = retrofit.create(PlantDocApiService::class.java)

    val database = PlantDocDatabase.getInstance(ctx)
    val preferencesRepository = PlantDocPreferencesRepository(ctx.dataStore)
    val plantRepository = PlantRepositoryImpl(database.plantDao())
    val diseaseRepository = DiseaseRepositoryImpl(database.diseaseDao())
    val historyRepository = HistoryRepositoryImpl(database.historyDao(), apiService)
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {
            return@withContext try {

                val response: Response<GeneralResponse<T>> = makeCall()
                if (response.isSuccessful) {
                    // Process the data and save it to the database
                    response.body()?.data?.let { insertData(it) }
                    Log.d(TAG, response.body().toString())
                    Result.success()
                } else {
                    Result.retry()
                }
//                Result.success()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                Result.failure()
            }
        }
    }

    abstract suspend fun makeCall(): Response<GeneralResponse<T>>
    abstract suspend fun insertData(data: List<T>)
}