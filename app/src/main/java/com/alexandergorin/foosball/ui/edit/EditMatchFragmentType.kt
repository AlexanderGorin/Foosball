package com.alexandergorin.foosball.ui.edit

import java.io.Serializable

sealed class EditMatchFragmentType : Serializable {
    object Add : EditMatchFragmentType()
    data class Edit(val matchId: Int) : EditMatchFragmentType()
}