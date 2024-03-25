package com.serrano.dictproject.api

import android.content.Context
import com.serrano.dictproject.activity.preferencesDataStore
import com.serrano.dictproject.utils.Unauthorized
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class AuthInterceptor(
    private val context: Context,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val invocation = chain.request().tag(Invocation::class.java)
            ?: return chain.proceed(chain.request())

        val shouldNotAttachAuthHeader = invocation
            .method()
            .annotations
            .any { it.annotationClass == Unauthorized::class }

        return if (shouldNotAttachAuthHeader) {
            chain.proceed(chain.request())
        } else {
            runBlocking {
                val token = context.preferencesDataStore.data.first().authToken
                chain.proceed(chain.request().newBuilder().addHeader("Authorization", token).build())
            }
        }
    }
}