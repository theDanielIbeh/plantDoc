package com.example.plantdoc.data.network.responses

import com.google.gson.annotations.Expose

data class GeneralResponse<T>(
    @Expose
    val data: List<T>,

    @Expose
    val message: String
)
