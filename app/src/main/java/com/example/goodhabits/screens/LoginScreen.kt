package com.example.goodhabits.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.goodhabits.R
import com.example.goodhabits.viewmodel.AuthResult
import com.example.goodhabits.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authResult by userViewModel.authResult.collectAsState()
    var isLoginMode by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Launcher for Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    userViewModel.firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Error de inicio de sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
             Toast.makeText(context, "Inicio de sesión con Google cancelado.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authResult) {
        if (authResult is AuthResult.Success) {
            onLoginSuccess((authResult as AuthResult.Success).username)
            userViewModel.resetAuthState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0xFFFFC107))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLoginMode) "Bienvenido de Vuelta" else "Crear Nueva Cuenta",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = if (isLoginMode) "Inicia sesión para continuar" else "Regístrate para empezar tu viaje",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.8f)),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de Usuario", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.White.copy(alpha = 0.7f)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (val result = authResult) {
                is AuthResult.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                is AuthResult.Error -> {
                    Text(
                        text = result.message,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                else -> {}
            }

            Button(
                onClick = {
                    if (isLoginMode) userViewModel.login(username, password)
                    else userViewModel.register(username, password)
                },
                enabled = authResult !is AuthResult.Loading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = if (isLoginMode) Color(0xFF4CAF50) else Color(0xFF2196F3))
            ) {
                Text(if (isLoginMode) "Iniciar Sesión" else "Crear Cuenta", color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- Google Sign-In Button ---
            Button(
                onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                // In a real app, you would use a Google icon here
                Text("Iniciar sesión con Google", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isLoginMode) "¿No tienes una cuenta? Regístrate" else "¿Ya tienes una cuenta? Inicia Sesión",
                color = Color.White,
                modifier = Modifier.clickable {
                    isLoginMode = !isLoginMode
                    userViewModel.resetAuthState()
                },
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
