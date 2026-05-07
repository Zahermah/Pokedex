package com.example.pokedex.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokedex.model.Pokemon
import com.example.pokedex.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PokemonPagingSourceTest {

    private val mockRepository: PokemonRepository = mockk()
    private lateinit var pagingSource: PokemonPagingSource

    @Before
    fun setup() {
        pagingSource = PokemonPagingSource(mockRepository)
    }

    @Test
    fun `load first page returns correct data and null prevKey`() = runTest {
        val pokemonList = listOf(Pokemon(1, "bulbasaur"), Pokemon(2, "ivysaur"))
        coEvery { mockRepository.getPokemonList(offset = 0, limit = 20) } returns pokemonList

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(pokemonList, page.data)
        assertNull(page.prevKey)
        assertEquals(1, page.nextKey)
    }

    @Test
    fun `load second page has correct prevKey and nextKey`() = runTest {
        val pokemonList = listOf(Pokemon(21, "spearow"))
        coEvery { mockRepository.getPokemonList(offset = 20, limit = 20) } returns pokemonList

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(key = 1, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(0, page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load empty result returns null nextKey`() = runTest {
        coEvery { mockRepository.getPokemonList(any(), any()) } returns emptyList()

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertNull(page.nextKey)
        assertTrue(page.data.isEmpty())
    }

    @Test
    fun `load returns Error when repository throws`() = runTest {
        coEvery { mockRepository.getPokemonList(any(), any()) } throws RuntimeException("Network error")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `getRefreshKey returns null when anchorPosition is null`() {
        val state = PagingState<Int, Pokemon>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        assertNull(pagingSource.getRefreshKey(state))
    }

    @Test
    fun `getRefreshKey returns null when no closest page found`() {
        val state = PagingState<Int, Pokemon>(
            pages = emptyList(),
            anchorPosition = 5,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        assertNull(pagingSource.getRefreshKey(state))
    }
}
