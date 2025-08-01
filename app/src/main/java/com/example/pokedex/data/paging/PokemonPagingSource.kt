package com.example.pokedex.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokedex.model.Pokemon
import com.example.pokedex.repository.PokemonRepository
import javax.inject.Inject


class PokemonPagingSource @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : PagingSource<Int, Pokemon>() {

    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        Log.d(TAG, "Getting refresh key, anchorPosition=${state.anchorPosition}")
        val mostRecentlyAccessedPosition = state.anchorPosition ?: return null
        val currentPage = state.closestPageToPosition(mostRecentlyAccessedPosition)
            ?: return null


        return currentPage.prevKey?.plus(1)
            ?: currentPage.nextKey?.minus(1)
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        Log.d(TAG, "Loading page: key=${params.key}, loadSize=${params.loadSize}")
        return try {

            val currentPage = params.key ?: INITIAL_PAGE
            Log.d(TAG, "Loading page: $currentPage")
            val itemsPerPage = params.loadSize
            val startPosition = currentPage * itemsPerPage


            val pokemonList = pokemonRepository.getPokemonList(
                offset = startPosition,
                limit = itemsPerPage
            )
            Log.d(TAG, "Successfully loaded ${pokemonList.size} Pokemon for page $currentPage")

            LoadResult.Page(
                data = pokemonList,
                prevKey = when (currentPage) {
                    INITIAL_PAGE -> null
                    else -> currentPage - 1
                },
                nextKey = when {
                    pokemonList.isEmpty() -> null
                    else -> currentPage + 1
                }
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Error loading Pokemon list for page ${params.key}", exception)
            LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE = 0
        private const val TAG = "PokemonPagingSource"
    }
}