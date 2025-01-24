package com.pam.pertemuan12.view.anggota

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pam.pertemuan12.costumwidget.FooterMenu
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.view.buku.OnError
import com.pam.pertemuan12.view.buku.OnLoading
import com.pam.pertemuan12.viewmodel.anggota.HomeAnggotaViewModel
import com.pam.pertemuan12.viewmodel.anggota.HomeAnggotaUiState
import com.pam.pertemuan12.viewmodel.anggota.PenyediaAnggotaViewModel

object DestinasiAnggotaHome : DestinasiNavigasi {
    override val route = "anggota_home"
    override val titleRes = "Daftar Anggota"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAnggotaScreen(
    navigateToltemEntry: () -> Unit,
    onHomeClick: () -> Unit,
    onAnggotaClick: () -> Unit,
    onBukuClick: () -> Unit,
    onPeminjamanClick: () -> Unit,
    onPengembalianClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeAnggotaViewModel = viewModel(factory = PenyediaAnggotaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val showDialog = remember { mutableStateOf(false) }
    val anggotaToDelete = remember { mutableStateOf<Anggota?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = "Daftar Anggota",
                navigateUp = navigateBack,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                onRefresh = { viewModel.getAgo() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToltemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Anggota")
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
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchBar(
                query = searchQuery.value,
                onQueryChange = { searchQuery.value = it }
            )
            HomeStatus(
                homeUiState = viewModel.agoUiState,
                searchQuery = searchQuery.value,
                retryAction = { viewModel.getAgo() },
                modifier = Modifier.fillMaxSize(),
                onDetailClick = onDetailClick,
                onDeleteClick = {
                    anggotaToDelete.value = it
                    showDialog.value = true
                }
            )
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Apakah Anda yakin ingin menghapus anggota ini?") },
                confirmButton = {
                    Button(onClick = {
                        anggotaToDelete.value?.let { anggota ->
                            viewModel.deleteAgo(anggota.id_anggota)
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
        label = { Text("Cari anggota...") },
        singleLine = true
    )
}

@Composable
fun HomeStatus(
    homeUiState: HomeAnggotaUiState,
    searchQuery: String,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Anggota) -> Unit = {},
    onDetailClick: (String) -> Unit
) {
    when (homeUiState) {
        is HomeAnggotaUiState.Loading -> OnLoading(modifier = modifier)
        is HomeAnggotaUiState.Success -> {
            val filteredAnggota = homeUiState.anggota.filter {
                it.nama.contains(searchQuery, ignoreCase = true) ||
                        it.email.contains(searchQuery, ignoreCase = true) ||
                        it.nomor_telepon.contains(searchQuery, ignoreCase = true)
            }

            if (filteredAnggota.isEmpty()) {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada anggota yang sesuai pencarian")
                }
            } else {
                AnggotaLayout(
                    anggota = filteredAnggota,
                    modifier = Modifier.fillMaxWidth(),
                    onDetailClick = { onDetailClick(it.id_anggota) },
                    onDeleteClick = onDeleteClick
                )
            }
        }
        is HomeAnggotaUiState.Error -> OnError(retryAction, modifier = modifier)
    }
}

@Composable
fun AnggotaLayout(
    anggota: List<Anggota>,
    modifier: Modifier = Modifier,
    onDetailClick: (Anggota) -> Unit,
    onDeleteClick: (Anggota) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(anggota) { anggota ->
            AnggotaCard(
                anggota = anggota,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetailClick(anggota) },
                onDeleteClick = {
                    onDeleteClick(anggota)
                }
            )
        }
    }
}

@Composable
fun AnggotaCard(
    anggota: Anggota,
    modifier: Modifier = Modifier,
    onDeleteClick: (Anggota) -> Unit = {}
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
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Nama")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = anggota.nama,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDeleteClick(anggota) }) {
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
                Icon(imageVector = Icons.Filled.Info, contentDescription = "ID_ANGGOTA")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = anggota.id_anggota,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = anggota.email,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Phone, contentDescription = "Nomor_Telepon")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = anggota.nomor_telepon,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
