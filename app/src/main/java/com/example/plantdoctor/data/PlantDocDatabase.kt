package com.example.plantdoctor.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.plantdoctor.data.entities.disease.Disease
import com.example.plantdoctor.data.entities.disease.DiseaseDao
import com.example.plantdoctor.data.entities.history.History
import com.example.plantdoctor.data.entities.history.HistoryDao
import com.example.plantdoctor.data.entities.plant.Plant
import com.example.plantdoctor.data.entities.plant.PlantDao
import com.example.plantdoctor.data.entities.user.User
import com.example.plantdoctor.data.entities.user.UserDao

@Database(
    entities = [
        User::class,
        Plant::class,
        Disease::class,
        History::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PlantDocDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun plantDao(): PlantDao
    abstract fun diseaseDao(): DiseaseDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDocDatabase? = null

        fun getInstance(context: Context): PlantDocDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlantDocDatabase::class.java,
                        "plantdoc_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}