package com.pgmv.bandify.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pgmv.bandify.api.HolidaysApi
import com.pgmv.bandify.api.RetrofitHelper
import com.pgmv.bandify.api.response.Holiday
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.domain.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgendaViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList<Event>())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _holidays = mutableStateOf<List<Holiday>>(emptyList())
    val holidays: State<List<Holiday>> = _holidays


    private val retrofitHelper = RetrofitHelper.getInstance()
    private val holidaysService = retrofitHelper.holidaysApi(HolidaysApi::class.java)


    init {
        loadUserEvents(userId = 1L)
    }



    private fun loadUserEvents(userId: Long) {
        viewModelScope.launch {
            dbHelper.eventDao().getEventsByUserId(userId).collect { events ->
                _events.value = events
            }
        }
    }
    public fun fetchHolidays(year: Int): List<Holiday> {
        viewModelScope.launch {
            try {
                val response = holidaysService.getHolidays(year)
                Log.d("Holidays","Search API Response: $response")

                if (response.isNotEmpty()) {
                    _holidays.value = response
                } else {
                    Log.d("Holidays","No holiday found")
                    _holidays.value = emptyList()
                }

            } catch (e: Exception) {
                Log.d("Holidays","Erro ao buscar feriados: ${e.message}")
                _holidays.value = emptyList()
            }
        }
        return _holidays.value
    }

}

class AgendaViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
            return AgendaViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}