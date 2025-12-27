package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.music.android.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit,
    onSignInSuccess: () -> Unit = {}
) {
    var showManualSignIn by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSigningInWithGoogle by remember { mutableStateOf(false) }
    
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    // Dismiss when authenticated
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onSignInSuccess()
            onDismiss()
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sign in to Music",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose how you want to sign in",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Google Sign-In Button
                    Button(
                        onClick = {
                            isSigningInWithGoogle = true
                            // TODO: Implement Google Sign-In
                            // authViewModel.signInWithGoogle()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isSigningInWithGoogle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSigningInWithGoogle) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Continue with Google",
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    // Manual Sign-In Button
                    Button(
                        onClick = { showManualSignIn = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Sign in with Email",
                                    color = Color.White
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
    
    // Manual Sign-In Sheet
    if (showManualSignIn) {
        ManualSignInScreen(
            authViewModel = authViewModel,
            onDismiss = { showManualSignIn = false },
            onSignInSuccess = {
                showManualSignIn = false
                onSignInSuccess()
                onDismiss()
            }
        )
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

