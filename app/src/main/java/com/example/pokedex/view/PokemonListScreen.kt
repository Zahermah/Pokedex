package com.example.pokedex.view

import android.R.drawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.pokedex.R.string.Pokemon
import com.example.pokedex.R.string.view_details
import com.example.pokedex.model.Pokemon
import com.example.pokedex.viewmodel.PokemonListViewModel

private const val TAG = "PokemonDetailList"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonListScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: PokemonListViewModel = hiltViewModel(),
    onPokemonClick: (Int) -> Unit,
) {
    val pokemonPagingItems = viewModel.pokemonList.collectAsLazyPagingItems()
    Log.d(TAG, "Initializing PokemonListScreen")

    PokemonListScaffold(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        pokemonPagingItems = pokemonPagingItems,
        onPokemonClick = onPokemonClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonListScaffold(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemonPagingItems: LazyPagingItems<Pokemon>,
    onPokemonClick: (Int) -> Unit,
) {
    Scaffold(
        topBar = { PokemonListTopBar() }
    ) { paddingValues ->
        PokemonList(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            pokemonPagingItems = pokemonPagingItems,
            contentPadding = paddingValues,
            onPokemonClick = onPokemonClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokemonListTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(Pokemon),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonList(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemonPagingItems: LazyPagingItems<Pokemon>,
    contentPadding: PaddingValues,
    onPokemonClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = pokemonPagingItems.itemCount,
            key = { index ->
                val pokemon = pokemonPagingItems[index]
                pokemon?.id ?: index
            }
        ) { index ->
            val pokemon = pokemonPagingItems[index]
            pokemon?.let {
                PokemonListItem(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    pokemon = it,
                    onClick = {
                        Log.d(TAG, "Selected Pokemon: id=${it.id}, name=${it.name}")
                        onPokemonClick(it.id)
                    }
                )

            }
        }

        pokemonPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    Log.d(TAG, "Starting initial load")
                    item { LoadingScreen() }
                }

                loadState.append is LoadState.Loading -> {
                    Log.d(TAG, "Loading more Pokemon")
                    item { LoadingScreen() }
                }

                loadState.refresh is LoadState.Error -> {
                    item {
                        ErrorItem(
                            error = loadState.refresh as LoadState.Error,
                            onRetry = {
                                Log.d(TAG, "Retrying initial load")
                                retry()
                            }
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    Log.e(TAG, "Failed to load more Pokemon")
                    item {
                        ErrorItem(
                            error = loadState.append as LoadState.Error,
                            onRetry = {
                                Log.d(TAG, "Retrying pagination")
                                retry()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonListItem(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemon: Pokemon,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PokemonImage(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                pokemon = pokemon
            )
            PokemonInfo(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                modifier = Modifier.weight(1f), 
                pokemon = pokemon
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonImage(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemon: Pokemon
) {
    with(sharedTransitionScope) {
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = pokemon.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .sharedElement(
                    state = rememberSharedContentState(key = "pokemon_image_${pokemon.id}"),
                    animatedVisibilityScope = animatedContentScope
                ),
            contentScale = ContentScale.Crop,
            error = painterResource(id = drawable.stat_notify_error)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonInfo(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier, 
    pokemon: Pokemon
) {
    Column(modifier = modifier) {
        with(sharedTransitionScope) {
            Text(
                text = pokemon.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "pokemon_name_${pokemon.id}"),
                    animatedVisibilityScope = animatedContentScope
                )
            )
        }
        Text(
            text = "#${pokemon.id.toString().padStart(3, '0')}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(view_details),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorItem(
    error: LoadState.Error,
    onRetry: () -> Unit
) {
    Log.e(TAG, "Displaying error: ${error.error.message}")
    ErrorScreen(
        message = error.error.message ?: "Unknown error occurred",
        onRetryClick = onRetry
    )
}