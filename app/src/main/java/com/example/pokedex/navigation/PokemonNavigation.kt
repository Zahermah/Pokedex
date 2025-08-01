package com.example.pokedex.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedex.view.PokemonDetailScreen
import com.example.pokedex.view.PokemonListScreen


sealed class Screen(val route: String) {
    object PokemonList : Screen("pokemon/list")

    object PokemonDetail : Screen("pokemon/detail/{pokemonId}") {
        const val POKEMON_ID = "pokemonId"
        fun createRoute(id: Int) = "pokemon/detail/$id"
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonNavigation(
    navController: NavHostController = rememberNavController()
) {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Screen.PokemonList.route) {
            composable(Screen.PokemonList.route) {
                PokemonListScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                    onPokemonClick = { id ->
                        navController.navigate(Screen.PokemonDetail.createRoute(id))
                    },
                )
            }

            composable(
                route = Screen.PokemonDetail.route,
                arguments = listOf(
                    navArgument(Screen.PokemonDetail.POKEMON_ID) {
                        type = NavType.IntType
                    }
                )
            ) { entry ->
                val id = entry.arguments?.getInt(Screen.PokemonDetail.POKEMON_ID)
                    ?: return@composable

                PokemonDetailScreen(
                    pokemonId = id,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}