package com.pgmv.bandify.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.pgmv.bandify.api.response.Holiday
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.domain.Event
import com.pgmv.bandify.ui.components.Day
import com.pgmv.bandify.ui.components.DaysOfWeekTitle
import com.pgmv.bandify.ui.components.AgendaEventCard
import com.pgmv.bandify.ui.components.MonthTitle
import com.pgmv.bandify.viewmodel.AgendaViewModel
import com.pgmv.bandify.viewmodel.AgendaViewModelFactory
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendaScreen(dbHelper: DatabaseHelper, navController: NavController) {

    val agendaViewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(dbHelper)
    )

    val events by agendaViewModel.events.collectAsState()

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(24) }
    val selectedDay = remember { mutableStateOf<CalendarDay?>(null) }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek().first()
    )

    val holidays = agendaViewModel.fetchHolidays(state.lastVisibleMonth.yearMonth.year)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CalendarSection(state, selectedDay, holidays, events)
        Button(
            onClick = {
                navController.navigate("novo_evento")
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar evento"
            )
            Text(
                text = "Adicionar evento",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

        if (selectedDay.value != null) {
            val selectedDate = selectedDay.value!!.date
            val dayEvents = events.filter { it.date == selectedDate.toString() }
            val dayHolidays = holidays.filter { it.date == selectedDate.toString() }

            if (dayHolidays.isNotEmpty()) {
                dayHolidays.forEach {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            if (dayEvents.isNotEmpty()) {
                dayEvents.forEach {
                    AgendaEventCard(
                        title = it.title,
                        time = it.time,
                        place = it.place,
                        address = it.address,
                        onButtonClick = {
                            navController.navigate("repertorio?event_id=${it.id}") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            } else {
                Text(
                    text = "Nenhum evento na data selecionada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
                )
            }

        }

    }
}


@Composable
fun CalendarSection(
    state: CalendarState,
    selectedDay: MutableState<CalendarDay?>,
    holidays: List<Holiday>,
    events: List<Event>,
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Calendário de Eventos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight(700),
            color = MaterialTheme.colorScheme.onBackground,
        )


    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 38.dp)
    ) {
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    isSelected = selectedDay.value == day,
                    isHoliday = holidays.any { it.date == day.date.toString() },
                    hasEvent = events.any { it.date == day.date.toString() },
                    onClick = { selectedDay.value = day }
                )
            },
            monthHeader = {
                MonthTitle(month = it)
                DaysOfWeekTitle(daysOfWeek())
            }
        )
    }
    val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
    Text(
        text = selectedDay.value?.date?.format(formatter) ?: "Selecione uma data",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

