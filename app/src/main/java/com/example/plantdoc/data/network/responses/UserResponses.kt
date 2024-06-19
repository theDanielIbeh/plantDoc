package com.example.plantdoc.data.network.responses

import com.example.plantdoc.data.entities.user.User
import com.google.gson.annotations.Expose

data class UsersResponse(
    @Expose
    val data: List<User>,

    @Expose
    val message: String
)
