package com.example.pokedex.domain.usecases

import com.example.pokedex.model.PokemonDetail
import com.example.pokedex.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(id: Int): PokemonDetail {
        return repository.getPokemonDetail(id)
    }
}