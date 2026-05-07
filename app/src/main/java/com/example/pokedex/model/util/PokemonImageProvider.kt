package com.example.pokedex.model.util

object PokemonImageProvider {
    private const val BASE_SPRITE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon"

    fun getSpriteUrl(id: Int): String = "$BASE_SPRITE_URL/$id.png"

    fun getShinyUrl(id: Int): String = "$BASE_SPRITE_URL/shiny/$id.png"
}