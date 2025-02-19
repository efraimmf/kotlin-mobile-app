package com.pgmv.bandify.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pgmv.bandify.api.MusicApi
import com.pgmv.bandify.api.RetrofitHelper
import com.pgmv.bandify.api.response.Recording
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.domain.Song
import com.pgmv.bandify.utils.formatDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NovaMusicaViewModel(dbHelper: DatabaseHelper? = null): ViewModel() {
    private val songDao = dbHelper?.songDao()
    private val retrofitHelper = RetrofitHelper.getInstance()
    private val musicService = retrofitHelper.musicApi(MusicApi::class.java)

    var songAdded = mutableStateOf(false)
    var songTitle = mutableStateOf("")
    var artist = mutableStateOf("")
    var duration = mutableStateOf("")
    var selectedTag = mutableStateOf("")
    var selectedTempo = mutableStateOf("")
    var selectedKey = mutableStateOf("")
    var searchResults = mutableStateOf<List<Recording>>(emptyList())
    private var debounceJob: kotlinx.coroutines.Job? = null

    val tagOptions = listOf("Ensaio", "Prontas")
    val keyOptions = listOf(
        "C", "C#", "D", "D#", "E", "F",
        "F#", "G", "G#", "A", "A#", "B",
        "Cm", "C#m", "Dm", "D#m", "Em", "Fm",
        "F#m", "Gm", "G#m", "Am", "A#m", "Bm",
        "Db", "Ebm", "Gb", "Ab", "Bb",
        "Dbm", "Ebm", "Gbm", "Abm", "Bbm"
    )


    fun updateSongTitle(value: String) {
        songTitle.value = value
    }

    fun updateArtist(value: String) {
        artist.value = value
    }

    fun updateSelectedTag(value: String) {
        selectedTag.value = value
    }

    fun updateSelectedTempo(value: String) {
        selectedTempo.value = value
    }

    fun updateSelectedKey(value: String) {
        selectedKey.value = value
    }

    fun searchSong(query: String) {
        if (query.isBlank()) {
            searchResults.value = emptyList()
            return
        }

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(500)

            try {
                val response = musicService.searchSong(query)
                println("Search API Response: ${response.data}")

                if (response.data.isNotEmpty()) {
                    searchResults.value = response.data
                } else {
                    println("Nenhum resultado encontrado")
                    searchResults.value = emptyList()
                }

            } catch (e: Exception) {
                println("Erro ao buscar músicas: ${e.message}")
                searchResults.value = emptyList()
            }
        }
    }

    fun selectSearchResult(recording: Recording) {
        songTitle.value = recording.title
        artist.value = recording.artist.name
        duration.value = recording.duration.toString()
        searchResults.value = emptyList()
    }

    fun saveSong() {
        val newSong = Song(
            title = songTitle.value,
            artist = artist.value,
            tag = selectedTag.value,
            tempo = selectedTempo.value.toInt(),
            key = selectedKey.value,
            userId = 1L,
            duration = formatDuration(duration.value.toInt())
        )
        viewModelScope.launch {
            try {
                songDao?.insertSong(newSong)
                songAdded.value = true
            }catch (e: Exception) {
                songAdded.value = false
            }
        }
    }
}

class NovaMusicaViewModelFactory(private val dbHelper: DatabaseHelper?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NovaMusicaViewModel::class.java)) {
            return NovaMusicaViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}