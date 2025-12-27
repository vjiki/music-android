package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.music.android.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualSignInScreen(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit,
    onSignInSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    // Handle authentication success
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onSignInSuccess()
        }
    }
    
    // Handle errors
    LaunchedEffect(error) {
        if (error != null) {
            errorMessage = error
            showError = true
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            color = Color.Black,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sign in with Email",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter your email and password to continue",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Email field
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your email", color = Color.White.copy(alpha = 0.5f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White.copy(alpha = 0.3f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                    
                    // Password field
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your password", color = Color.White.copy(alpha = 0.5f)) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White.copy(alpha = 0.3f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                    
                    // Sign in button
                    Button(
                        onClick = {
                            authViewModel.signInWithEmail(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (email.isNotEmpty() && password.isNotEmpty() && !isLoading) {
                                Color.Blue
                            } else {
                                Color.White.copy(alpha = 0.2f)
                            }
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Sign In",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
    
    // Error Alert
    if (showError && errorMessage != null) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Sign In Error", color = Color.White) },
            text = { Text(errorMessage ?: "", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("OK", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

