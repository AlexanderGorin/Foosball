package com.alexandergorin.domain

data class Ranking(
    val personName: String,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val id: Int = 0
)