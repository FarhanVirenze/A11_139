package com.pam.pertemuan12.view.buku

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
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.buku.DetailBukuViewModel
import com.pam.pertemuan12.viewmodel.buku.DetailUiState
import com.pam.pertemuan12.viewmodel.buku.PenyediaBukuViewModel

object DestinasiBukuDetail : DestinasiNavigasi {
    override val route = "buku_detail"
    override val titleRes = "Detail Buku"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(
    id_buku: String,
    navigateBack: () -> Unit,
    onClick: () -> Unit,
    viewModel: DetailBukuViewModel = viewModel(factory = PenyediaBukuViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    viewModel.getBukuById(id_buku)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiBukuDetail.titleRes,
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getBukuById(id_buku)
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
    ) { innerPadding ->
        when (uiState) {
            is DetailUiState.Loading -> {
                Text("Loading...", Modifier.fillMaxSize())
            }
            is DetailUiState.Success -> {
                val buku = (uiState as DetailUiState.Success).buku
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ItemDetailBku(buku = buku)
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
fun ItemDetailBku(
    modifier: Modifier = Modifier,
    buku: Buku
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
            ComponentDetailMhs(judul = "ID_BUKU", isinya = buku.id_buku)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailMhs(judul = "Judul", isinya = buku.judul)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailMhs(judul = "Penulis", isinya = buku.penulis)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailMhs(judul = "Kategori", isinya = buku.kategori)
            Spacer(modifier = Modifier.height(8.dp))

            ComponentDetailMhs(judul = "Status", isinya = buku.status)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ComponentDetailMhs(
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
