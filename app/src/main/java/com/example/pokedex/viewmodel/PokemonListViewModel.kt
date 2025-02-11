package com.example.pokedex.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.pokedex.data.paging.PokemonPagingSource
import com.example.pokedex.domain.usecases.GetPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 40
    }

    val pokemonList = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = false
        ).also {
            Log.d(
                "PokemonPaging",
                "Pager configured: pageSize=$PAGE_SIZE, prefetch=$PREFETCH_DISTANCE"
            )
        },
        pagingSourceFactory = {
            Log.d("PokemonPaging", "Creating new PokemonPagingSource instance")
            PokemonPagingSource(getPokemonListUseCase)
        }
    ).flow.cachedIn(viewModelScope)
}