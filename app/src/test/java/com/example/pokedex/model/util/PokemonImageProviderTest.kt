package com.example.pokedex.model.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonImageProviderTest {

    @Test
    fun `getSpriteUrl returns correct URL for bulbasaur`() {
        val url = PokemonImageProvider.getSpriteUrl(1)
        assertEquals(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
            url
        )
    }

    @Test
    fun `getSpriteUrl contains the pokemon id`() {
        val url = PokemonImageProvider.getSpriteUrl(25)
        assertTrue(url.contains("25"))
    }

    @Test
    fun `getSpriteUrl ends with png extension`() {
        val url = PokemonImageProvider.getSpriteUrl(150)
        assertTrue(url.endsWith(".png"))
    }

    @Test
    fun `getSpriteUrl differs between two pokemon`() {
        val url1 = PokemonImageProvider.getSpriteUrl(1)
        val url2 = PokemonImageProvider.getSpriteUrl(2)
        assertTrue(url1 != url2)
    }

    @Test
    fun `getShinyUrl returns correct URL for charizard`() {
        val url = PokemonImageProvider.getShinyUrl(6)
        assertEquals(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/6.png",
            url
        )
    }

    @Test
    fun `getShinyUrl contains shiny path segment`() {
        val url = PokemonImageProvider.getShinyUrl(25)
        assertTrue(url.contains("shiny"))
    }

    @Test
    fun `getShinyUrl and getSpriteUrl differ for same pokemon`() {
        val shiny = PokemonImageProvider.getShinyUrl(1)
        val normal = PokemonImageProvider.getSpriteUrl(1)
        assertTrue(shiny != normal)
    }
}
