package com.example.rythmcloud.data.remote

import com.example.rythmcloud.data.entities.Song


//TODO implemment pagination later
class MusicDatabase {
//    private val firestore = FirebaseFirestore.getInstance()
//    private val songCollection = firestore.collection(SONG_COLLECTION)
        private val api = RetrofitInstance.api

    suspend fun getAllSongs(): List<Song> {
        return try {
            api.getAllSongs()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}