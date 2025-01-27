package com.pam.pertemuan12.view.peminjaman

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Anggota
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.anggota.HomeAnggotaUiState
import com.pam.pertemuan12.viewmodel.anggota.HomeAnggotaViewModel
import com.pam.pertemuan12.viewmodel.anggota.PenyediaAnggotaViewModel
import com.pam.pertemuan12.viewmodel.buku.HomeUiState
import com.pam.pertemuan12.viewmodel.buku.HomeBukuViewModel
import com.pam.pertemuan12.viewmodel.buku.PenyediaBukuViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.UpdateUiEvent
import com.pam.pertemuan12.viewmodel.peminjaman.UpdateUiState
import com.pam.pertemuan12.viewmodel.peminjaman.PenyediaPeminjamanViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.UpdatePeminjamanViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

object DestinasiPeminjamanUpdate : DestinasiNavigasi {
    override val route = "peminjaman_update"
    override val titleRes = "Update Peminjaman"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePeminjamanView(
    id_peminjaman: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdatePeminjamanViewModel = viewModel(factory = PenyediaPeminjamanViewModel.Factory),
    viewModelBku: HomeBukuViewModel = viewModel(factory = PenyediaBukuViewModel.Factory),
    viewModelAgo: HomeAnggotaViewModel = viewModel(factory = PenyediaAnggotaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Mengambil UI State
    val bukuUiState = viewModelBku.bkuUiState
    val anggotaUiState = viewModelAgo.agoUiState

    LaunchedEffect(id_peminjaman) {
        viewModel.loadPeminjaman(id_peminjaman)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiPeminjamanUpdate.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        when {
            bukuUiState is HomeUiState.Loading || anggotaUiState is HomeAnggotaUiState.Loading -> {
                Text(
                    text = "Memuat data...",
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }
            bukuUiState is HomeUiState.Error || anggotaUiState is HomeAnggotaUiState.Error -> {
                Text(
                    text = "Gagal memuat data.",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }
            bukuUiState is HomeUiState.Success && anggotaUiState is HomeAnggotaUiState.Success -> {
                UpdateBody(
                    updateUiState = viewModel.uiState,
                    bukuList = bukuUiState.buku,
                    anggotaList = anggotaUiState.anggota,
                    onBukuValueChange = viewModel::updatePeminjamanState,
                    onSaveClick = {
                        coroutineScope.launch {
                            viewModel.updatePeminjaman(id_peminjaman)
                            navigateBack()
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
fun UpdateBody(
    updateUiState: UpdateUiState,
    bukuList: List<Buku>,
    anggotaList: List<Anggota>,
    onBukuValueChange: (UpdateUiEvent) -> Unit,
    onSaveClick: () -> Unit,
    isSaveEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier.padding(12.dp)
    ) {
        FormInput(
            updateUiEvent = updateUiState.updateUiEvent,
            bukuList = bukuList,
            anggotaList = anggotaList,
            onValueChange = onBukuValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = isSaveEnabled
        ) {
            Text(text = "Update")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    updateUiEvent: UpdateUiEvent,
    bukuList: List<Buku>,
    anggotaList: List<Anggota>,
    modifier: Modifier = Modifier,
    onValueChange: (UpdateUiEvent) -> Unit = {},
    enabled: Boolean = true
) {
    var bukuExpanded by remember { mutableStateOf(false) }
    var anggotaExpanded by remember { mutableStateOf(false) }

    // Inisialisasi selectedBuku dan selectedAnggota berdasarkan updateUiEvent
    var selectedBuku by remember { mutableStateOf(updateUiEvent.id_buku) }
    var selectedAnggota by remember { mutableStateOf(updateUiEvent.id_anggota) }

    // Filter the bukuList to include only books that are "Tersedia"
    val availableBooks = bukuList.filter { it.status == "Tersedia" }

    // State for DatePicker dialogs
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Dialog for Tanggal Peminjaman
    val datePickerPeminjaman = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = "${dayOfMonth.toString().padStart(2, '0')}/${(month + 1).toString().padStart(2, '0')}/$year"
            onValueChange(updateUiEvent.copy(tanggal_peminjaman = formattedDate))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Dialog for Tanggal Pengembalian
    val datePickerPengembalian = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = "${dayOfMonth.toString().padStart(2, '0')}/${(month + 1).toString().padStart(2, '0')}/$year"
            onValueChange(updateUiEvent.copy(tanggal_pengembalian = formattedDate))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Update selectedBuku dan selectedAnggota jika updateUiEvent berubah
    LaunchedEffect(updateUiEvent) {
        selectedBuku = updateUiEvent.id_buku
        selectedAnggota = updateUiEvent.id_anggota
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Input ID Peminjaman
        OutlinedTextField(
            value = updateUiEvent.id_peminjaman,
            onValueChange = { onValueChange(updateUiEvent.copy(id_peminjaman = it)) },
            label = { Text("ID Peminjaman") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = updateUiEvent.id_peminjaman.isBlank()
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
                availableBooks.forEach { buku ->  // Use filtered books
                    DropdownMenuItem(
                        text = { Text(buku.id_buku) },
                        onClick = {
                            selectedBuku = buku.id_buku
                            onValueChange(updateUiEvent.copy(id_buku = buku.id_buku))
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
                value = selectedAnggota,
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
                            selectedAnggota = anggota.nama
                            onValueChange(updateUiEvent.copy(id_anggota = anggota.nama))
                            anggotaExpanded = false
                        }
                    )
                }
            }
        }

        // Input Tanggal Peminjaman
        OutlinedTextField(
            value = updateUiEvent.tanggal_peminjaman,
            onValueChange = {},
            label = { Text("Tanggal Peminjaman") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled) { datePickerPeminjaman.show() },
            enabled = false, // Disable manual input
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { datePickerPeminjaman.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            isError = updateUiEvent.tanggal_peminjaman.isBlank()
        )

        // Input Tanggal Pengembalian
        OutlinedTextField(
            value = updateUiEvent.tanggal_pengembalian,
            onValueChange = {},
            label = { Text("Tanggal Pengembalian") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled) { datePickerPengembalian.show() },
            enabled = false, // Disable manual input
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { datePickerPengembalian.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            isError = updateUiEvent.tanggal_pengembalian.isBlank()
        )
    }
}


