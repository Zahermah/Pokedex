package com.example.pokedex.repository

import com.example.pokedex.datasource.PokemonDatasource
import com.example.pokedex.repository.`interface`.IPokemonDatasource
import javax.inject.Inject

class PokemonRepository @Inject constructor(pokemonDatasource: PokemonDatasource) :
    IPokemonDatasource by pokemonDatasource