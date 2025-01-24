package com.pam.pertemuan12.view.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pam.pertemuan12.costumwidget.FooterMenu
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.navigation.DestinasiNavigasi

object DestinasiSplash : DestinasiNavigasi {
    override val route = "splash"
    override val titleRes = "Daftar Menu"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    onHomeClick: () -> Unit,
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
                .background(Color(0xFFF5F5F5)) // Warna latar belakang lebih soft
        ) {
            TopAppBar(
                title = DestinasiSplash.titleRes,
                canNavigateBack = false,
                navigateUp = {},
                onRefresh = {},
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(60.dp)) // Jarak 16.dp dari TopAppBar

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF6200EA),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoumy),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 16.dp)
                    )
                    Text(
                        text = "Perpustakaan App",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(70.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally)
                    ) {
                        AnimatedButton(text = "Anggota", imageRes = R.drawable.logoanggota, onClick = onAnggotaClick)
                        AnimatedButton(text = "Buku", imageRes = R.drawable.logobuku, onClick = onBukuClick)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally)
                    ) {
                        AnimatedButton(text = "Peminjaman", imageRes = R.drawable.logotanggal, onClick = onPeminjamanClick)
                        AnimatedButton(text = "Pengembalian", imageRes = R.drawable.tanggalpengembalian, onClick = onPengembalianClick)
                    }
                }
            }

            FooterMenu(
                onHomeClick = onHomeClick, // Tambahkan navigasi yang sesuai
                onBukuClick = onBukuClick,
                onAnggotaClick = onAnggotaClick,
                onPeminjamanClick = onPeminjamanClick,
                onPengembalianClick = onPengembalianClick
            )
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
            painter = painterResource(id = R.drawable.logoumy),
            contentDescription = "Logo",
            modifier = Modifier
                .size(120.dp) // Ukuran gambar lebih besar
                .scale(scale)
        )
        Text(
            text = "Aplikasi Perpustakaan",
            color = Color.White,
            fontSize = 32.sp, // Ukuran teks lebih besar
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 50.dp)
        )
    }
}

@Composable
fun AnimatedButton(text: String, imageRes: Int, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EA), // Warna ungu tua
            contentColor = Color.White
        ),
        modifier = Modifier
            .width(170.dp) // Lebar tombol diperbesar
            .height(120.dp) // Tinggi tombol diperbesar
            .clickable { isPressed = !isPressed }
            .background(
                color = Color(0xFF6200EA),
                shape = RoundedCornerShape(16.dp) // Sudut tombol melengkung lebih besar
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp) // Ukuran gambar di tombol diperbesar
                    .padding(bottom = 12.dp) // Jarak antara gambar dan teks lebih besar
            )
            Text(
                text = text,
                fontSize = 18.sp, // Ukuran teks diperbesar
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
