package com.pgmv.bandify.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.domain.ActivityHistory
import com.pgmv.bandify.domain.Event
import com.pgmv.bandify.domain.EventSong
import com.pgmv.bandify.domain.Song
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class EventViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {

    var userId = 1L
    val eventDao = dbHelper.eventDao()
    val eventSongDao = dbHelper.eventSongDao()
    val activityHistoryDao = dbHelper.activityHistoryDao()

    var selectedDate = mutableStateOf(LocalDate.now())
    var selectedTime = mutableStateOf("Escolha um horário")
    val selectedSongs = mutableStateOf(setOf<Song>())
    var title = mutableStateOf("")
    var place = mutableStateOf("")
    var address = mutableStateOf("")
    var titleError = mutableStateOf<String?>(null)
    var placeError = mutableStateOf<String?>(null)
    var addressError = mutableStateOf<String?>(null)
    var timeError = mutableStateOf<String?>(null)

    val saveEventResult = mutableStateOf<Boolean?>(null)


    fun updateSelectedDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun updateSelectedTime(time: String) {
        selectedTime.value = time
        if (time.isBlank() || time == "Escolha um horário") {
            timeError.value = "O horário é obrigatório"
        } else {
            timeError.value = null
        }
    }

    fun updateSelectedSongs(songs: Set<Song>) {
        selectedSongs.value = songs
    }

    fun updateTitle(title: String) {
        this.title.value = title
        if (title.isBlank()) {
            titleError.value = "Título não pode ficar em branco"
        } else {
            titleError.value = null
        }
    }

    fun updatePlace(place: String) {
        this.place.value = place
        if (place.isBlank()) {
            placeError.value = "Local não pode ficar em branco"
        } else {
            placeError.value = null
        }
    }

    fun updateAddress(address: String) {
        this.address.value = address
        if (address.isBlank()) {
            addressError.value = "Endereço não pode ficar em branco"
        } else {
            addressError.value = null
        }
    }

    fun validateAllFields(): Boolean {
        updateSelectedDate(selectedDate.value)
        updateSelectedTime(selectedTime.value)
        updateSelectedSongs(selectedSongs.value)
        updateTitle(title.value)
        updatePlace(place.value)
        updateAddress(address.value)
        return titleError.value == null && placeError.value == null && addressError.value == null && timeError.value == null
    }

    fun resetFields() {
        title.value = ""
        place.value = ""
        address.value = ""
        selectedDate.value = LocalDate.now()
        selectedTime.value = "Escolha um horário"
        selectedSongs.value = setOf()
        titleError.value = null
        placeError.value = null
        addressError.value = null
        timeError.value = null
    }

     fun saveEvent() {
       viewModelScope.launch {
            try {
                val eventId = eventDao.insertEvent(
                    Event(
                        title = title.value,
                        place = place.value,
                        address = address.value,
                        date = selectedDate.value.toString(),
                        time = selectedTime.value,
                        userId = userId,
                    )
                )
                activityHistoryDao.insertActivity(
                    ActivityHistory(
                        userId = userId,
                        activity = "Um novo evento foi adicionado",
                        activityType = "evento",
                        createdAt = LocalDateTime.now(ZoneId.systemDefault()).toString(),
                        eventId = eventId,
                        itemName = title.value
                    )
                )
                if (selectedSongs.value.isNotEmpty()) {
                    selectedSongs.value.forEach { song ->
                        eventSongDao.insertEventSong(EventSong(eventId = eventId, songId = song.id))
                    }
                }
                saveEventResult.value = true
                resetFields()
            } catch (
                e: Exception
            ) {
                saveEventResult.value = false
                Log.e("EventViewModel", "Error saving event: ${e.message}")
            }
        }
    }

}

class EventViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}