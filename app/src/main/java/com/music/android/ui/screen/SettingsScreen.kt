package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    var searchText by remember { mutableStateOf("") }
    var showLoginView by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }
    var showDataAndStorage by remember { mutableStateOf(false) }
    
    val effectiveUser = authViewModel.effectiveUser
    val currentUser by authViewModel.currentUser.collectAsState()
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
            // Search bar
            SearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Your account section
            YourAccountSection()
            
            // How you use Music section
            HowYouUseSection()
            
            // Who can see your content section
            WhoCanSeeSection()
            
            // How others can interact with you section
            HowOthersInteractSection()
            
            // What you see section
            WhatYouSeeSection()
            
            // Your app and media section
            YourAppAndMediaSection(
                onDataAndStorageClick = { showDataAndStorage = true }
            )
            
            // Family Centre section
            FamilyCentreSection()
            
            // Your insights and tools section
            YourInsightsSection()
            
            // Your orders and fundraisers section
            OrdersSection()
            
            // More info and support section
            MoreInfoSection()
            
            // Also from Meta section
            AlsoFromMetaSection()
            
            // Login section
            LoginSection(
                authViewModel = authViewModel,
                currentUser = currentUser,
                isAuthenticated = isAuthenticated,
                onAddAccountClick = { showLoginView = true },
                onLogoutClick = { showLogoutConfirmation = true },
                isLoggingOut = isLoggingOut
            )
        }
    }
    
    // Login screen
    if (showLoginView) {
        LoginScreen(
            authViewModel = authViewModel,
            onDismiss = { showLoginView = false },
            onSignInSuccess = { showLoginView = false }
        )
    }
    
    // Data and Storage sheet
    if (showDataAndStorage) {
        DataAndStorageSettingsScreen(
            onDismiss = { showDataAndStorage = false },
            songManagerViewModel = songManagerViewModel
        )
    }
    
    // Logout confirmation
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Log Out", color = Color.White) },
            text = {
                Text(
                    text = "Are you sure you want to log out?",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isLoggingOut = true
                        authViewModel.signOut()
                        isLoggingOut = false
                        showLogoutConfirmation = false
                    }
                ) {
                    Text("Log Out", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search", color = Color.White.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.6f)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {})
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
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
    trailing: @Composable (() -> Unit)? = null,
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
        
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun YourAccountSection() {
    Column {
        SectionHeader(title = "Your account")
        
        SettingsRow(
            icon = Icons.Default.Person,
            title = "Accounts Centre",
            subtitle = "Password, security, personal details, ad preferences",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "∞ Meta",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        
        Text(
            text = "Manage your connected experiences and account settings across Meta technologies. Learn more",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun HowYouUseSection() {
    Column {
        SectionHeader(title = "How you use Music")
        
        SettingsRow(icon = Icons.Default.Bookmark, title = "Saved")
        SettingsRow(icon = Icons.Default.Refresh, title = "Archive")
        SettingsRow(icon = Icons.Default.TrendingUp, title = "Your activity")
        SettingsRow(icon = Icons.Default.Notifications, title = "Notifications")
        SettingsRow(icon = Icons.Default.Schedule, title = "Time management")
        SettingsRow(
            icon = Icons.Default.MusicNote,
            title = "Update Music",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.Blue, CircleShape)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
    }
}

@Composable
private fun WhoCanSeeSection() {
    Column {
        SectionHeader(title = "Who can see your content")
        
        SettingsRow(
            icon = Icons.Default.Lock,
            title = "Account privacy",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Public",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        
        SettingsRow(
            icon = Icons.Default.Star,
            title = "Close Friends",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        
        SettingsRow(icon = Icons.Default.Layers, title = "Crossposting")
        SettingsRow(
            icon = Icons.Default.Block,
            title = "Blocked",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        SettingsRow(icon = Icons.Default.LocationOff, title = "Story and location")
        SettingsRow(icon = Icons.Default.People, title = "Activity in Friends tab")
    }
}

@Composable
private fun HowOthersInteractSection() {
    Column {
        SectionHeader(title = "How others can interact with you")
        
        SettingsRow(icon = Icons.Default.Message, title = "Messages and story replies")
        SettingsRow(icon = Icons.Default.AlternateEmail, title = "Tags and mentions")
        SettingsRow(icon = Icons.Default.Comment, title = "Comments")
        SettingsRow(icon = Icons.Default.Share, title = "Sharing and reuse")
        SettingsRow(
            icon = Icons.Default.Block,
            title = "Restricted",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        SettingsRow(
            icon = Icons.Default.Warning,
            title = "Limit interactions",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Off",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        SettingsRow(icon = Icons.Default.TextFields, title = "Hidden words")
        SettingsRow(icon = Icons.Default.PersonAdd, title = "Follow and invite friends")
    }
}

@Composable
private fun WhatYouSeeSection() {
    Column {
        SectionHeader(title = "What you see")
        
        SettingsRow(
            icon = Icons.Default.Star,
            title = "Favourites",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        
        SettingsRow(
            icon = Icons.Default.NotificationsOff,
            title = "Muted accounts",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        
        SettingsRow(icon = Icons.Default.VideoLibrary, title = "Content preferences")
        SettingsRow(icon = Icons.Default.FavoriteBorder, title = "Like and share counts")
        SettingsRow(icon = Icons.Default.Star, title = "Subscriptions")
    }
}

@Composable
private fun YourAppAndMediaSection(
    onDataAndStorageClick: () -> Unit
) {
    Column {
        SectionHeader(title = "Your app and media")
        
        SettingsRow(icon = Icons.Default.PhoneAndroid, title = "Device permissions")
        SettingsRow(icon = Icons.Default.Download, title = "Archiving and downloading")
        
        // Data and Storage row with green icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDataAndStorageClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                tint = Color(0xFF4CAF50), // Green
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "Data and Storage",
                color = Color.White
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
        
        SettingsRow(icon = Icons.Default.Accessibility, title = "Accessibility")
        SettingsRow(icon = Icons.Default.Translate, title = "Language and translations")
        SettingsRow(icon = Icons.Default.HighQuality, title = "Media quality")
        SettingsRow(icon = Icons.Default.Computer, title = "App website permissions")
    }
}

@Composable
private fun FamilyCentreSection() {
    Column {
        SectionHeader(title = "Family Centre")
        
        SettingsRow(icon = Icons.Default.Home, title = "Supervision for Teen Accounts")
    }
}

@Composable
private fun YourInsightsSection() {
    Column {
        SectionHeader(title = "Your insights and tools")
        
        SettingsRow(icon = Icons.Default.Dashboard, title = "Your dashboard")
        SettingsRow(icon = Icons.Default.BarChart, title = "Account type and tools")
        SettingsRow(
            icon = Icons.Default.Verified,
            title = "Music Verified",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Not subscribed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
    }
}

@Composable
private fun OrdersSection() {
    Column {
        SectionHeader(title = "Your orders and fundraisers")
        
        SettingsRow(icon = Icons.Default.Description, title = "Orders and payments")
    }
}

@Composable
private fun MoreInfoSection() {
    Column {
        SectionHeader(title = "More info and support")
        
        SettingsRow(icon = Icons.Default.Help, title = "Help")
        SettingsRow(icon = Icons.Default.Security, title = "Privacy Centre")
        SettingsRow(icon = Icons.Default.People, title = "Account Status")
        SettingsRow(icon = Icons.Default.Info, title = "About")
    }
}

@Composable
private fun AlsoFromMetaSection() {
    Column {
        SectionHeader(title = "Also from Meta")
        
        SettingsRow(
            icon = Icons.Default.Message,
            title = "WhatsApp",
            subtitle = "Message privately with friends and family"
        )
        
        SettingsRow(
            icon = Icons.Default.Layers,
            title = "Edits",
            subtitle = "Create videos with powerful editing tools",
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.Blue, CircleShape)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )
        
        SettingsRow(
            icon = Icons.Default.AlternateEmail,
            title = "Threads",
            subtitle = "Share ideas and join conversations"
        )
        
        SettingsRow(
            icon = Icons.Default.Public,
            title = "Facebook",
            subtitle = "Explore things that you love"
        )
        
        SettingsRow(
            icon = Icons.Default.Bolt,
            title = "Messenger",
            subtitle = "Chat and share seamlessly with friends"
        )
    }
}

@Composable
private fun LoginSection(
    authViewModel: AuthViewModel,
    currentUser: com.music.android.data.model.AuthUser?,
    isAuthenticated: Boolean,
    onAddAccountClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isLoggingOut: Boolean
) {
    Column {
        SectionHeader(title = "Login")
        
        if (isAuthenticated && currentUser != null) {
            // User info section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentUser.avatarUrl != null && currentUser.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = currentUser.nickname ?: currentUser.name ?: "User",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (currentUser.email != null) {
                        Text(
                            text = currentUser.email ?: "",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Logout button
            TextButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = !isLoggingOut
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Red
                    )
                } else {
                    Text(
                        text = "Log out",
                        color = Color.Red
                    )
                }
            }
        } else {
            // Add account button
            TextButton(
                onClick = onAddAccountClick,
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
