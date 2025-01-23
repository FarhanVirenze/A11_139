package com.pam.pertemuan12.view.Pengembalian

import com.pam.pertemuan12.viewmodel.Pengembalian.UpdateUiState
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
import androidx.compose.runtime.LaunchedEffect
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
import com.pam.pertemuan12.model.Peminjaman
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.Pengembalian.PenyediaPengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.UpdatePengembalianViewModel
import com.pam.pertemuan12.viewmodel.Pengembalian.UpdateUiEvent
import com.pam.pertemuan12.viewmodel.peminjaman.HomePeminjamanUiState
import com.pam.pertemuan12.viewmodel.peminjaman.HomePeminjamanViewModel
import com.pam.pertemuan12.viewmodel.peminjaman.PenyediaPeminjamanViewModel
import kotlinx.coroutines.launch

object DestinasiPengembalianUpdate : DestinasiNavigasi {
    override val route = "pengembalian_update"
    override val titleRes = "Update Pengembalian"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePengembalianView(
    id_pengembalian: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdatePengembalianViewModel = viewModel(factory = PenyediaPengembalianViewModel.Factory),
    viewModelPjm: HomePeminjamanViewModel = viewModel(factory = PenyediaPeminjamanViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val peminjamanUiState = viewModelPjm.pjmUiState

    LaunchedEffect(id_pengembalian) {
        viewModel.loadPengembalian(id_pengembalian)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiPengembalianUpdate.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        when {
            peminjamanUiState is HomePeminjamanUiState.Loading -> {
                Text(
                    text = "Memuat data...",
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }

            peminjamanUiState is HomePeminjamanUiState.Error -> {
                Text(
                    text = "Gagal memuat data.",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                )
            }

            peminjamanUiState is HomePeminjamanUiState.Success -> {
                UpdateBody(
                    updateUiState = viewModel.uiState,
                    peminjamanList = peminjamanUiState.peminjaman,
                    onPeminjamanValueChange = viewModel::updatePengembalianState,
                    onSaveClick = {
                        coroutineScope.launch {
                            viewModel.updatePengembalian(id_pengembalian)
                            navigateBack()  // Navigate back after saving
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
    peminjamanList: List<Peminjaman>,
    onPeminjamanValueChange: (UpdateUiEvent) -> Unit,
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
            Text(text = "Update")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    updateUiEvent: UpdateUiEvent,
    peminjamanList: List<Peminjaman>,
    modifier: Modifier = Modifier,
    onValueChange: (UpdateUiEvent) -> Unit = {},
    enabled: Boolean = true
) {
    var peminjamanExpanded by remember { mutableStateOf(false) }
    var selectedPeminjaman by remember { mutableStateOf(updateUiEvent.id_peminjaman) }

    // Update selectedBuku dan selectedAnggota jika updateUiEvent berubah
    LaunchedEffect(updateUiEvent) {
        selectedPeminjaman = updateUiEvent.id_peminjaman
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Input ID Pengembalian
        OutlinedTextField(
            value = updateUiEvent.id_pengembalian,
            onValueChange = { onValueChange(updateUiEvent.copy(id_pengembalian = it)) },
            label = { Text("ID Pengembalian") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = updateUiEvent.id_pengembalian.isBlank()
        )

        // Dropdown untuk Peminjaman
        ExposedDropdownMenuBox(
            expanded = peminjamanExpanded,
            onExpandedChange = { peminjamanExpanded = !peminjamanExpanded }
        ) {
            OutlinedTextField(
                value = selectedPeminjaman,
                onValueChange = {},
                readOnly = true,
                label = { Text("ID Buku yang ingin dikembalikan") },
                trailingIcon = {
                    androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = peminjamanExpanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = peminjamanExpanded,
                onDismissRequest = { peminjamanExpanded = false }
            ) {
                peminjamanList.forEach { peminjaman ->
                    DropdownMenuItem(
                        text = { Text(peminjaman.id_buku) },
                        onClick = {
                            selectedPeminjaman = peminjaman.id_peminjaman
                            onValueChange(updateUiEvent.copy(id_peminjaman = peminjaman.id_buku))
                            peminjamanExpanded = false
                        }
                    )
                }
            }
        }

        // Input Tanggal Dikembalikan
        OutlinedTextField(
            value = updateUiEvent.tanggal_dikembalikan,
            onValueChange = { onValueChange(updateUiEvent.copy(tanggal_dikembalikan = it)) },
            label = { Text("Tanggal Dikembalikan") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = updateUiEvent.tanggal_dikembalikan.isBlank()
        )
    }
}
