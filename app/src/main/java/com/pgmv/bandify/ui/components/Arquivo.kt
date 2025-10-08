package com.pgmv.bandify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pgmv.bandify.ui.theme.BandifyTheme

@Composable

fun Arquivo (){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        //verticalAlignment = Alignment.CenterVertically
    ){
//        Icon(
//            imageVector =
//            tint =
//            contentDescription =
//            )

        Spacer(modifier = Modifier.width(1.dp))

        Column(modifier = Modifier.weight(1f)){
            Text(
                text = "Lista de arquivos"
            )

        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row (Modifier.padding(8.dp)) {
            Text(text = "Todos")
            Text(text = "PDF")
            Text(text = "Imagem")
            Text(text = "Evento")
        }

    }
}
@Preview
@Composable
fun ArquivoPreview(){
    BandifyTheme {
        Arquivo()
    }
}