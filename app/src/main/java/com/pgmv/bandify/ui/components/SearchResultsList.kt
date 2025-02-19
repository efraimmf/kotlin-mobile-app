package com.pgmv.bandify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.viewmodel.NovaMusicaViewModel
import com.pgmv.bandify.viewmodel.NovaMusicaViewModelFactory

@Composable
fun SearchResultsList(dbHelper: DatabaseHelper? = null) {
    val viewModel: NovaMusicaViewModel = viewModel(
        factory = NovaMusicaViewModelFactory(dbHelper)
    )

    val searchResults by viewModel.searchResults

    if (searchResults.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
                .heightIn(max = 300.dp)
        ) {
            items(searchResults) { recording ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectSearchResult(recording) }
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.small)
                ) {
                    Text(
                        text = recording.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = recording.artist.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}