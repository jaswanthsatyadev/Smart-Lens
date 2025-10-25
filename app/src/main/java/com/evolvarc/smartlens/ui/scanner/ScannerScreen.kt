package com.evolvarc.smartlens.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onProductScanned: (String) -> Unit,
    onAddProduct: (String) -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    var flashlightEnabled by remember { mutableStateOf(false) }
    var showManualEntryDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SmartLens",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Scan History",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                CameraPreview(
                    flashlightEnabled = flashlightEnabled,
                    onBarcodeDetected = { barcode ->
                        viewModel.onBarcodeScanned(barcode)
                    }
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                
                Spacer(modifier = Modifier.weight(1f))
                
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Position barcode within frame",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Control buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Flashlight toggle
                    FloatingActionButton(
                        onClick = { flashlightEnabled = !flashlightEnabled },
                        containerColor = if (flashlightEnabled) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surface,
                        contentColor = if (flashlightEnabled)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = if (flashlightEnabled) "💡" else "🔦",
                            fontSize = 24.sp
                        )
                    }
                    
                    // Manual entry button
                    FloatingActionButton(
                        onClick = { showManualEntryDialog = true },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text("123", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // Manual entry dialog
            if (showManualEntryDialog) {
                ManualBarcodeEntryDialog(
                    onDismiss = { showManualEntryDialog = false },
                    onBarcodeEntered = { barcode ->
                        showManualEntryDialog = false
                        viewModel.onBarcodeScanned(barcode)
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Camera Permission Required",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SmartLens needs camera access to scan product barcodes",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }
        
        val haptic = LocalHapticFeedback.current
        
        when (uiState) {
            is ScannerUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is ScannerUiState.Success -> {
                val product = (uiState as ScannerUiState.Success).product
                LaunchedEffect(product) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onProductScanned(product.barcode)
                }
            }
            is ScannerUiState.Error -> {
                val errorMessage = (uiState as ScannerUiState.Error).message
                val scannedBarcode = (uiState as ScannerUiState.Error).barcode
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = when {
                                    errorMessage.contains("not found", ignoreCase = true) -> "Product Not Found"
                                    errorMessage.contains("network", ignoreCase = true) -> "Network Error"
                                    errorMessage.contains("timeout", ignoreCase = true) -> "Connection Timeout"
                                    else -> "Error"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when {
                                    errorMessage.contains("not found", ignoreCase = true) -> 
                                        "This product isn't in our database yet."
                                    errorMessage.contains("network", ignoreCase = true) -> 
                                        "Check your internet connection and try again."
                                    errorMessage.contains("timeout", ignoreCase = true) -> 
                                        "The server took too long to respond. Please try again."
                                    else -> errorMessage
                                },
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Show "Add Product" button if product not found
                            if (errorMessage.contains("not found", ignoreCase = true) && scannedBarcode != null) {
                                Button(
                                    onClick = { 
                                        viewModel.resetScanner()
                                        onAddProduct(scannedBarcode)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Add This Product")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showManualEntryDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Text("Enter Manually")
                                }
                                Button(
                                    onClick = { viewModel.resetScanner() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Scan Again")
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
        }
    }
}

@Composable
fun CameraPreview(
    flashlightEnabled: Boolean,
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val barcodeScanner = remember { BarcodeScanning.getClient() }
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    var lastScannedBarcode by remember { mutableStateOf<String?>(null) }
    var lastScanTime by remember { mutableStateOf(0L) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    
    // Handle flashlight state changes
    LaunchedEffect(flashlightEnabled) {
        camera?.cameraControl?.enableTorch(flashlightEnabled)
    }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProvider = cameraProviderFuture.get()
            
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { value ->
                                            val currentTime = System.currentTimeMillis()
                                            if (value != lastScannedBarcode || currentTime - lastScanTime > 3000) {
                                                lastScannedBarcode = value
                                                lastScanTime = currentTime
                                                onBarcodeDetected(value)
                                            }
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }
                }
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ManualBarcodeEntryDialog(
    onDismiss: () -> Unit,
    onBarcodeEntered: (String) -> Unit
) {
    var barcodeInput by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Barcode") },
        text = {
            Column {
                Text(
                    "Can't scan the barcode? Enter it manually:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = barcodeInput,
                    onValueChange = { barcodeInput = it },
                    label = { Text("Barcode") },
                    placeholder = { Text("e.g., 8901058856507") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (barcodeInput.isNotBlank()) {
                                onBarcodeEntered(barcodeInput)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (barcodeInput.isNotBlank()) {
                        onBarcodeEntered(barcodeInput)
                    }
                },
                enabled = barcodeInput.isNotBlank()
            ) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
