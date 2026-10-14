package com.example.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DisciplinaryHold

@Composable
fun DisciplinaryHoldDialog(
    hold: DisciplinaryHold,
    userAlias: String,
    onAdminLoginClick: () -> Unit
) {
    if (!hold.isActive) return
    val affectsUser = hold.targetAlias == "ALL" || hold.targetAlias.equals(userAlias, ignoreCase = true)
    if (!affectsUser) return

    Dialog(
        onDismissRequest = { /* Cannot dismiss without Admin revocation */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(20.dp))
                .testTag("disciplinary_hold_modal"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A0A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Disciplinary Alert",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ADMINISTRATIVE DISCIPLINARY HOLD",
                    color = Color(0xFFEF4444),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Issued by: ${hold.issuedBy}",
                    color = Color(0xFFFCA5A5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color(0xFF2B1010),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Target Subject: ${hold.targetAlias}",
                            color = Color(0xFFFFE4E6),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = hold.reason,
                            color = Color(0xFFFECDD3),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "This terminal lock requires administrative revocation or mess committee review.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAdminLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Admin Console Login", color = Color.White)
                }
            }
        }
    }
}
