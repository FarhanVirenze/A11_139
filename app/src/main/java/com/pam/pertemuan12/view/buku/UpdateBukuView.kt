package com.pam.pertemuan12.view.buku

import com.pam.pertemuan12.viewmodel.buku.UpdateUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.pam.pertemuan12.customwidget.TopAppBar
import com.pam.pertemuan12.navigation.DestinasiNavigasi
import com.pam.pertemuan12.viewmodel.buku.PenyediaBukuViewModel
import com.pam.pertemuan12.viewmodel.buku.UpdateBukuViewModel
import com.pam.pertemuan12.viewmodel.buku.UpdateUiEvent
import kotlinx.coroutines.launch

object DestinasiBukuUpdate : DestinasiNavigasi {
    override val route = "buku_update"
    override val titleRes = "Update Buku"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateView(
    id_buku: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateBukuViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = PenyediaBukuViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(id_buku) {
        viewModel.loadBuku(id_buku)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiBukuUpdate.titleRes,
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        UpdateBody(
            updateUiState = viewModel.uiState,
            onSiswaValueChange = viewModel::updateMahasiswaState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateBuku(id_buku)
                    navigateBack()  // Navigate back after saving
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun UpdateBody(
    updateUiState: UpdateUiState,
    onSiswaValueChange: (UpdateUiEvent) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier.padding(12.dp)
    ) {
        FormInput(
            updateUiEvent = updateUiState.updateUiEvent,
            onValueChange = onSiswaValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Update")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInput(
    updateUiEvent: UpdateUiEvent,
    modifier: Modifier = Modifier,
    onValueChange: (UpdateUiEvent) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = updateUiEvent.judul,
            onValueChange = { onValueChange(updateUiEvent.copy(judul = it)) },
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = updateUiEvent.id_buku,
            onValueChange = { onValueChange(updateUiEvent.copy(id_buku = it)) },
            label = { Text("ID_BUKU") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = updateUiEvent.penulis,
            onValueChange = { onValueChange(updateUiEvent.copy(penulis = it)) },
            label = { Text("Penulis") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = updateUiEvent.kategori,
            onValueChange = { onValueChange(updateUiEvent.copy(kategori = it)) },
            label = { Text("Kategori") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        // Radio button for "Status"
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text("Status", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RadioButton(
                    selected = updateUiEvent.status == "Tersedia",
                    onClick = { onValueChange(updateUiEvent.copy(status = "Tersedia")) }
                )
                Text("Tersedia", modifier = Modifier.align(Alignment.CenterVertically))

                RadioButton(
                    selected = updateUiEvent.status == "Tidak Tersedia",
                    onClick = { onValueChange(updateUiEvent.copy(status = "Tidak Tersedia")) }
                )
                Text("Tidak Tersedia", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
        if (enabled) {
            Text(
                text = "Isi Semua Data",
                modifier = Modifier.padding(12.dp)
            )
        }
        Divider(
            thickness = 8.dp,
            modifier = Modifier.padding(12.dp)
        )
    }
}
