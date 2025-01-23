package com.pam.pertemuan12.view.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pam.pertemuan12.R
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.navigation.DestinasiNavigasi

object DestinasiSplash : DestinasiNavigasi {
    override val route = "splash"
    override val titleRes = "Daftar Menu"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    onAnggotaClick: () -> Unit,
    onBukuClick: () -> Unit,
    onPeminjamanClick: () -> Unit,
    onPengembalianClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var scale by remember { mutableStateOf(0.5f) }

    if (showSplash) {
        LaunchedEffect(Unit) {
            scale = 1.5f
            kotlinx.coroutines.delay(3000)
            showSplash = false
        }
        SplashScreen(scale)
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
        ) {
            TopAppBar(
                title = DestinasiSplash.titleRes,
                canNavigateBack = false,
                navigateUp = {},
                onRefresh = {},
                modifier = Modifier
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedButton(text = "Anggota", imageRes = R.drawable.logoanggota, onClick = onAnggotaClick)
                AnimatedButton(text = "Buku", imageRes = R.drawable.logobuku, onClick = onBukuClick)
                AnimatedButton(text = "Peminjaman", imageRes = R.drawable.logotanggal, onClick = onPeminjamanClick)
                AnimatedButton(text = "Pengembalian", imageRes = R.drawable.logotanggal, onClick = onPengembalianClick)
            }
        }
    }
}

@Composable
fun SplashScreen(scale: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6200EA)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoperpus),
            contentDescription = "Logo",
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
        )
        Text(
            text = "Aplikasi Perpustakaan",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun AnimatedButton(text: String, imageRes: Int, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EA),
            contentColor = Color.White
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .height(90.dp)
            .fillMaxWidth(0.9f)
            .clickable { isPressed = !isPressed }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(50.dp)
            )
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .animateScale(isPressed)
            )
        }
    }
}

@Composable
fun Modifier.animateScale(isPressed: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 300)
    )
    return this.then(Modifier.scale(scale))
}
