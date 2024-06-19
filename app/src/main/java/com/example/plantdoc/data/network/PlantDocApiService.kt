package com.example.plantdoc.data.network

/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.data.entities.history.History
import com.example.plantdoc.data.entities.plant.Plant
import com.example.plantdoc.data.entities.user.User
import com.example.plantdoc.data.network.responses.UsersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service object for creating api calls
 */
interface PlantDocApiService {

//    Users
    @POST("users/create")
    suspend fun insertUser(@Body user: User): Response<UsersResponse>

    @GET("users")
    suspend fun getUsers(): List<User>

//    History
    @POST("history")
    suspend fun insertHistory(@Body history: History)

    @POST("history")
    suspend fun getHistory(
        @Query("user_id")
        userID: String,
    ): List<History>

//    Plants
    @GET("plants")
    suspend fun getPlants(): List<Plant>

//    Diseases
    @GET("diseases")
    suspend fun getDiseases(): List<Disease>
}