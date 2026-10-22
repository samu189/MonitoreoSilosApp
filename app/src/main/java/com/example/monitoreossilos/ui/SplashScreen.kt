
package com.example.monitoreossilos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreossilos.ui.theme.AzulPrincipal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulPrincipal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Warehouse,
                contentDescription = "Logo SiloControl",
                tint = Color.White,
                modifier = Modifier.height(90.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "SiloControl",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Monitoreo inteligente de alimento",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            Text(
                text = "Controla tus silos y supervisa los niveles de alimento",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Color.White
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Iniciando SiloControl...",
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}
