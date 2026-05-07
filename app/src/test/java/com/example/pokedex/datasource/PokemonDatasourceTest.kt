package com.example.pokedex.datasource

import com.example.pokedex.api.IPokemonApi
import com.example.pokedex.model.responses.FlavorTextEntry
import com.example.pokedex.model.responses.Language
import com.example.pokedex.model.responses.PokemonDetailResponse
import com.example.pokedex.model.responses.PokemonListItemResponse
import com.example.pokedex.model.responses.PokemonListResponse
import com.example.pokedex.model.responses.PokemonSpeciesResponse
import com.example.pokedex.model.responses.SpritesResponse
import com.example.pokedex.model.responses.StatInfo
import com.example.pokedex.model.responses.StatResponse
import com.example.pokedex.model.responses.TypeInfo
import com.example.pokedex.model.responses.TypeResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class PokemonDatasourceTest {

    private val mockApi: IPokemonApi = mockk()
    private lateinit var datasource: PokemonDatasource

    @Before
    fun setup() {
        datasource = PokemonDatasource(mockApi)
    }

    @Test
    fun `getPokemonList returns correctly mapped pokemon`() = runTest {
        coEvery { mockApi.getPokemonList(0, 20) } returns PokemonListResponse(
            count = 2,
            next = null,
            previous = null,
            results = listOf(
                PokemonListItemResponse("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                PokemonListItemResponse("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
            )
        )

        val result = datasource.getPokemonList(0, 20)

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("bulbasaur", result[0].name)
        assertEquals(2, result[1].id)
        assertEquals("ivysaur", result[1].name)
    }

    @Test
    fun `getPokemonList extracts correct pokemon id from URL`() = runTest {
        coEvery { mockApi.getPokemonList(any(), any()) } returns PokemonListResponse(
            count = 1,
            next = null,
            previous = null,
            results = listOf(
                PokemonListItemResponse("pikachu", "https://pokeapi.co/api/v2/pokemon/25/")
            )
        )

        val result = datasource.getPokemonList(0, 1)

        assertEquals(25, result[0].id)
    }

    @Test
    fun `getPokemonDetail returns correctly mapped detail`() = runTest {
        coEvery { mockApi.getPokemonDetail(1) } returns buildDetailResponse(id = 1, name = "bulbasaur")
        coEvery { mockApi.getPokemonSpecies(1) } returns buildSpeciesResponse("A strange seed was planted on its back.")

        val result = datasource.getPokemonDetail(1)

        assertEquals(1, result.id)
        assertEquals("bulbasaur", result.name)
        assertEquals(7, result.height)
        assertEquals(69, result.weight)
        assertEquals("A strange seed was planted on its back.", result.description)
    }

    @Test
    fun `getPokemonDetail maps stats correctly`() = runTest {
        val detailResponse = PokemonDetailResponse(
            id = 25,
            name = "pikachu",
            height = 4,
            weight = 60,
            types = listOf(TypeResponse(1, TypeInfo("electric", "url"))),
            stats = listOf(
                StatResponse(base_stat = 35, effort = 0, stat = StatInfo("hp", "url")),
                StatResponse(base_stat = 55, effort = 0, stat = StatInfo("attack", "url"))
            ),
            sprites = SpritesResponse(front_default = null)
        )
        coEvery { mockApi.getPokemonDetail(25) } returns detailResponse
        coEvery { mockApi.getPokemonSpecies(25) } returns buildSpeciesResponse("Pikachu!")

        val result = datasource.getPokemonDetail(25)

        assertEquals(2, result.stats.size)
        assertEquals("hp", result.stats[0].name)
        assertEquals(35, result.stats[0].value)
        assertEquals("attack", result.stats[1].name)
        assertEquals(55, result.stats[1].value)
    }

    @Test
    fun `getPokemonDetail uses default description when no English entry`() = runTest {
        coEvery { mockApi.getPokemonDetail(1) } returns buildDetailResponse(id = 1, name = "bulbasaur")
        coEvery { mockApi.getPokemonSpecies(1) } returns PokemonSpeciesResponse(
            flavor_text_entries = listOf(
                FlavorTextEntry("Un extraño...", Language("es", "url"))
            )
        )

        val result = datasource.getPokemonDetail(1)

        assertEquals("No description available.", result.description)
    }

    @Test
    fun `getPokemonDetail replaces newlines in description`() = runTest {
        coEvery { mockApi.getPokemonDetail(1) } returns buildDetailResponse(id = 1, name = "bulbasaur")
        coEvery { mockApi.getPokemonSpecies(1) } returns buildSpeciesResponse("A strange\nseed\nwas planted.")

        val result = datasource.getPokemonDetail(1)

        assertEquals("A strange seed was planted.", result.description)
    }

    @Test
    fun `getPokemonDetail throws IllegalStateException on API failure`() = runTest {
        coEvery { mockApi.getPokemonDetail(1) } throws RuntimeException("API error")

        var caught: IllegalStateException? = null
        try {
            datasource.getPokemonDetail(1)
        } catch (e: IllegalStateException) {
            caught = e
        }

        assertNotNull(caught)
    }

    private fun buildDetailResponse(id: Int, name: String) = PokemonDetailResponse(
        id = id,
        name = name,
        height = 7,
        weight = 69,
        types = listOf(TypeResponse(1, TypeInfo("grass", "url"))),
        stats = listOf(StatResponse(base_stat = 45, effort = 0, stat = StatInfo("hp", "url"))),
        sprites = SpritesResponse(front_default = null)
    )

    private fun buildSpeciesResponse(description: String) = PokemonSpeciesResponse(
        flavor_text_entries = listOf(FlavorTextEntry(description, Language("en", "url")))
    )
}
