package com.example.plantdoc.data

import android.content.Context
import com.example.plantdoc.data.entities.disease.DiseaseRepository
import com.example.plantdoc.data.entities.disease.DiseaseRepositoryImpl
import com.example.plantdoc.data.entities.history.HistoryRepository
import com.example.plantdoc.data.entities.history.HistoryRepositoryImpl
import com.example.plantdoc.data.entities.plant.PlantRepository
import com.example.plantdoc.data.entities.plant.PlantRepositoryImpl
import com.example.plantdoc.data.entities.user.UserRepositoryImpl
import com.example.plantdoc.data.entities.user.UserRepository

interface AppContainer {
//    val userRepository: UserRepository
    val plantRepository: PlantRepository
    val diseaseRepository: DiseaseRepository
    val historyRepository: HistoryRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {
//    override val userRepository: UserRepository by lazy {
//        UserRepositoryImpl(PlantDocDatabase.getInstance(context = context).userDao())
//    }

    override val plantRepository: PlantRepository by lazy {
        PlantRepositoryImpl(PlantDocDatabase.getInstance(context = context).plantDao())
    }

    override val diseaseRepository: DiseaseRepository by lazy {
        DiseaseRepositoryImpl(PlantDocDatabase.getInstance(context = context).diseaseDao())
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(PlantDocDatabase.getInstance(context = context).historyDao())
    }
}