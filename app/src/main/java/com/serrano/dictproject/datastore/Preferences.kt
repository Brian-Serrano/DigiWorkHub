package com.serrano.dictproject.datastore

import kotlinx.serialization.Serializable

@Serializable
data class Preferences(
    val authToken: String = "",
    val id: Int = 0,
    val name: String = "Test1234",
    val email: String = "Test1234@test.com",
    val password: String = "Test1234",
    val image: String = ""
)