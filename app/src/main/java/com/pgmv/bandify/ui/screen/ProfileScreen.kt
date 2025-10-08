package com.pgmv.bandify.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pgmv.bandify.database.DatabaseHelper
import com.pgmv.bandify.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun ProfileScreen(databaseHelper: DatabaseHelper, userId: Long?, navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val firebaseUser = auth.currentUser
    val isGoogleLogin = firebaseUser != null
    val context = LocalContext.current
    val googleSignInClient = getGoogleSignInClient(context)

    val user = remember { mutableStateOf<User?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(userId, firebaseUser) {
        isLoading.value = true
        if (isGoogleLogin) {

            isLoading.value = false
        } else if (userId != null) {

            user.value = withContext(Dispatchers.IO) {
                databaseHelper.userDao().getUserById(userId)
            }
            isLoading.value = false
        } else {
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val userProfile = user.value

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account Icon",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isGoogleLogin) firebaseUser?.displayName ?: "Usuário"
                        else userProfile?.username ?: "Usuário",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Vocalista Principal",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(start = 35.dp, top = 30.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Phone Icon",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGoogleLogin) firebaseUser?.phoneNumber ?: "Não informado"
                    else userProfile?.phone ?: "Não informado",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Row(
                modifier = Modifier
                    .padding(start = 35.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email Icon",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGoogleLogin) firebaseUser?.email ?: "Não informado"
                    else userProfile?.email ?: "Não informado",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 60.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Editar Informações",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.LockReset,
                        contentDescription = "LockReset Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Alterar Senha",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Configurações",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(start = 30.dp, top = 120.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (auth.currentUser != null) {

                        TextButton(
                            onClick = {
                                auth.signOut()
                                googleSignInClient.signOut()
                                navController.navigate("login") {
                                    popUpTo("profile") { inclusive = true }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout Icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sair",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                }
            }

        }
    }
}





