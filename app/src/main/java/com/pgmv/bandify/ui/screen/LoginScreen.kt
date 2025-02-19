package com.pgmv.bandify.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pgmv.bandify.R
import com.pgmv.bandify.viewmodel.AuthenticationViewModel
import com.pgmv.bandify.viewmodel.UserViewModel
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun LoginScreen(
    authenticationViewModel: AuthenticationViewModel,
    navController: NavHostController,
    userViewModel: UserViewModel,

) {
    val email = authenticationViewModel.email
    val password = authenticationViewModel.password
    val emailError = authenticationViewModel.emailError
    val passwordError = authenticationViewModel.passwordError
    var visiblePassword by remember { mutableStateOf(false) }
    val loginError = authenticationViewModel.loginError

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val googleSignInClient = getGoogleSignInClient(context)


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_reverse_transparent),
            contentDescription = "Logo da aplicação",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Faça login na sua conta\n para continuar",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { authenticationViewModel.updateEmail(it) },
            label = { Text("Email") },
            isError = emailError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            supportingText = {
                emailError?.let { errorMsg ->
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { authenticationViewModel.updatePassword(it) },
            label = { Text("Senha") },
            isError = passwordError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = if (visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visiblePassword = !visiblePassword }) {
                    Icon(
                        imageVector = if (visiblePassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (visiblePassword) "Ocultar senha" else "Mostrar senha"
                    )
                }
            },
            supportingText = {
                passwordError?.let { errorMsg ->
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        if (loginError != null) {
            Text(
                text = loginError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                authenticationViewModel.login(
                    onSuccess = {
                        val context = navController.context
                        val sharedPreferences =
                            context.getSharedPreferences("user_prefs", 0)
                        authenticationViewModel.loggedInUserId?.let { it1 ->
                            sharedPreferences.edit().putLong("user_id", it1).apply()
                        }

                        userViewModel.userId = authenticationViewModel.loggedInUserId
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onError = {}
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ainda não possui uma conta?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        TextButton(
            onClick = { navController.navigate("register") },
        ) {
            Text(
                text = "Cadastre-se",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent;
                launcher.launch(signInIntent);
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 16.dp)
                .height(50.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(50)),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                );
                Text(
                    text = "Fazer Login com Google",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium
                );
            }
        }
    }
}

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("351674972226-1hut8i6q4iv6q6cpoa5aa2b5eccaae5n.apps.googleusercontent.com") // Substitua pelo seu ID de cliente do Firebase
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}



