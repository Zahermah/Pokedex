package com.example.pokedex.model.responses

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeResponse>,
    val stats: List<StatResponse>,
    val sprites: SpritesResponse
)