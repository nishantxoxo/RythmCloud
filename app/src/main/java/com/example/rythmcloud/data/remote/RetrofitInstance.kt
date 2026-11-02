package com.example.rythmcloud.data.remote

import android.content.Context
import com.example.rythmcloud.data.auth.AuthApi
import com.example.rythmcloud.data.auth.RefreshRequest
import com.example.rythmcloud.data.auth.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.126.36.231:5000/"
    private lateinit var tokenStore: TokenStore
    private lateinit var retrofit: Retrofit
    private lateinit var refreshApi: AuthApi

    fun initialize(context: Context) {
        if (::retrofit.isInitialized) return

        tokenStore = TokenStore(context.applicationContext)
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            tokenStore.accessToken
                ?.takeIf { it.isNotBlank() }
                ?.let { requestBuilder.header("Authorization", "Bearer $it") }
            chain.proceed(requestBuilder.build())
        }

        val authenticator = Authenticator { _, response ->
            if (responseCount(response) >= 2) {
                tokenStore.clear()
                return@Authenticator null
            }

            val currentToken = tokenStore.accessToken
            val refreshToken = tokenStore.refreshToken
            if (currentToken == null || refreshToken == null) {
                tokenStore.clear()
                return@Authenticator null
            }

            synchronized(tokenStore) {
                if (tokenStore.accessToken != currentToken) {
                    return@synchronized tokenStore.accessToken?.let {
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $it")
                            .build()
                    }
                }

                val refreshed = runCatching {
                    runBlocking {
                        refreshApi.refresh(RefreshRequest(refreshToken))
                    }
                }.getOrNull()

                if (refreshed == null) {
                    tokenStore.clear()
                    return@synchronized null
                }

                tokenStore.saveSession(refreshed)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshed.accessToken}")
                    .build()
                }
            }

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .authenticator(authenticator)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        refreshApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val api: SongApi
        get() = retrofit.create(SongApi::class.java)

    val authApi: AuthApi
        get() = retrofit.create(AuthApi::class.java)

    fun createTokenStore(context: Context): TokenStore {
        return TokenStore(context.applicationContext)
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}