package com.example.pokedex.domain.usecases

import androidx.paging.PagingData
import com.example.pokedex.repository.PokemonRepository
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class GetPokemonListUseCaseTest {

    private val repository: PokemonRepository = mockk()
    private lateinit var useCase: GetPokemonListUseCase

    @Before
    fun setup() {
        useCase = GetPokemonListUseCase(repository)
    }

    @Test
    fun `invoke returns a non-null flow`() {
        val result = useCase()
        assertNotNull(result)
    }

    @Test
    fun `invoke emits PagingData`() = runTest {
        val result: PagingData<*> = useCase().first()
        assertNotNull(result)
    }

    @Test
    fun `invoke called twice returns two independent flows`() {
        val flow1 = useCase()
        val flow2 = useCase()
        assertNotNull(flow1)
        assertNotNull(flow2)
        assert(flow1 !== flow2)
    }
}