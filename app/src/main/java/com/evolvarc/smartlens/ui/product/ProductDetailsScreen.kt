package com.evolvarc.smartlens.ui.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.model.ProductCategory
import com.evolvarc.smartlens.ui.components.CategoryBadge
import com.evolvarc.smartlens.ui.components.NutriScoreBadgeLarge
import com.evolvarc.smartlens.ui.components.ProductDetailsShimmer
import com.evolvarc.smartlens.ui.components.ScoreCircle
import com.evolvarc.smartlens.ui.components.WarningBadge
import com.evolvarc.smartlens.ui.theme.BackgroundOffWhite
import com.evolvarc.smartlens.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    barcode: String,
    onNavigateBack: () -> Unit,
    onShowAlternatives: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    LaunchedEffect(barcode) {
        viewModel.loadProduct(barcode)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartLens") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState is ProductUiState.Success) {
                        val product = (uiState as ProductUiState.Success).product
                        IconButton(onClick = {
                            val shareIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this product!")
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    "I scanned ${product.name} with SmartLens\n" +
                                    "Health Score: ${product.healthScore}/100\n" +
                                    "Get SmartLens to scan smarter and choose better!"
                                )
                            }
                            context.startActivity(
                                android.content.Intent.createChooser(shareIntent, "Share via")
                            )
                        }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ProductUiState.Loading -> {
                ProductDetailsShimmer()
            }
            is ProductUiState.Success -> {
                var isVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    isVisible = true
                }
                
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)) + 
                            slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                initialOffsetY = { it / 4 }
                            )
                ) {
                    val allergenWarnings by viewModel.allergenWarnings.collectAsState()
                    
                    ProductContent(
                        product = state.product,
                        allergenWarnings = allergenWarnings,
                        onShowAlternatives = onShowAlternatives,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
            is ProductUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductContent(
    product: Product,
    allergenWarnings: List<com.evolvarc.smartlens.domain.usecase.AllergenWarning>,
    onShowAlternatives: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (product.imageUrl != null) {
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "image_scale"
                    )
                    
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                    product.brands?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Cache indicator
            val cacheAge = TimeUtils.getTimeAgoString(product.cachedAt)
            val isExpired = TimeUtils.isCacheExpired(product.cachedAt)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpired) "⚠️ Data may be outdated" else "🕐 Updated $cacheAge",
                    fontSize = 12.sp,
                    color = if (isExpired) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Show NutriScore badge prominently if available
            product.nutritionData?.nutriScoreGrade?.let { grade ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Official NutriScore",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NutriScoreBadgeLarge(grade = grade)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            ScoreCircle(
                score = product.healthScore,
                nutriScoreGrade = null, // Don't show again inside circle
                dataAvailability = product.dataAvailability
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Data availability indicator
            val dataAvailabilityText = when (product.dataAvailability) {
                com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase.DataAvailability.INSUFFICIENT -> 
                    "⚠️ Limited data available for accurate scoring"
                com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase.DataAvailability.PARTIAL -> 
                    "ℹ️ Partial data available - score may not be fully accurate"
                com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase.DataAvailability.COMPLETE -> 
                    null
            }
            
            dataAvailabilityText?.let { text ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (product.dataAvailability) {
                            com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase.DataAvailability.INSUFFICIENT ->
                                MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = when (product.category) {
                    ProductCategory.FOOD -> "Nutritional Health"
                    ProductCategory.BEAUTY, ProductCategory.PERSONAL_CARE -> "Ingredient Safety"
                    else -> "Overall Score"
                },
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CategoryBadge(category = product.category)
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Personalized allergen warnings (for logged-in users)
        if (allergenWarnings.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🚫",
                                fontSize = 24.sp
                            )
                            Text(
                                text = "Allergen Alert",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        allergenWarnings.forEach { warning ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(
                                    text = warning.message,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        if (product.warnings.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(product.warnings) { warning ->
                        WarningBadge(warning = warning)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        item {
            when (product.category) {
                ProductCategory.FOOD -> FoodDetails(product)
                ProductCategory.BEAUTY, ProductCategory.PERSONAL_CARE -> BeautyDetails(product)
                else -> {}
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Disabled Better Alternatives feature
            // Button(
            //     onClick = onShowAlternatives,
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .padding(horizontal = 16.dp)
            //         .height(56.dp),
            //     shape = RoundedCornerShape(12.dp)
            // ) {
            //     Text(
            //         text = "Show Better Alternatives",
            //         fontSize = 16.sp,
            //         fontWeight = FontWeight.SemiBold
            //     )
            // }
            // 
            // Spacer(modifier = Modifier.height(24.dp))
            
            // Where to Buy Section
            BuyLinksSection(
                product = product,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        if (product.ingredientsText != null) {
            item {
                var expanded by remember { mutableStateOf(false) }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(
                                text = if (expanded) "Ingredients (Full List) ▼" else "Ingredients (Full List) ▶",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HighlightedIngredientsText(
                                ingredientsText = product.ingredientsText,
                                harmfulIngredients = product.beautyData?.harmfulIngredients ?: emptyList()
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        item {
            Text(
                text = "Powered by Open${
                    when (product.category) {
                        ProductCategory.FOOD -> "Food"
                        ProductCategory.BEAUTY, ProductCategory.PERSONAL_CARE -> "Beauty"
                        else -> "Products"
                    }
                }Facts",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun FoodDetails(product: Product) {
    val nutrition = product.nutritionData ?: return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Nutritional Breakdown",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            nutrition.energyKcal100g?.let {
                NutrientRow("Calories", "${it.toInt()} kcal", it / 500.0)
            }
            nutrition.sugars100g?.let {
                NutrientRow("Sugar", "${String.format("%.1f", it)}g", it / 25.0)
            }
            nutrition.salt100g?.let {
                NutrientRow("Salt", "${String.format("%.2f", it)}g", it / 2.0)
            }
            nutrition.saturatedFat100g?.let {
                NutrientRow("Saturated Fat", "${String.format("%.1f", it)}g", it / 10.0)
            }
            nutrition.proteins100g?.let {
                NutrientRow("Protein", "${String.format("%.1f", it)}g", it / 20.0)
            }
            nutrition.fiber100g?.let {
                NutrientRow("Fiber", "${String.format("%.1f", it)}g", it / 10.0)
            }
            
            nutrition.nutriScoreGrade?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Nutri-Score: ${it.uppercase()}",
                    fontWeight = FontWeight.Medium
                )
            }
            
            nutrition.novaGroup?.let {
                Text(
                    text = "NOVA Group: $it",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NutrientRow(label: String, value: String, percentage: Double) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 14.sp)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        
        LinearProgressIndicator(
            progress = { percentage.toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
        )
    }
}

@Composable
fun BeautyDetails(product: Product) {
    val beauty = product.beautyData ?: return
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (beauty.isVegan == true) {
                item { AttributeBadge("✓ Vegan", true) }
            }
            if (beauty.isCrueltyFree == true) {
                item { AttributeBadge("✓ Cruelty-Free 🐰", true) }
            }
            if (beauty.isParabenFree == true) {
                item { AttributeBadge("✓ Paraben-Free", true) }
            }
            if (beauty.isSulfateFree == true) {
                item { AttributeBadge("✓ Sulfate-Free", true) }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ingredient Breakdown",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                beauty.harmfulIngredients?.let {
                    DetailRow("Harmful Ingredients", "${it.size} found")
                    if (it.isNotEmpty()) {
                        Text(
                            text = it.joinToString(", "),
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
                
                beauty.allergens?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Allergens", it.joinToString(", "))
                }
            }
        }
    }
}

@Composable
fun AttributeBadge(text: String, isPositive: Boolean) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = if (isPositive) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = if (isPositive) Color(0xFF4CAF50) else Color.Red,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun BuyLinksSection(
    product: Product,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Where to Buy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Amazon button
            OutlinedButton(
                onClick = {
                    val searchQuery = "${product.name} ${product.brands ?: ""}".trim()
                    val url = "https://www.amazon.in/s?k=${searchQuery.replace(" ", "+")}"
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🛒 Search on Amazon", fontSize = 15.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Flipkart button
            OutlinedButton(
                onClick = {
                    val searchQuery = "${product.name} ${product.brands ?: ""}".trim()
                    val url = "https://www.flipkart.com/search?q=${searchQuery.replace(" ", "+")}"
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🛒 Search on Flipkart", fontSize = 15.sp)
            }
            
            // Add Nykaa for beauty products
            if (product.category == ProductCategory.BEAUTY || product.category == ProductCategory.PERSONAL_CARE) {
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = {
                        val searchQuery = "${product.name} ${product.brands ?: ""}".trim()
                        val url = "https://www.nykaa.com/search/result/?q=${searchQuery.replace(" ", "+")}"
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("💄 Search on Nykaa", fontSize = 15.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Note: We may earn a commission from purchases",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun HighlightedIngredientsText(
    ingredientsText: String,
    harmfulIngredients: List<String>
) {
    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        val lowerIngredients = ingredientsText.lowercase()
        
        // Create a list of harmful ingredient positions
        val highlightRanges = mutableListOf<Pair<Int, Int>>()
        
        harmfulIngredients.forEach { harmful ->
            val lowerHarmful = harmful.lowercase()
            var startIndex = lowerIngredients.indexOf(lowerHarmful, currentIndex)
            
            while (startIndex != -1) {
                highlightRanges.add(Pair(startIndex, startIndex + harmful.length))
                startIndex = lowerIngredients.indexOf(lowerHarmful, startIndex + harmful.length)
            }
        }
        
        // Sort ranges by start position
        val sortedRanges = highlightRanges.sortedBy { it.first }
        
        currentIndex = 0
        sortedRanges.forEach { (start, end) ->
            // Add normal text before the harmful ingredient
            if (currentIndex < start) {
                append(ingredientsText.substring(currentIndex, start))
            }
            
            // Add highlighted harmful ingredient
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(ingredientsText.substring(start, end))
            }
            
            currentIndex = end
        }
        
        // Add remaining text
        if (currentIndex < ingredientsText.length) {
            append(ingredientsText.substring(currentIndex))
        }
    }
    
    Text(
        text = annotatedString,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
}

