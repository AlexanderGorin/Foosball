package com.alexandergorin.foosball.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(id: Int): String = context.getString(id)

    override fun getString(id: Int, vararg arguments: Any) = context.getString(id, *arguments)

}