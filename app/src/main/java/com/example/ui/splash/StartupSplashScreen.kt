package com.example.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun StartupSplashScreen(
    onEnterApp: () -> Unit
) {
    val syncStages = listOf(
        "Syncing MH & LH Mess Schedules...",
        "Fetching Today's Feast Alerts...",
        "Connecting to Campus Grubs...",
        "Ready! Welcome to VIT AP Grub."
    )

    var currentStageIndex by remember { mutableIntStateOf(0) }
    val progress = remember { Animatable(0.15f) }

    // Pulsing steam / glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    LaunchedEffect(Unit) {
        for (i in syncStages.indices) {
            currentStageIndex = i
            progress.animateTo(
                targetValue = (i + 1) / syncStages.size.toFloat(),
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
            delay(650)
        }
        delay(400)
        onEnterApp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF261204),
                        Color(0xFF0F172A),
                        Color(0xFF080D1A)
                    ),
                    radius = 1200f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("startup_splash_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top University Badge
            Surface(
                color = Color(0x22FFA726),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FF6B00)),
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🏛️ VIT-AP UNIVERSITY • AMARAVATI",
                        color = Color(0xFFFFA726),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Center Branding & Glow Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Ambient radial glow behind icon
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B00).copy(alpha = glowAlpha),
                                        Color(0xFFFFA726).copy(alpha = glowAlpha * 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Logo image with fallback icon
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFFFF6B00), CircleShape),
                        color = Color(0xFF080D1A)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.grub_logo),
                            contentDescription = "VIT AP Grub Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "VIT AP Grub",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Campus Mess & Food Hub",
                    fontSize = 15.sp,
                    color = Color(0xFFFFA726),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Formerly VIT-AP Bites",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Bottom Progress & Enter Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = syncStages[currentStageIndex],
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFFF6B00),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onEnterApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B00)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .testTag("enter_app_button")
                ) {
                    Text(
                        text = "Enter App 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
