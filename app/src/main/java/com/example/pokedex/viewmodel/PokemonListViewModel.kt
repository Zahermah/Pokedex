package com.example.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pokedex.domain.usecases.GetPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {
    val pokemonList = getPokemonListUseCase().cachedIn(viewModelScope)
}