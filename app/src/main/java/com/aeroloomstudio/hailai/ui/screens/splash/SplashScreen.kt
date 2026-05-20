package com.aeroloomstudio.hailai.ui.screens.splash

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aeroloomstudio.hailai.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Gradient colors inspired by Hail AI theme
    val color1 = Color(0xFF93B4F5)
    val color2 = Color(0xFFC4A1E0)
    val color3 = Color(0xFFF0A8C4)
    val color4 = Color(0xFF8DD8C4)

    // 1. Gradient falling animation
    val gradientOffset by animateFloatAsState(
        targetValue = if (startAnimation) 2000f else -1500f,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
        label = "gradient_fall"
    )

    // 2. Icon Drop & Scale animation from the notch
    val iconOffsetY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -1000f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "icon_drop"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.1f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "icon_scale"
    )

    // 3. Text Fade In
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, delayMillis = 1500, easing = LinearEasing),
        label = "text_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        
        // Play the custom generated drop sound in the background just before impact
        launch(Dispatchers.IO) {
            delay(350) 
            playSciFiDropSound()
        }
        
        delay(3000) // Show splash for 3 seconds
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Falling Gradient Background without any boxy edges
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            0.0f to Color.White,
                            0.2f to color1.copy(alpha = 0.5f),
                            0.4f to color2.copy(alpha = 0.6f),
                            0.6f to color3.copy(alpha = 0.4f),
                            0.8f to color4.copy(alpha = 0.2f),
                            1.0f to Color.White,
                            // Animate the brush coordinates directly, preventing any boxy edges!
                            startY = gradientOffset - size.height,
                            endY = gradientOffset + size.height
                        )
                    )
                }
        )

        // Center Image (Drops from notch, bounces, scales up)
        Image(
            painter = painterResource(id = R.drawable.splash_image),
            contentDescription = "Hail AI Logo",
            modifier = Modifier
                .size(288.dp)
                .align(Alignment.Center)
                .offset { IntOffset(0, iconOffsetY.toInt()) }
                .scale(iconScale)
        )

        // Bottom Branding
        Text(
            text = "Developed by Aeroloom Studio",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF757575), // Sleek solid gray instead of gradient
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .systemBarsPadding()
                .alpha(textAlpha)
        )
    }
}

/**
 * Generates a cool Sci-Fi "drop" sound effect mathematically using a byte array
 * and plays it via AudioTrack. This requires no external audio files!
 */
private fun playSciFiDropSound() {
    val sampleRate = 44100
    // A classic "bloop" water drop sound (exponential frequency sweep upward)
    val durationMs = 150
    val numSamples = (durationMs * sampleRate) / 1000
    val generatedSnd = ByteArray(2 * numSamples)
    
    for (i in 0 until numSamples) {
        val progress = i.toDouble() / numSamples
        val env = Math.exp(-progress * 5.0) // Fast exponential decay
        
        // Frequency sweeps exponentially from 400Hz up to 1200Hz
        val currentFreq = 400.0 * Math.exp(progress * 1.5)
        val angle = 2.0 * Math.PI * i / (sampleRate / currentFreq)
        
        val sample = (Math.sin(angle) * 20000 * env).toInt().toShort()
        
        generatedSnd[2 * i] = (sample.toInt() and 0x00FF).toByte()
        generatedSnd[2 * i + 1] = ((sample.toInt() and 0xFF00) shr 8).toByte()
    }
    
    try {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(generatedSnd.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
            
        audioTrack.write(generatedSnd, 0, generatedSnd.size)
        audioTrack.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
