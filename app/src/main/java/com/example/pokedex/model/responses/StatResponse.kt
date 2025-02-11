package com.example.pokedex.model.responses

data class StatResponse(
    val base_stat: Int,
    val effort: Int,
    val stat: StatInfo
)