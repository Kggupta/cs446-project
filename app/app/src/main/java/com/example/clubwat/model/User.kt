package com.example.clubwat.model

import androidx.compose.runtime.MutableState

data class User(
    var userId: String?,
    val firstName: MutableState<String>,
    val lastName: MutableState<String>,
    val email: MutableState<String>,
    val password: MutableState<String>,
)
