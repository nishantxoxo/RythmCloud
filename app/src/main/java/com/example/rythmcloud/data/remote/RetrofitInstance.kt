package com.example.rythmcloud.data.remote

import android.content.Context
import com.example.rythmcloud.data.auth.AuthApi
import com.example.rythmcloud.data.auth.TokenStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.126.36.231:5000/"
    private lateinit var tokenStore: TokenStore
    private lateinit var retrofit: Retrofit

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

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(authInterceptor).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SongApi
        get() = retrofit.create(SongApi::class.java)

    val authApi: AuthApi
        get() = retrofit.create(AuthApi::class.java)

    fun createTokenStore(context: Context): TokenStore {
        return TokenStore(context.applicationContext)
    }
}