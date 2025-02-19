package com.pgmv.bandify.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.ui.components.DropdownTextField
import com.pgmv.bandify.ui.components.SearchResultsList
import com.pgmv.bandify.ui.components.ValidatedTextField
import com.pgmv.bandify.ui.theme.BandifyTheme
import com.pgmv.bandify.viewmodel.NovaMusicaViewModel
import com.pgmv.bandify.viewmodel.NovaMusicaViewModelFactory

@Composable
fun NovaMusicaScreen(
    dbHelper: DatabaseHelper? = null,
    navController: NavController? = null
) {
    val viewModel: NovaMusicaViewModel = viewModel(
        factory = NovaMusicaViewModelFactory(dbHelper)
    )

    val context = LocalContext.current

    val songTitle = viewModel.songTitle.value
    val artist = viewModel.artist.value
    val selectedTag = viewModel.selectedTag.value
    val selectedTempo = viewModel.selectedTempo.value
    val selectedKey = viewModel.selectedKey.value
    val songAdded by viewModel.songAdded

    val validFields = songTitle.isNotBlank()
            && artist.isNotBlank()
            && selectedTag.isNotBlank()
            && selectedTempo.isNotBlank()
            && selectedKey.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ValidatedTextField(
            value = songTitle,
            onValueChange = {
                viewModel.updateSongTitle(it)
                viewModel.searchSong(it)
            },
            label = "Nome da Música",
            modifier = Modifier.padding(top = 12.dp),
            leadingIcon = {
                Icon(Icons.Filled.MusicNote,
                    contentDescription = "Música")
            }
        )

        SearchResultsList(dbHelper)

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            ValidatedTextField(
                value = artist,
                onValueChange = { viewModel.updateArtist(it) },
                label = "Banda/Artista",
                modifier = Modifier.weight(1f)
            )

            DropdownTextField(
                label = "Tag",
                options = viewModel.tagOptions,
                selectedOption = selectedTag,
                onOptionSelected = { viewModel.updateSelectedTag(it) },
                modifier = Modifier.weight(1f)
            )
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            ValidatedTextField(
                value = selectedTempo,
                onValueChange = { viewModel.updateSelectedTempo(it) },
                label = "Tempo (bpm)",
                modifier = Modifier.weight(1f)
            )

            DropdownTextField(
                label = "Tom",
                options = viewModel.keyOptions,
                selectedOption = selectedKey,
                onOptionSelected = { viewModel.updateSelectedKey(it) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.padding(24.dp))

        Button(
            onClick = {
                if (validFields){
                    viewModel.saveSong()
                    navController?.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            enabled = validFields
        ) {
            Text(text = "Adicionar Música", style = MaterialTheme.typography.titleMedium)
        }
    }

    LaunchedEffect(songAdded) {
        if (songAdded) {
            Toast.makeText(context, "Música adicionada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NovaMusicaScreenPreview() {
    BandifyTheme {
        NovaMusicaScreen()
    }
}