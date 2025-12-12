package com.example.goodhabits.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.dataUser.User
import com.example.goodhabits.dataUser.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Auth states for UI
sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    data class Success(val username: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

// Data from Realtime DB
data class UserProfile(
    val username: String = "",
    val profileImageUrl: String = ""
)

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _uploadInProgress = MutableStateFlow(false)
    val uploadInProgress: StateFlow<Boolean> = _uploadInProgress.asStateFlow()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://goodhabits-5f00a-default-rtdb.firebaseio.com/")
    private val storage = Firebase.storage

    private var valueEventListener: ValueEventListener? = null
    private var currentKey: String? = null

    private fun listenToUserProfile(key: String) {
        if (currentKey == key) return // Already listening
        currentKey = key
        val userRef = database.getReference("users").child(key)
        valueEventListener?.let { database.getReference("users").removeEventListener(it) } // remove old listener

        valueEventListener = userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(UserProfile::class.java)
                _userProfile.update { 
                    profile ?: UserProfile(username = firebaseAuth.currentUser?.displayName ?: key)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                _authResult.value = AuthResult.Error("Error de base de datos: ${error.message}")
            }
        })
    }
    
    fun uploadProfileImage(uri: Uri) {
        val key = currentKey ?: return
        _uploadInProgress.value = true
        
        val storageRef = storage.reference.child("profile_pictures/$key.jpg")
        
        viewModelScope.launch {
            try {
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                database.getReference("users").child(key).child("profileImageUrl").setValue(downloadUrl).await()
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error("Error al subir la imagen: ${e.message}")
            } finally {
                _uploadInProgress.value = false
            }
        }
    }
    
    fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            try {
                val authResultData = firebaseAuth.signInWithCredential(credential).await()
                val user = authResultData.user!!
                val username = user.displayName ?: "Usuario"
                
                val userRef = database.getReference("users").child(user.uid)
                val snapshot = userRef.get().await()
                if (!snapshot.exists()) {
                     val profile = UserProfile(username = username, profileImageUrl = user.photoUrl?.toString() ?: "")
                     userRef.setValue(profile).await()
                }
                
                listenToUserProfile(user.uid)
                _authResult.value = AuthResult.Success(username)

            } catch (e: Exception) {
                 _authResult.value = AuthResult.Error("Error de Firebase: ${e.message}")
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val user = repository.getUserByUsername(username)
            if (user == null) {
                _authResult.value = AuthResult.Error("El usuario no existe.")
            } else if (user.passwordHash != hashPassword(password)) {
                _authResult.value = AuthResult.Error("Contraseña incorrecta.")
            } else {
                listenToUserProfile(username) 
                _authResult.value = AuthResult.Success(user.username)
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            if (repository.getUserByUsername(username) != null) {
                _authResult.value = AuthResult.Error("El nombre de usuario ya existe.")
            } else {
                val newUser = User(username = username, passwordHash = hashPassword(password))
                repository.insertUser(newUser)
                
                val profile = UserProfile(username = username, profileImageUrl = "")
                database.getReference("users").child(username).setValue(profile).await()

                listenToUserProfile(username)
                _authResult.value = AuthResult.Success(username)
            }
        }
    }

    fun updateCredentials(currentUsername: String, newUsername: String, currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val user = repository.getUserByUsername(currentUsername)
            if (user == null || user.passwordHash != hashPassword(currentPassword)) {
                _authResult.value = AuthResult.Error("La contraseña actual es incorrecta.")
                return@launch
            }

            if (newUsername.isNotBlank() && newUsername != currentUsername) {
                database.getReference("users").child(currentKey!!).child("username").setValue(newUsername).await()
            }

            if (newPassword.isNotBlank()) {
                 val updatedUser = user.copy(passwordHash = hashPassword(newPassword))
                 repository.updateUser(updatedUser)
            }
            
            _authResult.value = AuthResult.Success(newUsername.ifBlank { currentUsername })
        }
    }
    
    private fun hashPassword(password: String): String = password.hashCode().toString()

    fun resetAuthState() {
        _authResult.value = AuthResult.Idle
    }

    override fun onCleared() {
        super.onCleared()
        valueEventListener?.let { listener ->
             currentKey?.let { key ->
                database.getReference("users").child(key).removeEventListener(listener)
             }
        }
    }
}