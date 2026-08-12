package com.example.rythmcloud.data.remote

import com.example.rythmcloud.data.entities.Song
import retrofit2.http.GET

interface SongApi {
    @GET("songs")
    suspend fun getAllSongs(): List<Song>
}