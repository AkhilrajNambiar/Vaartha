package com.example.vaartha.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.vaartha.db.Convertors
import java.io.Serializable

// The reason we say that the class inherits from Serializable is because, when we pass this
// article as an argument through safe args, safeargs will automatically convert article,
// a complex datatype, to the primitive types

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    val author: String?,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
) : Serializable