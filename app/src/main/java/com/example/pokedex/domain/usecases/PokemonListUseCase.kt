package com.example.pokedex.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokedex.data.paging.PokemonPagingSource
import com.example.pokedex.model.Pokemon
import com.example.pokedex.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<PagingData<Pokemon>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 40,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(repository) }
        ).flow
    }
}
