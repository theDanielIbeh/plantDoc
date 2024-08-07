package com.example.plantdoctor.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.plantdoctor.data.entities.disease.DiseaseDao
import com.example.plantdoctor.data.entities.disease.DiseaseRepository
import com.example.plantdoctor.data.entities.disease.DiseaseRepositoryImpl
import com.example.plantdoctor.data.entities.history.HistoryDao
import com.example.plantdoctor.data.entities.history.HistoryRepository
import com.example.plantdoctor.data.entities.history.HistoryRepositoryImpl
import com.example.plantdoctor.data.entities.plant.PlantDao
import com.example.plantdoctor.data.entities.plant.PlantRepository
import com.example.plantdoctor.data.entities.plant.PlantRepositoryImpl
import com.example.plantdoctor.data.entities.user.UserRepositoryImpl
import com.example.plantdoctor.data.entities.user.UserDao
import com.example.plantdoctor.data.entities.user.UserRepository
import com.example.plantdoctor.data.network.PlantDocApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


const val PLANT_DOC_PREFERENCE_NAME = "plantdoc_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PLANT_DOC_PREFERENCE_NAME
)

@Module
@InstallIn(SingletonComponent::class)
abstract class AppRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindPlantRepository(plantRepository: PlantRepositoryImpl): PlantRepository

    @Singleton
    @Binds
    abstract fun bindDiseaseRepository(diseaseRepository: DiseaseRepositoryImpl): DiseaseRepository

    @Singleton
    @Binds
    abstract fun bindHistoryRepository(historyRepository: HistoryRepositoryImpl): HistoryRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://10.0.2.2:5000/"
    // Setup logging using OkHttp
    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(logger)
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .callTimeout(5, TimeUnit.MINUTES)
//    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient.build())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): PlantDocApiService {
        return retrofit.create(PlantDocApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providePlantDocDatabase(@ApplicationContext context: Context): PlantDocDatabase {
        return PlantDocDatabase.getInstance(context)
    }

    @Provides
    fun provideUserDao(database: PlantDocDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun providePlantDao(database: PlantDocDatabase): PlantDao {
        return database.plantDao()
    }

    @Provides
    fun provideDiseaseDao(database: PlantDocDatabase): DiseaseDao {
        return database.diseaseDao()
    }

    @Provides
    fun provideHistoryDao(database: PlantDocDatabase): HistoryDao {
        return database.historyDao()
    }

    @Singleton
    @Provides
    fun providePreferenceRepository(context: Context): PlantDocPreferencesRepository {
        val plantDocPreferencesRepository = PlantDocPreferencesRepository(context.dataStore)
        return plantDocPreferencesRepository
    }

    @Singleton
    @Provides
    fun provideWorkRepository(context: Context): WorkManagerRepository {
        val workRepository = WorkManagerRepository(context)
        return workRepository
    }
}