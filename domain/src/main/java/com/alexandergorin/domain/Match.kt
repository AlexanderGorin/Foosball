package com.alexandergorin.domain

data class Match(
    val firstPersonName: String,
    val firstScore: Int,
    val secondPersonName: String,
    val secondScore: Int,
    val id: Int = 0,
)