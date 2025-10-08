package com.pgmv.bandify.ui.screen



import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.ui.theme.BandifyTheme
import kotlinx.coroutines.flow.flowOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pgmv.bandify.domain.File




@Composable
fun ArquivosScreen(
    dbHelper: DatabaseHelper? = null,
    navController: NavController
){
    val fileDao = dbHelper?.fileDao()

    // Armazena a categoria dos arquivos
    var selectedCategory by remember { mutableStateOf("Todos") }


    val filesFlow = remember(selectedCategory) {
        when (selectedCategory) {
            "PDF" -> fileDao?.getFilesByCategory("PDF") ?: flowOf(emptyList())
            "Imagem" -> fileDao?.getFilesByCategory("Imagem") ?: flowOf(emptyList())
            else -> fileDao?.getAllFiles() ?: flowOf(emptyList())
        }
    }

val files by filesFlow.collectAsState(initial = emptyList())

    // Estrutura da tela
Scaffold(
modifier = Modifier.fillMaxSize(),
floatingActionButton = {
    Button (
        onClick = { navController.navigate("upload_arquivo") },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "Enviar Arquivo",
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Enviar Arquivo"
        )
    }
}
)
{ innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Text(
            text = "Arquivos Compartilhados",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Filtro de Categorias
        CategoryFilterRow(
            selectedCategory = selectedCategory,
            onCategorySelected = { category -> selectedCategory = category }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (files.isEmpty()) {
                Text(
                    text = "Nenhum arquivo encontrado",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.align(CenterHorizontally)
                )
            } else {
                files.forEach { file ->
                    FileCard(file = file)
                }
            }
        }
    }
}
}

@Composable
fun CategoryFilterRow(selectedCategory: String, onCategorySelected: (String) -> Unit) {

    Button(onClick = { onCategorySelected("PDF") }) {
        Text("PDF")
    }
    Button(onClick = { onCategorySelected("Imagem") }) {
        Text("Imagem")
    }
    Button(onClick = { onCategorySelected("Todos") }) {
        Text("Todos")
    }
}

@Composable
fun FileCard(file: File) {
    Text(
        text = file.name,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Text(
        text = "Tipo: ${file.category}",
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Preview (
    showBackground = true,
    showSystemUi = true)

@Composable
fun ArquivosScreenPreview() {
    BandifyTheme {
        ArquivosScreen(navController = NavController(context = androidx.compose.ui.platform.LocalContext.current))
    }
}