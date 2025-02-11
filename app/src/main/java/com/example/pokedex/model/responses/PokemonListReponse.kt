package com.example.pokedex.model.responses

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItemResponse>
)