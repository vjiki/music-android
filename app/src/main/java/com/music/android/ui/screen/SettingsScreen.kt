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
import androidx.navigation.NavController
import com.music.android.ui.viewmodel.AuthViewModel
import com.music.android.ui.viewmodel.SongManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    songManagerViewModel: SongManagerViewModel,
    onDismiss: () -> Unit
) {
    var showDataAndStorage by remember { mutableStateOf(false) }
    val effectiveUser = authViewModel.effectiveUser
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings and activity", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Your app and media section
            SectionHeader(title = "Your app and media")
            
            SettingsRow(
                icon = Icons.Default.PhoneAndroid,
                title = "Device permissions"
            )
            
            SettingsRow(
                icon = Icons.Default.Download,
                title = "Archiving and downloading"
            )
            
            // Data and Storage row with green icon
            SettingsRow(
                icon = Icons.Default.Storage,
                title = "Data and Storage",
                iconTint = Color(0xFF4CAF50), // Green
                onClick = { showDataAndStorage = true }
            )
            
            SettingsRow(
                icon = Icons.Default.Accessibility,
                title = "Accessibility"
            )
            
            SettingsRow(
                icon = Icons.Default.Translate,
                title = "Language and translations"
            )
            
            SettingsRow(
                icon = Icons.Default.HighQuality,
                title = "Media quality"
            )
            
            SettingsRow(
                icon = Icons.Default.Computer,
                title = "App website permissions"
            )
            
            // Login section
            SectionHeader(title = "Login")
            
            if (isAuthenticated) {
                // User info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = effectiveUser.nickname ?: effectiveUser.name ?: "User",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (effectiveUser.email != null) {
                            Text(
                                text = effectiveUser.email ?: "",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                // Logout button
                TextButton(
                    onClick = {
                        authViewModel.signOut()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Log out",
                        color = Color.Red
                    )
                }
            } else {
                TextButton(
                    onClick = {
                        // Navigate to login
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Add account",
                        color = Color.Blue
                    )
                }
            }
        }
    }
    
    // Data and Storage sheet
    if (showDataAndStorage) {
        DataAndStorageSettingsScreen(
            onDismiss = { showDataAndStorage = false },
            songManagerViewModel = songManagerViewModel
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

