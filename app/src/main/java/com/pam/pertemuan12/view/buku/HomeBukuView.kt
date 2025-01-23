package com.pam.pertemuan12.view.buku

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.LaunchedEffect
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
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.buku.HomeBukuViewModel
import com.pam.pertemuan12.viewmodel.buku.HomeUiState
import com.pam.pertemuan12.viewmodel.buku.PenyediaBukuViewModel

object DestinasiBukuHome : DestinasiNavigasi {
    override val route = "buku_home"
    override val titleRes = "Daftar Buku"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToltemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeBukuViewModel = viewModel(factory = PenyediaBukuViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val showDialog = remember { mutableStateOf(false) }
    val bukuToDelete = remember { mutableStateOf<Buku?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    // Memuat ulang daftar buku setelah pengembalian
    LaunchedEffect(Unit) {
        viewModel.getBku() // Memuat ulang daftar buku saat layar pertama kali diload
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiBukuHome.titleRes,
                navigateUp = navigateBack,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getBku()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToltemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mahasiswa")
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)) {
            SearchBar(
                query = searchQuery.value,
                onQueryChange = { searchQuery.value = it }
            )
            HomeStatus(
                homeUiState = viewModel.bkuUiState,
                searchQuery = searchQuery.value,
                retryAction = { viewModel.getBku() },
                modifier = Modifier.fillMaxSize(),
                onDetailClick = onDetailClick,
                onDeleteClick = {
                    bukuToDelete.value = it
                    showDialog.value = true
                }
            )
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Apakah Anda yakin ingin menghapus buku ini?") },
                confirmButton = {
                    Button(onClick = {
                        bukuToDelete.value?.let { bku ->
                            viewModel.deleteBku(bku.id_buku)
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
        label = { Text("Cari buku...") },
        singleLine = true
    )
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    searchQuery: String,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Buku) -> Unit = {},
    onDetailClick: (String) -> Unit
) {
    when (homeUiState) {
        is HomeUiState.Loading -> OnLoading(modifier = modifier)
        is HomeUiState.Success -> {
            val filteredBuku = homeUiState.buku.filter {
                it.judul.contains(searchQuery, ignoreCase = true) ||
                        it.penulis.contains(searchQuery, ignoreCase = true) ||
                        it.kategori.contains(searchQuery, ignoreCase = true)
            }

            if (filteredBuku.isEmpty()) {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada buku yang sesuai pencarian")
                }
            } else {
                BkuLayout(
                    buku = filteredBuku,
                    modifier = Modifier.fillMaxWidth(),
                    onDetailClick = { onDetailClick(it.id_buku) },
                    onDeleteClick = onDeleteClick
                )
            }
        }
        is HomeUiState.Error -> OnError(retryAction, modifier = modifier)
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
fun BkuLayout(
    buku: List<Buku>,
    modifier: Modifier = Modifier,
    onDetailClick: (Buku) -> Unit,
    onDeleteClick: (Buku) -> Unit = {}
){
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(buku){ buku ->
            BkuCard(
                buku = buku,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{ onDetailClick(buku) },
                onDeleteClick = {
                    onDeleteClick(buku)
                }
            )
        }
    }
}

@Composable
fun BkuCard(
    buku: Buku,
    modifier: Modifier = Modifier,
    onDeleteClick: (Buku) -> Unit = {}
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
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = buku.judul,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDeleteClick(buku) }) {
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
                Icon(imageVector = Icons.Filled.Info, contentDescription = "ID_BUKU")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = buku.id_buku,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Penulis")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = buku.penulis,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Kategori")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = buku.kategori,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "Status")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = buku.status,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}