package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Canvas
import com.music.android.data.cache.CacheService
import com.music.android.data.cache.CacheCategory
import com.music.android.ui.viewmodel.SongManagerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAndStorageSettingsScreen(
    onDismiss: () -> Unit,
    songManagerViewModel: SongManagerViewModel
) {
    val context = LocalContext.current
    val cacheService = remember { CacheService.getInstance(context) }
    val scope = rememberCoroutineScope()
    
    val totalCacheSize by cacheService.totalCacheSize.collectAsState()
    val imagesCacheSize by cacheService.imagesCacheSize.collectAsState()
    val audioCacheSize by cacheService.audioCacheSize.collectAsState()
    val videoCacheSize by cacheService.videoCacheSize.collectAsState()
    
    var showClearCacheConfirmation by remember { mutableStateOf(false) }
    var isClearingCache by remember { mutableStateOf(false) }
    var selectedCacheSize by remember { mutableStateOf(CacheSize.FIVE_GB) }
    
    val cacheData = remember(totalCacheSize, imagesCacheSize, audioCacheSize, videoCacheSize) {
        cacheService.getCacheStatistics()
    }
    
    LaunchedEffect(Unit) {
        cacheService.calculateCacheSize()
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
                        text = "Data and Storage",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                if (cacheData.totalSize > 0) {
                    // Donut chart
                    DonutChartView(
                        cacheData = cacheData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    )
                    
                    // Memory usage summary
                    MemoryUsageSummary(
                        totalSize = cacheData.totalSize,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Cache categories list
                    CacheCategoriesList(
                        categories = cacheData.categories,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Clear cache button
                    Button(
                        onClick = { showClearCacheConfirmation = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = !isClearingCache,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue
                        )
                    ) {
                        if (isClearingCache) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Clear All Cache ${String.format("%.1f", cacheData.totalSize)} GB",
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Cloud storage note
                    Text(
                        text = "All media will remain in the cloud; you can download them again if needed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    // Empty state
                    EmptyStateView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Auto-delete section
                AutoDeleteSection(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Maximum cache size section
                MaximumCacheSizeSection(
                    selectedCacheSize = selectedCacheSize,
                    onCacheSizeSelected = { selectedCacheSize = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
    
    // Clear cache confirmation
    if (showClearCacheConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearCacheConfirmation = false },
            title = { Text("Clear Cache", color = Color.White) },
            text = {
                Text(
                    text = "This will clear ${String.format("%.1f", cacheData.totalSize)} GB of cached data. All media will remain in the cloud and can be downloaded again if needed.",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isClearingCache = true
                        scope.launch {
                            cacheService.clearAllCache()
                            isClearingCache = false
                            showClearCacheConfirmation = false
                        }
                    }
                ) {
                    Text("Clear All Cache", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheConfirmation = false }) {
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
private fun DonutChartView(
    cacheData: com.music.android.data.cache.CacheData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Canvas(modifier = Modifier.size(200.dp)) {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = size.minDimension / 2 - 15.dp.toPx(),
                style = Stroke(width = 30.dp.toPx())
            )
        }
        
        // Segmented donut chart
        DonutChart(
            categories = cacheData.categories,
            modifier = Modifier.size(200.dp)
        )
        
        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f GB", cacheData.totalSize),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DonutChart(
    categories: List<CacheCategory>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        var startAngle = -90f
        val radius = size.minDimension / 2 - 15.dp.toPx()
        val strokeWidth = 30.dp.toPx()
        
        categories.forEach { category ->
            val sweepAngle: Float = ((category.percentage / 100f) * 360f).toFloat()
            drawArc(
                color = Color(category.color),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
private fun MemoryUsageSummary(
    totalSize: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Memory Usage",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Music occupies ${String.format("%.1f", totalSize * 0.278)}% of free space on the device.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Divider(
            color = Color.Blue.copy(alpha = 0.3f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun CacheCategoriesList(
    categories: List<CacheCategory>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEach { category ->
            CacheCategoryRow(category = category)
        }
    }
}

@Composable
private fun CacheCategoryRow(
    category: CacheCategory
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(category.color),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = category.name,
                color = Color.White
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${String.format("%.1f", category.percentage)}%",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = formatSize(category.size),
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Storage,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.White.copy(alpha = 0.3f)
        )
        Text(
            text = "No cached data",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Cached songs and images will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun AutoDeleteSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "AUTO-DELETE CACHED MEDIA",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        AutoDeleteRow(
            icon = Icons.Default.Person,
            title = "Personal chats",
            value = "Never"
        )
        AutoDeleteRow(
            icon = Icons.Default.Group,
            title = "Groups",
            value = "1 month"
        )
        AutoDeleteRow(
            icon = Icons.Default.Campaign,
            title = "Channels",
            value = "1 week"
        )
    }
}

@Composable
private fun AutoDeleteRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                color = Color.White
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
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
private fun MaximumCacheSizeSection(
    selectedCacheSize: CacheSize,
    onCacheSizeSelected: (CacheSize) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MAXIMUM CACHE SIZE",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Photos, videos and other files that you have not opened during this period will be deleted from the device to save space on your phone.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        
        // Cache size selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(8.dp)
                ),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            CacheSize.allCases.forEach { size ->
                Button(
                    onClick = { onCacheSizeSelected(size) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCacheSize == size) {
                            Color.Blue.copy(alpha = 0.3f)
                        } else {
                            Color.Transparent
                        }
                    )
                ) {
                    Text(
                        text = size.displayName,
                        color = if (selectedCacheSize == size) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.6f)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        Text(
            text = "If the cache size exceeds this limit, the oldest unused media will be deleted from the device's memory.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

enum class CacheSize(val displayName: String) {
    FIVE_GB("5 GB"),
    TWENTY_GB("20 GB"),
    FIFTY_GB("50 GB"),
    NONE("None");
    
    companion object {
        val allCases = values().toList()
    }
}

private fun formatSize(size: Double): String {
    return if (size >= 1.0) {
        String.format("%.1f GB", size)
    } else {
        String.format("%.1f MB", size * 1024)
    }
}

