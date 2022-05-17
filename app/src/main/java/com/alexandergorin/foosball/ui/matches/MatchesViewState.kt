package com.alexandergorin.foosball.ui.matches

sealed class MatchesViewState {
    object Loading : MatchesViewState()
    data class Loaded(val matches: List<MatchState>) : MatchesViewState()
    object Error : MatchesViewState()
}

data class MatchState(
    val id: Int,
    val firstPersonName: String,
    val firstPersonScore: String,
    val secondPersonName: String,
    val secondPersonScore: String
)