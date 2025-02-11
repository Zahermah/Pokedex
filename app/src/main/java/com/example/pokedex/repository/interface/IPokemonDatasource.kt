package com.example.pokedex.repository.`interface`

import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonDetail

interface IPokemonDatasource {
    suspend fun getPokemonList(offset: Int, limit: Int): List<Pokemon>
    suspend fun getPokemonDetail(id: Int): PokemonDetail
}