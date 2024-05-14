package com.serrano.dictproject.datastore

import kotlinx.serialization.Serializable

/**
 * An object used by datastore and store in internal storage
 *
 * @param[authToken] The authorization token of user.
 * @param[id] The id of user.
 * @param[name] The name of user.
 * @param[email] The email of user.
 * @param[password] The un-hashed password of user.
 * @param[image] The image of user.
 */
@Serializable
data class Preferences(
    val authToken: String = "",
    val id: Int = 0,
    val name: String = "Test1234",
    val email: String = "Test1234@test.com",
    val password: String = "Test1234",
    val image: String = ""
)