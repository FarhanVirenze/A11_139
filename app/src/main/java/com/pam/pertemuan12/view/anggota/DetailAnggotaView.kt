package com.pam.pertemuan12.view.anggota

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pam.pertemuan12.costumwidget.FooterMenu
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.anggota.DetailAnggotaUiState
import com.pam.pertemuan12.viewmodel.anggota.DetailAnggotaViewModel
import com.pam.pertemuan12.viewmodel.anggota.PenyediaAnggotaViewModel

object DestinasiAnggotaDetail : DestinasiNavigasi {
    override val route = "anggota_detail"
    override val titleRes = "Detail Anggota"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAnggotaView(
    id_anggota: String,
    onHomeClick: () -> Unit,
    onAnggotaClick: () -> Unit,
    onBukuClick: () -> Unit,
    onPeminjamanClick: () -> Unit,
    onPengembalianClick: () -> Unit,
    navigateBack: () -> Unit,
    onClick: () -> Unit,
    viewModel: DetailAnggotaViewModel = viewModel(factory = PenyediaAnggotaViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    viewModel.getAnggotaById(id_anggota)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiAnggotaDetail.titleRes,
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getAnggotaById(id_anggota)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClick,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Anggota")
            }
        },
        bottomBar = {
            FooterMenu(
                onAnggotaClick = onAnggotaClick,
                onBukuClick = onBukuClick,
                onHomeClick = onHomeClick,
                onPeminjamanClick = onPeminjamanClick,
                onPengembalianClick = onPengembalianClick
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is DetailAnggotaUiState.Loading -> {
                Text("Loading...", Modifier.fillMaxSize())
            }
            is DetailAnggotaUiState.Success -> {
                val anggota = (uiState as DetailAnggotaUiState.Success).anggota
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ItemDetailAnggota(anggota = anggota)
                }
            }
            is DetailAnggotaUiState.Error -> {
                Text(
                    text = "Error: Gagal memuat data. Silakan coba lagi.",
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ItemDetailAnggota(
    modifier: Modifier = Modifier,
    anggota: Anggota
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ComponentDetailAnggota(judul = "ID_ANGGOTA", isinya = anggota.id_anggota)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailAnggota(judul = "Nama", isinya = anggota.nama)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailAnggota(judul = "Email", isinya = anggota.email) // Updated field name
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailAnggota(judul = "Nomor Telepon", isinya = anggota.nomor_telepon) // Updated field name
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ComponentDetailAnggota(
    modifier: Modifier = Modifier,
    judul: String,
    isinya: String,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "$judul : ",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
        )
        Text(
            text = isinya,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        )
    }
}
