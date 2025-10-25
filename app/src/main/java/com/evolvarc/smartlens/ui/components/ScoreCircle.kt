package com.evolvarc.smartlens.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase
import com.evolvarc.smartlens.util.getScoreColor
import com.evolvarc.smartlens.util.getScoreLabel

@Composable
fun ScoreCircle(
    score: Int,
    nutriScoreGrade: String? = null,
    dataAvailability: CalculateHealthScoreUseCase.DataAvailability = CalculateHealthScoreUseCase.DataAvailability.COMPLETE,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 16.dp,
    animationDuration: Int = 1000
) {
    // Show N/A badge for insufficient data
    if (dataAvailability == CalculateHealthScoreUseCase.DataAvailability.INSUFFICIENT) {
        Card(
            modifier = modifier.size(size),
            shape = RoundedCornerShape(size / 2),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "N/A",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Not Scored",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        return
    }
    var animationPlayed by remember { mutableStateOf(false) }
    val currentPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) score / 100f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "score_animation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    val scoreColor = getScoreColor(score)
    val scoreLabel = getScoreLabel(score)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            
            drawArc(
                color = scoreColor,
                startAngle = -90f,
                sweepAngle = 360f * currentPercentage.value,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = (currentPercentage.value * 100).toInt().toString(),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
            Text(
                text = scoreLabel,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = scoreColor
            )
            
            // Show NutriScore if available
            nutriScoreGrade?.let { grade ->
                Spacer(modifier = Modifier.height(8.dp))
                NutriScoreBadge(grade = grade)
            }
        }
    }
}

@Composable
fun NutriScoreBadge(grade: String, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (grade.uppercase()) {
        "A" -> Color(0xFF038141) to Color.White
        "B" -> Color(0xFF85BB2F) to Color.White
        "C" -> Color(0xFFFECB02) to Color.Black
        "D" -> Color(0xFFEE8100) to Color.White
        "E" -> Color(0xFFE63E11) to Color.White
        else -> Color.Gray to Color.White
    }
    
    Box(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "NutriScore ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.9f)
            )
            Text(
                text = grade.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun NutriScoreBadgeLarge(grade: String, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (grade.uppercase()) {
        "A" -> Color(0xFF038141) to Color.White
        "B" -> Color(0xFF85BB2F) to Color.White
        "C" -> Color(0xFFFECB02) to Color.Black
        "D" -> Color(0xFFEE8100) to Color.White
        "E" -> Color(0xFFE63E11) to Color.White
        else -> Color.Gray to Color.White
    }
    
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nutriscore_scale"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 32.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Grade: ",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.95f)
            )
            Text(
                text = grade.uppercase(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

