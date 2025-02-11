package com.example.pokedex.model

import com.example.pokedex.model.responses.TypeResponse
import com.example.pokedex.model.util.PokemonImageProvider

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeResponse>,
    val stats: List<PokemonStats>,
    val description: String
){
    val imageUrl : String get() = PokemonImageProvider.getSpriteUrl(id)
}