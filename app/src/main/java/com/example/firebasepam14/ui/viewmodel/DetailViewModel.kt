package com.example.firebasepam14.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasepam14.model.Mahasiswa
import com.example.firebasepam14.repository.MahasiswaRepository
import com.example.firebasepam14.ui.navigate.DestinasiDetail
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch



sealed class DetailUiState {
    data class Success(val mahasiswa: Mahasiswa) : DetailUiState()
    data class Error (val message : Throwable) :DetailUiState()
    object Loading : DetailUiState()
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val mhs: MahasiswaRepository
) : ViewModel() {

    var mahasiswaDetailState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    private val _nim: String = checkNotNull(savedStateHandle[DestinasiDetail.NIM])

    init {
        getMahasiswabyNim()
    }

    fun getMahasiswabyNim() {
        viewModelScope.launch {
            mhs.getMahasiswaByNim(_nim)
                .onStart {
                    mahasiswaDetailState = DetailUiState.Loading
                }
                .catch {
                    mahasiswaDetailState = DetailUiState.Error(it)
                }
                .collect{
                    mahasiswa ->
                    mahasiswaDetailState = DetailUiState.Success(mahasiswa)
                }
        }
    }
}