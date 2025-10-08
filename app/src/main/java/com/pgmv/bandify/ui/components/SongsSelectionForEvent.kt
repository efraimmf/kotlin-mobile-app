package com.pgmv.bandify.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.domain.Song
import com.pgmv.bandify.ui.theme.Grey20

@Composable
fun SongsSelectionForEvent(dbHelper: DatabaseHelper, selectedSongs: MutableState<Set<Song>>) {
    val songDao = dbHelper.songDao()


    val songs = songDao.getSongsByUserId(1).collectAsState(initial = emptyList()).value

    Text(
        text = "Músicas do evento",
        
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(modifier = Modifier.padding(8.dp))
    MultiSelectDropdownSelector(
        items = songs,
        onItemSelected = {
            selectedSongs.value = if (selectedSongs.value.contains(it)) {
                selectedSongs.value - it
            } else {
                selectedSongs.value + it
            }
        },
        displayItems = { "${it.title} - ${it.artist}" },
        selectedItems = selectedSongs.value
    )

    LazyColumn {
        items(selectedSongs.value.size) { item ->
            Row (
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Música"
                )
                Text(
                    text = "${selectedSongs.value.elementAt(item).title} - ${selectedSongs.value.elementAt(item).artist}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        selectedSongs.value -= selectedSongs.value.elementAt(item)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover música"
                    )
                }
            }
            HorizontalDivider(
                thickness = 2.dp,
                color = Grey20
            )
        }
    }
}