package com.example.rythmcloud.data.entities

import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("mediaId") val mediaId: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("songUrl") val songUri: String,
    @SerializedName("imageUrl") val imageUri: String
)