package com.example.rythmcloud.data.remote

import com.example.rythmcloud.data.entities.Song
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApi {
    @GET("songs")
    suspend fun getAllSongs(@Query("query") query: String? = null): List<Song>
}