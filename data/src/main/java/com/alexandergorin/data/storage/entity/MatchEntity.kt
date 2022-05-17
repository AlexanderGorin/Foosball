package com.alexandergorin.data.storage.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MatchEntity(
    val person1: String,
    val score1: Int,
    val person2: String,
    val score2: Int,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
) : Serializable
