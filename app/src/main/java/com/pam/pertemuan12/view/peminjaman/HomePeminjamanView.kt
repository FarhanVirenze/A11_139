package com.pam.pertemuan12.view.peminjaman

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pam.pertemuan12.R
import com.pam.pertemuan12.costumwidget.FooterMenu
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.viewmodel.Pengembalian.HomePengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.PenyediaPengembalianViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.HomePeminjamanUiState
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.Pengembalian.HomePengembalianUiState
import com.pam.pertemuan12.viewmodel.peminjaman.HomePeminjamanViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.PenyediaPeminjamanViewModel

object DestinasiPeminjamanHome : DestinasiNavigasi {
    override val route = "peminjaman_home"
    override val titleRes = "Daftar Peminjaman"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePeminjamanScreen(
    navigateToltemEntry: () -> Unit,
    onHomeClick: () -> Unit,
    onAnggotaClick: () -> Unit,
    onBukuClick: () -> Unit,
    onPeminjamanClick: () -> Unit,
    onPengembalianClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomePeminjamanViewModel = viewModel(factory = PenyediaPeminjamanViewModel.Factory),
    viewModelPgn: HomePengembalianViewModel = viewModel(factory = PenyediaPengembalianViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val showDialog = remember { mutableStateOf(false) }
    val peminjamanToDelete = remember { mutableStateOf<Peminjaman?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    val pengembalianUiState = viewModelPgn.pgnUiState

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiPeminjamanHome.titleRes,
                navigateUp = navigateBack,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getPjm()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToltemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Peminjaman")
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
        when {
            pengembalianUiState is HomePengembalianUiState.Loading -> {
                Text(
                    text = "Memuat data...",
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }

            pengembalianUiState is HomePengembalianUiState.Error -> {
                Text(
                    text = "Gagal memuat data.",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }

            pengembalianUiState is HomePengembalianUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                ) {
                    SearchBar(
                        query = searchQuery.value,
                        onQueryChange = { searchQuery.value = it }
                    )
                    HomeStatus(
                        homeUiState = viewModel.pjmUiState,
                        pengembalianList = pengembalianUiState.pengembalian,
                        searchQuery = searchQuery.value,
                        retryAction = { viewModel.getPjm() },
                        modifier = Modifier.fillMaxSize(),
                        onDetailClick = onDetailClick,
                        onDeleteClick = {
                            peminjamanToDelete.value = it
                            showDialog.value = true
                        }
                    )
                }
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Apakah Anda yakin ingin menghapus buku ini?") },
                confirmButton = {
                    Button(onClick = {
                        peminjamanToDelete.value?.let { pjm ->
                            viewModel.deletePjm(pjm.id_peminjaman)
                        }
                        showDialog.value = false
                    }) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        label = { Text("Cari peminjaman...") },
        singleLine = true
    )
}

@Composable
fun HomeStatus(
    homeUiState: HomePeminjamanUiState,
    pengembalianList: List<Pengembalian>,
    searchQuery: String,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Peminjaman) -> Unit = {},
    onDetailClick: (String) -> Unit
) {
    when (homeUiState) {
        is HomePeminjamanUiState.Loading -> OnLoading(modifier = modifier)
        is HomePeminjamanUiState.Success -> {
            val filteredPeminjaman = homeUiState.peminjaman.filter {
                it.status == "Aktif" && (
                        it.id_buku.contains(searchQuery, ignoreCase = true) ||
                                it.id_anggota.contains(searchQuery, ignoreCase = true) ||
                                it.tanggal_peminjaman.contains(searchQuery, ignoreCase = true)
                        )
            }

            if (filteredPeminjaman.isEmpty()) {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada peminjaman yang sesuai pencarian")
                }
            } else {
                PjmLayout(
                    peminjaman = filteredPeminjaman,
                    pengembalianList = pengembalianList,  // Pass the filtered pengembalian list here
                    modifier = Modifier.fillMaxWidth(),
                    onDetailClick = { onDetailClick(it.id_peminjaman) },
                    onDeleteClick = onDeleteClick
                )
            }
        }
        is HomePeminjamanUiState.Error -> OnError(retryAction, modifier = modifier)
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription =  stringResource(R.string.loading)
    )
}

@Composable
fun OnError(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction){
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun PjmLayout(
    peminjaman: List<Peminjaman>,
    pengembalianList: List<Pengembalian>,
    modifier: Modifier = Modifier,
    onDetailClick: (Peminjaman) -> Unit,
    onDeleteClick: (Peminjaman) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(peminjaman) { peminjaman ->
            // Find corresponding pengembalian
            val pengembalian = pengembalianList.find {
                it.id_peminjaman == peminjaman.id_peminjaman
            }

            PjmCard(
                peminjaman = peminjaman,
                pengembalian = pengembalian,  // Pass the matched pengembalian object here
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetailClick(peminjaman) },
                onDeleteClick = { onDeleteClick(peminjaman) }
            )
        }
    }
}

@Composable
fun PjmCard(
    peminjaman: Peminjaman,
    pengembalian: Pengembalian?,
    modifier: Modifier = Modifier,
    onDeleteClick: (Peminjaman) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = peminjaman.id_peminjaman,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDeleteClick(peminjaman) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "ID_BUKU")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = peminjaman.id_anggota,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Penulis")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = peminjaman.id_buku,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Kategori")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = peminjaman.tanggal_peminjaman,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Status")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = peminjaman.tanggal_pengembalian,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "statusBku")
                Spacer(modifier = Modifier.width(4.dp))
                peminjaman.status?.let { status ->
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
