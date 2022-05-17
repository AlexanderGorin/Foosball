package com.alexandergorin.foosball.ui.rankings

sealed class RankingsViewState {
    object Loading : RankingsViewState()
    data class Loaded(val rankings: List<RankingState>) : RankingsViewState()
    object Error : RankingsViewState()
}

data class RankingState(
    val id: String,
    val personName: String,
    val gamesPlayed: String,
    val gamesWon: String
)