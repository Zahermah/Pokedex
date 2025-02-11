package com.example.pokedex.domain.usecases

import com.example.pokedex.model.Pokemon
import com.example.pokedex.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(offset: Int, limit: Int): List<Pokemon> {
        return repository.getPokemonList(offset, limit)
    }
}