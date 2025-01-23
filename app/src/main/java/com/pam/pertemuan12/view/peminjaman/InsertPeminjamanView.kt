package com.pam.pertemuan12.view.peminjaman

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.model.Pengembalian
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.Pengembalian.HomePengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.PenyediaPengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.HomePengembalianUiState
import com.pam.pertemuan12.viewmodel.anggota.HomeAnggotaViewModel
import com.pam.pertemuan12.viewmodel.anggota.PenyediaAnggotaViewModel
import com.pam.pertemuan12.viewmodel.anggota.HomeAnggotaUiState
import com.pam.pertemuan12.viewmodel.buku.HomeUiState
import com.pam.pertemuan12.viewmodel.buku.HomeBukuViewModel
import com.pam.pertemuan12.viewmodel.buku.PenyediaBukuViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.InsertPeminjamanViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.PenyediaPeminjamanViewModel
import kotlinx.coroutines.launch

object DestinasiPeminjamanInsert : DestinasiNavigasi {
    override val route = "peminjaman_insert"
    override val titleRes = "Tambah Peminjaman"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertPeminjamanScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsertPeminjamanViewModel = viewModel(factory = PenyediaPeminjamanViewModel.Factory),
    viewModelBku: HomeBukuViewModel = viewModel(factory = PenyediaBukuViewModel.Factory),
    viewModelAgo: HomeAnggotaViewModel = viewModel(factory = PenyediaAnggotaViewModel.Factory),
    viewModelPgn: HomePengembalianViewModel = viewModel(factory = PenyediaPengembalianViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Mengambil UI State
    val bukuUiState = viewModelBku.bkuUiState
    val anggotaUiState = viewModelAgo.agoUiState
    val pengembalianUiState = viewModelPgn.pgnUiState

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiPeminjamanInsert.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        when {
            bukuUiState is HomeUiState.Loading || anggotaUiState is HomeAnggotaUiState.Loading || pengembalianUiState is HomePengembalianUiState.Loading -> {
                Text(
                    text = "Memuat data...",
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }
            bukuUiState is HomeUiState.Error || anggotaUiState is HomeAnggotaUiState.Error || pengembalianUiState is HomePengembalianUiState.Error -> {
                Text(
                    text = "Gagal memuat data.",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }
            bukuUiState is HomeUiState.Success && anggotaUiState is HomeAnggotaUiState.Success && pengembalianUiState is HomePengembalianUiState.Success -> {
                EntryBody(
                    insertUiState = viewModel.uiState,
                    bukuList = bukuUiState.buku,
                    anggotaList = anggotaUiState.anggota,
                    pengembalianList = pengembalianUiState.pengembalian,
                    onBukuValueChange = viewModel::updateInsertPjmState,
                    onSaveClick = {
                        coroutineScope.launch {
                            try {
                                // Menyimpan data peminjaman
                                viewModel.insertPjm()

                                // Refresh data buku dan peminjaman
                                viewModelPgn.getPgn()
                                viewModelBku.getBku()

                                // Navigasi kembali
                                navigateBack()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Tampilkan pesan kesalahan (opsional)
                            }
                        }
                    },
                    isSaveEnabled = viewModel.isFormValid(),
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EntryBody(
    insertUiState: InsertPeminjamanViewModel.InsertUiState,
    bukuList: List<Buku>,
    anggotaList: List<Anggota>,
    pengembalianList: List<Pengembalian>,
    onBukuValueChange: (InsertPeminjamanViewModel.InsertUiEvent) -> Unit,
    onSaveClick: () -> Unit,
    isSaveEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier.padding(12.dp)
    ) {
        FormInput(
            insertUiEvent = insertUiState.insertUiEvent,
            bukuList = bukuList,
            anggotaList = anggotaList,
            pengembalianList = pengembalianList,
            onValueChange = onBukuValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = isSaveEnabled
        ) {
            Text(text = "Simpan")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    insertUiEvent: InsertPeminjamanViewModel.InsertUiEvent,
    bukuList: List<Buku>,
    anggotaList: List<Anggota>,
    pengembalianList: List<Pengembalian>,
    modifier: Modifier = Modifier,
    onValueChange: (InsertPeminjamanViewModel.InsertUiEvent) -> Unit = {},
    enabled: Boolean = true
) {
    var bukuExpanded by remember { mutableStateOf(false) }
    var anggotaExpanded by remember { mutableStateOf(false) }
    var selectedBuku by remember { mutableStateOf(insertUiEvent.id_buku) }
    var selectedAnggota by remember { mutableStateOf(anggotaList.find { it.id_anggota == insertUiEvent.id_anggota }) }

    // Filter buku berdasarkan status "Tersedia"
    val availableBooks = bukuList.filter { it.status == "Tersedia" }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Input ID Peminjaman
        OutlinedTextField(
            value = insertUiEvent.id_peminjaman,
            onValueChange = { onValueChange(insertUiEvent.copy(id_peminjaman = it)) },
            label = { Text("ID Peminjaman") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = insertUiEvent.id_peminjaman.isBlank()
        )

        // Dropdown untuk Buku
        ExposedDropdownMenuBox(
            expanded = bukuExpanded,
            onExpandedChange = { bukuExpanded = !bukuExpanded }
        ) {
            OutlinedTextField(
                value = selectedBuku,
                onValueChange = {},
                readOnly = true,
                label = { Text("ID Buku yang ingin dipinjam") },
                trailingIcon = {
                    androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = bukuExpanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = bukuExpanded,
                onDismissRequest = { bukuExpanded = false }
            ) {
                availableBooks.forEach { buku ->
                    DropdownMenuItem(
                        text = { Text(buku.id_buku) },
                        onClick = {
                            selectedBuku = buku.id_buku
                            onValueChange(insertUiEvent.copy(id_buku = buku.id_buku))
                            bukuExpanded = false
                        }
                    )
                }
            }
        }

        // Dropdown untuk Anggota
        ExposedDropdownMenuBox(
            expanded = anggotaExpanded,
            onExpandedChange = { anggotaExpanded = !anggotaExpanded }
        ) {
            OutlinedTextField(
                value = selectedAnggota?.nama ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Nama Anggota") },
                trailingIcon = {
                    androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = anggotaExpanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = anggotaExpanded,
                onDismissRequest = { anggotaExpanded = false }
            ) {
                anggotaList.forEach { anggota ->
                    DropdownMenuItem(
                        text = { Text(anggota.nama) },
                        onClick = {
                            selectedAnggota = anggota
                            onValueChange(insertUiEvent.copy(id_anggota = anggota.nama))
                            anggotaExpanded = false
                        }
                    )
                }
            }
        }

        // Input Tanggal Peminjaman
        OutlinedTextField(
            value = insertUiEvent.tanggal_peminjaman,
            onValueChange = { onValueChange(insertUiEvent.copy(tanggal_peminjaman = it)) },
            label = { Text("Tanggal Peminjaman") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = insertUiEvent.tanggal_peminjaman.isBlank()
        )

        // Input Tanggal Pengembalian
        OutlinedTextField(
            value = insertUiEvent.tanggal_pengembalian,
            onValueChange = { onValueChange(insertUiEvent.copy(tanggal_pengembalian = it)) },
            label = { Text("Tanggal Pengembalian") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = insertUiEvent.tanggal_pengembalian.isBlank()
        )
    }
}




