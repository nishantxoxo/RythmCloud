package com.example.rythmcloud.player

class BackendMusicSource {

    private val onReadyListners = mutableListOf<(Boolean) -> Unit>()
}



enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}