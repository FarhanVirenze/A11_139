package com.pam.pertemuan12.view.Pengembalian

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pam.pertemuan12.R
import com.pam.pertemuan12.costumwidget.FooterMenu
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.Pengembalian.HomePengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.HomePengembalianUiState
import com.pam.pertemuan12.viewmodel.Pengembalian.PenyediaPengembalianViewModel

object DestinasiPengembalianHome : DestinasiNavigasi {
    override val route = "pengembaliann_home"
    override val titleRes = "Daftar Pengembalian"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePengembalianScreen(
    navigateToltemEntry: () -> Unit,
    onHomeClick: () -> Unit,
    onAnggotaClick: () -> Unit,
    onBukuClick: () -> Unit,
    onPeminjamanClick: () -> Unit,
    onPengembalianClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomePengembalianViewModel = viewModel(factory = PenyediaPengembalianViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val showDialog = remember { mutableStateOf(false) }
    val pengembalianToDelete = remember { mutableStateOf<Pengembalian?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    // Get the pengembalian data when the screen is loaded
    val homeUiState = viewModel.pgnUiState

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiPengembalianHome.titleRes,
                navigateUp = navigateBack,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getPgn()  // Refresh data
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToltemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Pengembalian")
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

        // UI elements for search, displaying pengembalian data, and handling loading/error states
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchBar(
                query = searchQuery.value,
                onQueryChange = { searchQuery.value = it }
            )

            Text(
                text = "Note : Denda Rp. 1.000 per hari jika tanggal dikembalikan lewat dari tanggal pengembalian yang telah ditentukan",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )

            HomeStatus(
                homeUiState = homeUiState,
                searchQuery = searchQuery.value,
                retryAction = { viewModel.getPgn() },
                modifier = Modifier.fillMaxSize(),
                onDetailClick = onDetailClick,
                onDeleteClick = {
                    pengembalianToDelete.value = it
                    showDialog.value = true
                }
            )
        }

        // Confirm delete dialog
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Apakah Anda yakin ingin menghapus pengembalian ini?") },
                confirmButton = {
                    Button(onClick = {
                        pengembalianToDelete.value?.let { pgn ->
                            viewModel.deletePgn(pgn.id_pengembalian)
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
        label = { Text("Cari pengembalian...") },
        singleLine = true
    )
}

@Composable
fun HomeStatus(
    homeUiState: HomePengembalianUiState,
    searchQuery: String,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Pengembalian) -> Unit = {},
    onDetailClick: (String) -> Unit
) {
    when (homeUiState) {
        is HomePengembalianUiState.Loading -> OnLoading(modifier = modifier)
        is HomePengembalianUiState.Success -> {
            val filteredPengembalian = homeUiState.pengembalian.filter {
                it.id_pengembalian.contains(searchQuery, ignoreCase = true) ||
                        it.id_peminjaman.contains(searchQuery, ignoreCase = true)
            }

            if (filteredPengembalian.isEmpty()) {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada pengembalian yang sesuai pencarian")
                }
            } else {
                PgnLayout(
                    pengembalian = filteredPengembalian,
                    modifier = Modifier.fillMaxWidth(),
                    onDetailClick = { onDetailClick(it.id_pengembalian) },
                    onDeleteClick = onDeleteClick
                )
            }
        }
        is HomePengembalianUiState.Error -> OnError(retryAction, modifier = modifier)
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
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = "Add Buku"
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction){
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun PgnLayout(
    pengembalian: List<Pengembalian>,
    modifier: Modifier = Modifier,
    onDetailClick: (Pengembalian) -> Unit,
    onDeleteClick: (Pengembalian) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pengembalian) { pengembalian ->
            PgnCard(
                pengembalian = pengembalian,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetailClick(pengembalian) },
                onDeleteClick = { onDeleteClick(pengembalian) }
            )
        }
    }
}

@Composable
fun PgnCard(
    pengembalian: Pengembalian,
    modifier: Modifier = Modifier,
    onDeleteClick: (Pengembalian) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(2.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = pengembalian.id_pengembalian,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDeleteClick(pengembalian) }) {
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
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "ID Peminjaman")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = pengembalian.id_peminjaman,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Face, contentDescription = "Nama")
                Spacer(modifier = Modifier.padding(4.dp))
                pengembalian.nama?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Tanggal Dikembalikan")
                Spacer(modifier = Modifier.padding(4.dp))
                pengembalian.tanggal_peminjaman?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Tanggal Dikembalikan")
                Spacer(modifier = Modifier.padding(4.dp))
                pengembalian.tanggal_pengembalian?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Tanggal Dikembalikan")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = pengembalian.tanggal_dikembalikan,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            pengembalian.denda?.let { denda ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "Denda")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "Denda: $denda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
