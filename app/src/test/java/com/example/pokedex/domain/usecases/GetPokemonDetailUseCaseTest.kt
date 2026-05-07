package com.example.pokedex.domain.usecases

import com.example.pokedex.model.PokemonDetail
import com.example.pokedex.model.PokemonStats
import com.example.pokedex.model.responses.TypeInfo
import com.example.pokedex.model.responses.TypeResponse
import com.example.pokedex.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class GetPokemonDetailUseCaseTest {

    private val repository: PokemonRepository = mockk()
    private lateinit var useCase: GetPokemonDetailUseCase

    @Before
    fun setup() {
        useCase = GetPokemonDetailUseCase(repository)
    }

    @Test
    fun `invoke returns pokemon detail from repository`() = runTest {
        val expected = fakePokemonDetail(id = 1)
        coEvery { repository.getPokemonDetail(1) } returns expected

        val result = useCase(1)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke with different id returns correct pokemon`() = runTest {
        val bulbasaur = fakePokemonDetail(id = 1, name = "bulbasaur")
        val pikachu = fakePokemonDetail(id = 25, name = "pikachu")
        coEvery { repository.getPokemonDetail(1) } returns bulbasaur
        coEvery { repository.getPokemonDetail(25) } returns pikachu

        assertEquals(bulbasaur, useCase(1))
        assertEquals(pikachu, useCase(25))
    }

    @Test
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.getPokemonDetail(99) } throws RuntimeException("Not found")

        var thrown: RuntimeException? = null
        try {
            useCase(99)
        } catch (e: RuntimeException) {
            thrown = e
        }

        assertNotNull(thrown)
    }

    private fun fakePokemonDetail(id: Int = 1, name: String = "bulbasaur") = PokemonDetail(
        id = id,
        name = name,
        height = 7,
        weight = 69,
        types = listOf(TypeResponse(1, TypeInfo("grass", "url"))),
        stats = listOf(PokemonStats("hp", 45)),
        description = "A strange seed."
    )
}
