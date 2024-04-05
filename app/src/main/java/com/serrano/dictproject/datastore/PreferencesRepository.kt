package com.serrano.dictproject.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>
) {

    suspend fun login(authToken: String, id: Int, name: String, email: String, password: String, image: String) {
        preferencesDataStore.updateData {
            it.copy(
                authToken = authToken,
                id = id,
                name = name,
                email = email,
                password = password,
                image = image
            )
        }
    }

    suspend fun logout() {
        preferencesDataStore.updateData {
            Preferences()
        }
    }

    suspend fun updateAuthToken(authToken: String) {
        preferencesDataStore.updateData {
            it.copy(authToken = authToken)
        }
    }

    suspend fun changeName(name: String) {
        preferencesDataStore.updateData {
            it.copy(name = name)
        }
    }

    suspend fun changeImage(image: String) {
        preferencesDataStore.updateData {
            it.copy(image = image)
        }
    }

    fun getData(): Flow<Preferences> {
        return preferencesDataStore.data
    }
}