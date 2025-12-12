package com.example.goodhabits.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.goodhabits.viewmodel.AuthResult
import com.example.goodhabits.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    currentUsername: String,
) {
    var newUsername by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    
    val authResult by userViewModel.authResult.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()
    val isUploading by userViewModel.uploadInProgress.collectAsState()

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { userViewModel.uploadProfileImage(it) }
        }
    )

    LaunchedEffect(authResult) {
        when (val result = authResult) {
            is AuthResult.Success -> {
                Toast.makeText(context, "Datos actualizados correctamente.", Toast.LENGTH_SHORT).show()
                userViewModel.resetAuthState()
            }
            is AuthResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                userViewModel.resetAuthState()
            }
            else -> { /* Do nothing */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Configuración") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(enabled = !isUploading) { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (userProfile.profileImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = userProfile.profileImageUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Añadir foto de perfil",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isUploading) {
                    CircularProgressIndicator()
                }
            }
            Text(if (isUploading) "Subiendo foto..." else "Toca para cambiar la foto")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("Nuevo nombre de usuario") },
                placeholder = { Text(userProfile.username.ifBlank { currentUsername }) },
                modifier = Modifier.fillMaxWidth(),
                enabled = authResult !is AuthResult.Loading
            )

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña actual (requerido)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = authResult !is AuthResult.Loading
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = authResult !is AuthResult.Loading
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                     if(userProfile.username.isNotBlank()){
                        userViewModel.updateCredentials(
                            currentUsername = userProfile.username,
                            newUsername = newUsername,
                            currentPassword = currentPassword,
                            newPassword = newPassword
                        )
                     } else {
                        // This case is for users that are not using Google Sign in
                         userViewModel.updateCredentials(
                            currentUsername = currentUsername,
                            newUsername = newUsername,
                            currentPassword = currentPassword,
                            newPassword = newPassword
                        )                       
                     }

                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authResult !is AuthResult.Loading && currentPassword.isNotBlank()
            ) {
                if (authResult is AuthResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}