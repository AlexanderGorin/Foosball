package com.alexandergorin.foosball.ui.edit

import java.io.Serializable

sealed class EditMatchType : Serializable {
    object Add : EditMatchType()
    data class Edit(val matchId: Int) : EditMatchType()
}