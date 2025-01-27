package com.pam.pertemuan12.view.Pengembalian

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.model.Buku
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.Pengembalian.InsertPengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.InsertUiEvent
import com.pam.pertemuan12.viewmodel.Pengembalian.InsertUiState
import com.pam.pertemuan12.viewmodel.Pengembalian.PenyediaPengembalianViewModel
import com.pam.pertemuan12.viewmodel.buku.HomeUiState
import com.pam.pertemuan12.viewmodel.buku.HomeBukuViewModel
import com.pam.pertemuan12.viewmodel.buku.PenyediaBukuViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.HomePeminjamanUiState
import com.pam.pertemuan12.viewmodel.peminjaman.HomePeminjamanViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.PenyediaPeminjamanViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

object DestinasiPengembalianInsert : DestinasiNavigasi {
    override val route = "pengembalian_insert"
    override val titleRes = "Transaksi Pengembalian Buku"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertPengembalianScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsertPengembalianViewModel = viewModel(factory = PenyediaPengembalianViewModel.Factory),
    viewModelPjm: HomePeminjamanViewModel = viewModel(factory = PenyediaPeminjamanViewModel.Factory),
    viewModelBku: HomeBukuViewModel = viewModel(factory = PenyediaBukuViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val peminjamanUiState = viewModelPjm.pjmUiState
    val bukuUiState = viewModelBku.bkuUiState

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiPengembalianInsert.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        when {
            bukuUiState is HomeUiState.Loading || peminjamanUiState is HomePeminjamanUiState.Loading -> {
                Text(
                    text = "Memuat data...",
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }
            bukuUiState is HomeUiState.Error || peminjamanUiState is HomePeminjamanUiState.Error -> {
                Text(
                    text = "Gagal memuat data.",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }
            bukuUiState is HomeUiState.Success && peminjamanUiState is HomePeminjamanUiState.Success -> {
                EntryBody(
                    insertUiState = viewModel.uiState,
                    bukuList = bukuUiState.buku,
                    peminjamanList = peminjamanUiState.peminjaman,
                    onPeminjamanValueChange = viewModel::updateInsertPgnState,
                    onSaveClick = {
                        coroutineScope.launch {
                            try {
                                println("Saving Pengembalian...")

                                // Insert pengembalian data
                                viewModel.insertPgn()

                                // Refresh data
                                viewModelPjm.getPjm()
                                viewModelBku.getBku()

                                navigateBack()

                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Optional: Show error message to the user
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
    insertUiState: InsertUiState,
    bukuList: List<Buku>,
    peminjamanList: List<Peminjaman>,
    onPeminjamanValueChange: (InsertUiEvent) -> Unit,
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
            peminjamanList = peminjamanList,
            onValueChange = onPeminjamanValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = isSaveEnabled
        ) {
            Text(text = "Simpan Pengembalian")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    insertUiEvent: InsertUiEvent,
    bukuList: List<Buku>,
    peminjamanList: List<Peminjaman>,
    modifier: Modifier = Modifier,
    onValueChange: (InsertUiEvent) -> Unit = {},
    enabled: Boolean = true
) {
    var bukuExpanded by remember { mutableStateOf(false) }
    var selectedBuku by remember { mutableStateOf(insertUiEvent.id_buku) }

    // Filter buku based on the peminjaman list
    val availableBooks = bukuList.filter { buku ->
        peminjamanList.any { peminjaman -> peminjaman.id_buku == buku.id_buku }
    }

    // State for DatePicker Dialog
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // DatePicker Dialog for Tanggal Dikembalikan
    val datePickerDikembalikan = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = "${dayOfMonth.toString().padStart(2, '0')}/${
                (month + 1).toString().padStart(2, '0')
            }/$year"
            onValueChange(insertUiEvent.copy(tanggal_dikembalikan = formattedDate))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Input ID Pengembalian
        OutlinedTextField(
            value = insertUiEvent.id_pengembalian,
            onValueChange = { onValueChange(insertUiEvent.copy(id_pengembalian = it)) },
            label = { Text("ID Pengembalian") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = insertUiEvent.id_pengembalian.isBlank()
        )

        // Dropdown for Buku
        ExposedDropdownMenuBox(
            expanded = bukuExpanded,
            onExpandedChange = { bukuExpanded = !bukuExpanded }
        ) {
            OutlinedTextField(
                value = selectedBuku,
                onValueChange = {},
                readOnly = true,
                label = { Text("ID Buku yang ingin dikembalikan") },
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

                            // Find the corresponding peminjaman and set the id_peminjaman
                            val peminjaman = peminjamanList.find { it.id_buku == buku.id_buku }
                            onValueChange(insertUiEvent.copy(
                                id_buku = buku.id_buku,
                                id_peminjaman = peminjaman?.id_peminjaman.orEmpty()
                            ))

                            bukuExpanded = false
                        }
                    )
                }
            }
        }

        // Input Tanggal Dikembalikan
        OutlinedTextField(
            value = insertUiEvent.tanggal_dikembalikan,
            onValueChange = {},
            label = { Text("Tanggal Dikembalikan") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled) { datePickerDikembalikan.show() },
            enabled = false, // Disable manual input
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { datePickerDikembalikan.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            isError = insertUiEvent.tanggal_dikembalikan.isBlank()
        )
    }
}

