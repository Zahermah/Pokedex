package com.example.pokedex.viewmodel

import com.example.pokedex.domain.usecases.GetPokemonDetailUseCase
import com.example.pokedex.domain.util.ErrorHandler
import com.example.pokedex.model.PokemonDetail
import com.example.pokedex.model.PokemonStats
import com.example.pokedex.model.responses.TypeInfo
import com.example.pokedex.model.responses.TypeResponse
import com.example.pokedex.network.NetworkConnectivityObserver
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase = mockk()
    private val errorHandler: ErrorHandler = mockk()
    private val networkObserver: NetworkConnectivityObserver = mockk()
    private val connectionFlow = MutableSharedFlow<NetworkConnectivityObserver.ConnectionState>(
        extraBufferCapacity = 64
    )

    private lateinit var viewModel: PokemonDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { networkObserver.observe() } returns connectionFlow
        viewModel = PokemonDetailViewModel(getPokemonDetailUseCase, errorHandler, networkObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        assertTrue(viewModel.uiState.value is PokemonDetailUiState.Loading)
    }

    @Test
    fun `loadPokemonDetail emits Success when use case succeeds`() = runTest(testDispatcher) {
        val pokemon = fakePokemonDetail(id = 1)
        coEvery { getPokemonDetailUseCase(1) } returns pokemon

        viewModel.loadPokemonDetail(1)

        val state = viewModel.uiState.value
        assertTrue(state is PokemonDetailUiState.Success)
        assertEquals(pokemon, (state as PokemonDetailUiState.Success).pokemon)
    }

    @Test
    fun `loadPokemonDetail emits Error when use case throws`() = runTest(testDispatcher) {
        val exception = RuntimeException("Network failure")
        coEvery { getPokemonDetailUseCase(1) } throws exception
        every { errorHandler.handleError(exception) } returns "An unexpected error occurred. Please try again."

        viewModel.loadPokemonDetail(1)

        val state = viewModel.uiState.value
        assertTrue(state is PokemonDetailUiState.Error)
        assertEquals(
            "An unexpected error occurred. Please try again.",
            (state as PokemonDetailUiState.Error).message
        )
    }

    @Test
    fun `loadPokemonDetail does not reload same pokemon when already in Success state`() = runTest(testDispatcher) {
        val pokemon = fakePokemonDetail(id = 1)
        coEvery { getPokemonDetailUseCase(1) } returns pokemon

        viewModel.loadPokemonDetail(1)
        assertTrue(viewModel.uiState.value is PokemonDetailUiState.Success)

        // Second call with the same ID should be ignored
        coEvery { getPokemonDetailUseCase(1) } returns fakePokemonDetail(id = 1, name = "changed")
        viewModel.loadPokemonDetail(1)

        assertEquals("bulbasaur", (viewModel.uiState.value as PokemonDetailUiState.Success).pokemon.name)
    }

    @Test
    fun `loadPokemonDetail loads new pokemon when id changes`() = runTest(testDispatcher) {
        coEvery { getPokemonDetailUseCase(1) } returns fakePokemonDetail(id = 1, name = "bulbasaur")
        coEvery { getPokemonDetailUseCase(2) } returns fakePokemonDetail(id = 2, name = "ivysaur")

        viewModel.loadPokemonDetail(1)
        viewModel.loadPokemonDetail(2)

        val state = viewModel.uiState.value as PokemonDetailUiState.Success
        assertEquals("ivysaur", state.pokemon.name)
    }

    @Test
    fun `reconnection triggers reload when state is Error`() = runTest(testDispatcher) {
        val exception = RuntimeException("Network failure")
        every { errorHandler.handleError(exception) } returns "error"
        coEvery { getPokemonDetailUseCase(1) } throws exception
        viewModel.loadPokemonDetail(1)
        assertTrue(viewModel.uiState.value is PokemonDetailUiState.Error)

        val pokemon = fakePokemonDetail(id = 1)
        coEvery { getPokemonDetailUseCase(1) } returns pokemon
        connectionFlow.emit(NetworkConnectivityObserver.ConnectionState.Available)

        assertTrue(viewModel.uiState.value is PokemonDetailUiState.Success)
    }

    @Test
    fun `reconnection does not reload when state is Success`() = runTest(testDispatcher) {
        val pokemon = fakePokemonDetail(id = 1)
        coEvery { getPokemonDetailUseCase(1) } returns pokemon
        viewModel.loadPokemonDetail(1)
        assertTrue(viewModel.uiState.value is PokemonDetailUiState.Success)

        coEvery { getPokemonDetailUseCase(1) } returns fakePokemonDetail(id = 1, name = "changed")
        connectionFlow.emit(NetworkConnectivityObserver.ConnectionState.Available)

        // State should remain Success with the original pokemon
        assertEquals("bulbasaur", (viewModel.uiState.value as PokemonDetailUiState.Success).pokemon.name)
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
