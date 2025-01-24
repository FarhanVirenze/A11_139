package com.pam.pertemuan12.view.Pengembalian

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
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
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.Pengembalian.DetailPengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.PenyediaPengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.DetailUiState

object DestinasiPengembalianDetail : DestinasiNavigasi {
    override val route = "pengembalian_detail"
    override val titleRes = "Detail Pengembalian"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPengembalianView(
    id_pengembalian: String,
    onHomeClick: () -> Unit,
    onAnggotaClick: () -> Unit,
    onBukuClick: () -> Unit,
    onPeminjamanClick: () -> Unit,
    onPengembalianClick: () -> Unit,
    navigateBack: () -> Unit,
    onClick: () -> Unit,
    viewModel: DetailPengembalianViewModel = viewModel(factory = PenyediaPengembalianViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    viewModel.getPengembalianById(id_pengembalian)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiPengembalianDetail.titleRes,
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getPengembalianById(id_pengembalian)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClick,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Buku")
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
            is DetailUiState.Loading -> {
                Text("Loading...", Modifier.fillMaxSize())
            }
            is DetailUiState.Success -> {
                val pengembalian = (uiState as DetailUiState.Success).pengembalian
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ItemDetailPgn(pengembalian = pengembalian)
                }
            }
            is DetailUiState.Error -> {
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
fun ItemDetailPgn(
    modifier: Modifier = Modifier,
    pengembalian: Pengembalian
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
            ComponentDetailPgn(judul = "ID Pengembalian", isinya = pengembalian.id_pengembalian)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailPgn(judul = "Buku yang ingin dikembalikan", isinya = pengembalian.id_peminjaman)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailPgn(judul = "Tanggal Dikembalikan", isinya = pengembalian.tanggal_dikembalikan)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ComponentDetailPgn(
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
