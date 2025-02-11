package com.example.pokedex.datasource

import android.util.Log
import com.example.pokedex.api.IPokemonApi
import com.example.pokedex.repository.`interface`.IPokemonDatasource
import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonDetail
import com.example.pokedex.model.PokemonStats
import com.example.pokedex.model.responses.PokemonDetailResponse
import com.example.pokedex.model.responses.PokemonListItemResponse
import com.example.pokedex.model.responses.PokemonSpeciesResponse
import com.example.pokedex.model.responses.StatResponse
import javax.inject.Inject


class PokemonDatasource @Inject constructor(
    private val pokemonApi: IPokemonApi
) : IPokemonDatasource {

    override suspend fun getPokemonList(offset: Int, limit: Int): List<Pokemon> {
        Log.d(TAG, "Fetching Pokemon list: offset=$offset, limit=$limit")
        return pokemonApi.getPokemonList(offset, limit)
            .results
            .map { pokemonResult -> pokemonResult.toPokemon() }
    }

    override suspend fun getPokemonDetail(id: Int): PokemonDetail {
        Log.d(TAG, "Fetching Pokemon detail: id=$id")
        return try {
            val pokemonResponse = pokemonApi.getPokemonDetail(id)
            val speciesResponse = pokemonApi.getPokemonSpecies(id)

            createPokemonDetail(pokemonResponse, speciesResponse)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to fetch Pokemon details for id: $id", e)
        }
    }

    private fun PokemonListItemResponse.toPokemon(): Pokemon {
        val id = extractPokemonId(url)
        return Pokemon(
            id = id,
            name = name
        )
    }

    private fun extractPokemonId(url: String): Int {
        return url.split("/")
            .dropLast(1)
            .last()
            .toInt()
    }


    private fun createPokemonDetail(
        pokemonResponse: PokemonDetailResponse,
        speciesResponse: PokemonSpeciesResponse
    ): PokemonDetail {
        return PokemonDetail(
            id = pokemonResponse.id,
            name = pokemonResponse.name,
            height = pokemonResponse.height,
            weight = pokemonResponse.weight,
            types = pokemonResponse.types,  // Direct mapping as types are the same structure
            stats = mapStats(pokemonResponse.stats),
            description = extractEnglishDescription(speciesResponse)
        )
    }


    private fun mapStats(stats: List<StatResponse>): List<PokemonStats> {
        return stats.map { statResponse ->
            PokemonStats(
                name = statResponse.stat.name,
                value = statResponse.base_stat
            )
        }
    }


    private fun extractEnglishDescription(speciesResponse: PokemonSpeciesResponse): String {
        return speciesResponse.flavor_text_entries
            .firstOrNull { it.language.name == ENGLISH_LANGUAGE }
            ?.flavor_text
            ?.replace(NEWLINE_CHAR, SPACE_CHAR)
            ?: DEFAULT_DESCRIPTION
    }

    private companion object {
        const val TAG = "PokemonData"
        const val ENGLISH_LANGUAGE = "en"
        const val NEWLINE_CHAR = "\n"
        const val SPACE_CHAR = " "
        const val DEFAULT_DESCRIPTION = "No description available."
    }
}
