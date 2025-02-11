package com.example.pokedex.model

import com.example.pokedex.model.util.PokemonImageProvider

data class Pokemon(val id: Int, val name: String) {
    val imageUrl: String get() = PokemonImageProvider.getSpriteUrl(id)
}
