package com.serrano.dictproject.api

import com.google.gson.Gson
import com.serrano.dictproject.utils.ClientErrorObj
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.ServerErrorObj
import retrofit2.HttpException
import retrofit2.Response

class ApiHandler {

    suspend fun <T : Any> handleApi(execute: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = execute()
            when (response.code()) {
                in 200..299 -> {
                    Resource.Success(response.body()!!)
                }
                in 400..499 -> {
                    val jsonObj = Gson().fromJson(response.errorBody()?.charStream(), ClientErrorObj::class.java)
                    Resource.ClientError(jsonObj)
                }
                in 500..599 -> {
                    val jsonObj = Gson().fromJson(response.errorBody()?.charStream(), ServerErrorObj::class.java)
                    Resource.ServerError(jsonObj)
                }
                else -> Resource.GenericError(response.errorBody()?.string() ?: "")
            }
        } catch (e: Throwable) {
            when (e) {
                is HttpException -> {
                    when(e.code()) {
                        in 400..499 -> {
                            val jsonObj = Gson().fromJson(e.response()?.errorBody()?.charStream(), ClientErrorObj::class.java)
                            Resource.ClientError(jsonObj)
                        }
                        in 500..599 -> {
                            val jsonObj = Gson().fromJson(e.response()?.errorBody()?.charStream(), ServerErrorObj::class.java)
                            Resource.ServerError(jsonObj)
                        }
                        else -> Resource.GenericError(e.message ?: "")
                    }
                }
                else -> Resource.GenericError(e.message ?: "")
            }
        }
    }
}