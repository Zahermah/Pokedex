package com.example.pokedex.view

import android.R.drawable
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.pokedex.R.string
import com.example.pokedex.model.PokemonDetail
import com.example.pokedex.model.PokemonStats
import com.example.pokedex.model.responses.TypeResponse
import com.example.pokedex.ui.theme.getPokemonTypeColor
import com.example.pokedex.ui.theme.getStatColor
import com.example.pokedex.viewmodel.PokemonDetailUiState
import com.example.pokedex.viewmodel.PokemonDetailViewModel

private const val TAG = "PokemonDetailList"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: PokemonDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(pokemonId) {
        Log.d("PokemonDetail", "Loading Pokemon details for ID: $pokemonId")
        viewModel.loadPokemonDetail(pokemonId)
    }

    PokemonDetailScaffold(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        uiState = uiState,
        onBackClick = onBackClick,
        onRetryClick = { viewModel.loadPokemonDetail(pokemonId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonDetailScaffold(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    uiState: PokemonDetailUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Scaffold(
        topBar = {
            PokemonDetailTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is PokemonDetailUiState.Loading -> {
                    Log.d(TAG, "Showing loading state")
                    LoadingScreen()
                }

                is PokemonDetailUiState.Success -> {
                    Log.d("PokemonDetail", "Loaded Pokemon: ${uiState.pokemon.name}")
                    PokemonDetailContent(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                        pokemon = uiState.pokemon
                    )
                }

                is PokemonDetailUiState.Error -> {
                    Log.e("PokemonDetail", "Error loading Pokemon: ${uiState.message}")
                    ErrorScreen(
                        message = uiState.message,
                        onRetryClick = onRetryClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokemonDetailTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(string.view_details)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonDetailContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemon: PokemonDetail
) {
    val scrollState = rememberSaveable(saver = ScrollState.Saver) {
        ScrollState(initial = 0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        PokemonHeader(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            pokemon = pokemon
        )
        PokemonInfo(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            pokemon = pokemon
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonHeader(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemon: PokemonDetail
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        GradientBackground()
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PokemonImage(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                imageUrl = pokemon.imageUrl,
                pokemonName = pokemon.name,
                pokemonId = pokemon.id
            )
        }
    }
}

@Composable
private fun GradientBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonImage(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    imageUrl: String,
    pokemonName: String,
    pokemonId: Int
) {
    with(sharedTransitionScope) {
        AsyncImage(
            model = imageUrl,
            contentDescription = pokemonName,
            modifier = Modifier
                .size(200.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "pokemon_image_$pokemonId"),
                    animatedVisibilityScope = animatedContentScope
                ),
            contentScale = ContentScale.Fit,
            error = painterResource(id = drawable.star_on)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonInfo(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemon: PokemonDetail
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PokemonBasicInfo(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            pokemon = pokemon
        )
        Spacer(modifier = Modifier.height(16.dp))

        PokemonTypeSection(types = pokemon.types)
        Spacer(modifier = Modifier.height(12.dp))

        if (pokemon.description.isNotEmpty()) {
            PokemonDescriptionSection(description = pokemon.description)
            Spacer(modifier = Modifier.height(12.dp))
        }

        PokemonCharacteristicsSection(
            height = pokemon.height,
            weight = pokemon.weight
        )
        Spacer(modifier = Modifier.height(24.dp))

        PokemonStatsSection(stats = pokemon.stats)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PokemonBasicInfo(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pokemon: PokemonDetail
) {
    with(sharedTransitionScope) {
        Text(
            text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .sharedElement(
                    state = rememberSharedContentState(key = "pokemon_name_${pokemon.id}"),
                    animatedVisibilityScope = animatedContentScope
                )
        )
    }

    Text(
        text = "#${pokemon.id.toString().padStart(3, '0')}",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PokemonTypeSection(types: List<TypeResponse>) {
    SectionTitle(title = stringResource(string.pokemon_type))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3
    ) {
        types.forEach { type ->
            PokemonTypeChip(type = type)
        }
    }
}

@Composable
private fun PokemonDescriptionSection(description: String) {
    SectionTitle(title = stringResource(string.pokemon_description))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun PokemonCharacteristicsSection(height: Int, weight: Int) {
    SectionTitle(title = stringResource(string.pokemon_physical_characteristics))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PhysicalCharacteristic(
                label = stringResource(string.pokemon_height),
                value = "${height / 10.0}m"
            )
            VerticalDivider()
            PhysicalCharacteristic(
                label = stringResource(string.pokemon_weight),
                value = "${weight / 10.0}kg"
            )
        }
    }
}

@Composable
private fun PokemonStatsSection(stats: List<PokemonStats>) {
    SectionTitle(title = stringResource(string.pokemon_base_stats))
    stats.forEach { stat ->
        StatBar(stat = stat)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun StatBar(stat: PokemonStats) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stat.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stat.value.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { stat.value / 255f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = getStatColor(stat.value),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun PhysicalCharacteristic(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun PokemonTypeChip(type: TypeResponse) {
    Surface(
        color = getPokemonTypeColor(type.type.name),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = type.type.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}