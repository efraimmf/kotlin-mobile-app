package com.pgmv.bandify.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.ui.components.HomeActivityCard
import com.pgmv.bandify.ui.components.HomeEventCard
import com.pgmv.bandify.ui.theme.Grey60
import java.time.LocalDate

@Composable
fun HomeScreen(dbHelper: DatabaseHelper? = null, navController: NavController) {
    val eventDao = dbHelper?.eventDao()
    val activityHistoryDao = dbHelper?.activityHistoryDao()
    val nextFiveEvents = eventDao?.getNextFiveEvents(LocalDate.now().toString(), 1)
        ?.collectAsState(initial = emptyList())
    val recentActivities =
        activityHistoryDao?.getRecentActivitiesByUserId(1)?.collectAsState(initial = emptyList())

    val parentHeight = LocalConfiguration.current.screenHeightDp.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Próximos eventos",
                style = MaterialTheme.typography.titleLarge
            )
            
            TextButton(
                onClick = {
                    navController.navigate("agenda") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) {
                Text(
                    text = "Ir para agenda",
                )
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(parentHeight.div(2)),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (nextFiveEvents != null) {
                    if (nextFiveEvents.value.isEmpty()) {
                        item {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "Nenhum evento encontrado",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Grey60
                            )
                        }
                    } else {
                        items(nextFiveEvents.value) { event ->
                            HomeEventCard(event = event, onSongsIconClick = {
                                navController.navigate("repertorio?event_id=${event.id}") {
                                    popUpTo("home") { inclusive = true }
                                    launchSingleTop = true
                                }
                            })
                            Spacer(modifier = Modifier.padding(4.dp))
                        }
                    }
                } else {
                    item {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = "Carregando...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Grey60
                        )
                    }
                }

            }

        }


        Text(
            text = "Atividades Recentes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (recentActivities != null) {
                if (recentActivities.value.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = "Nenhuma atividade encontrada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Grey60
                        )
                    }
                } else {
                    items(recentActivities.value) { activity ->
                        HomeActivityCard(
                            activity = activity,
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            } else {
                item {

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Carregando...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Grey60
                    )
                }
            }
        }
    }
}

