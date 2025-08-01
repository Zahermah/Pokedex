package com.example.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.domain.usecases.GetPokemonDetailUseCase
import com.example.pokedex.domain.util.ErrorHandler
import com.example.pokedex.model.PokemonDetail
import com.example.pokedex.network.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    private val errorHandler: ErrorHandler,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var currentPokemonId: Int? = null

    init {
        viewModelScope.launch {
            networkConnectivityObserver.observe().collect { connectionState ->
                if (connectionState is NetworkConnectivityObserver.ConnectionState.Available
                    && _uiState.value is PokemonDetailUiState.Error
                    && _uiState.value !is PokemonDetailUiState.Loading
                ) {
                    currentPokemonId?.let { loadPokemonDetail(it) }
                }
            }
        }
    }


    fun loadPokemonDetail(pokemonId: Int) {
        Log.d("PokemonDetail", "Loading Pokemon detail for ID: $pokemonId")

        if (currentPokemonId != pokemonId || _uiState.value is PokemonDetailUiState.Error) {
            Log.d("PokemonDetail", "New Pokemon ID detected or retrying")
            currentPokemonId = pokemonId
            viewModelScope.launch {
                _uiState.value = PokemonDetailUiState.Loading
                try {
                    val pokemon = getPokemonDetailUseCase(pokemonId)
                    Log.d("PokemonDetail", "Successfully retrieved Pokemon: ${pokemon.name}")
                    _uiState.value = PokemonDetailUiState.Success(pokemon)
                } catch (e: Exception) {
                    Log.e("PokemonDetail", "Error loading Pokemon detail: ${e.message}")
                    _uiState.value = PokemonDetailUiState.Error(errorHandler.handleError(e))
                }
            }
        }
    }
}


sealed class PokemonDetailUiState {
    object Loading : PokemonDetailUiState()
    data class Success(val pokemon: PokemonDetail) : PokemonDetailUiState()
    data class Error(val message: String) : PokemonDetailUiState()
}