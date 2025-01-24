package com.pam.pertemuan12.costumwidget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pam.pertemuan12.R

@Composable
fun FooterMenu(
    onAnggotaClick: () -> Unit,
    onBukuClick: () -> Unit,
    onHomeClick: () -> Unit,
    onPeminjamanClick: () -> Unit,
    onPengembalianClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6200EA)) // Warna latar belakang ungu
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FooterIconButton(iconRes = R.drawable.logoanggota, onClick = onAnggotaClick)
        FooterIconButton(iconRes = R.drawable.logobuku, onClick = onBukuClick)
        FooterIconButtonHome(iconRes = R.drawable.logohome, onClick = onHomeClick) // Home dengan desain berbeda
        FooterIconButton(iconRes = R.drawable.logotanggal, onClick = onPeminjamanClick)
        FooterIconButton(iconRes = R.drawable.tanggalpengembalian, onClick = onPengembalianClick)
    }
}

@Composable
fun FooterIconButton(iconRes: Int, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null, // Tidak memerlukan deskripsi teks
            modifier = Modifier
                .size(44.dp) // Ukuran ikon diperbesar
        )
    }
}

@Composable
fun FooterIconButtonHome(iconRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp) // Ukuran lebih besar untuk menonjolkan
            .shadow(
                elevation = 16.dp, // Memberikan bayangan
                shape = CircleShape, // Bentuk lingkaran
                clip = false
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFC107), Color(0xFFFF9800)) // Gradien oranye keemasan
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null, // Tidak memerlukan deskripsi teks
            modifier = Modifier.size(44.dp) // Ukuran ikon Home sedikit lebih kecil agar proporsional
        )
    }
}

