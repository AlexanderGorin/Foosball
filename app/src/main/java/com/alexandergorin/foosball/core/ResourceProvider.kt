package com.alexandergorin.foosball.core

interface ResourceProvider {
    fun getString(id: Int): String
    fun getString(id: Int, vararg arguments: Any): String
}
