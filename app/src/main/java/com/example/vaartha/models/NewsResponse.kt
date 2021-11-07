package com.example.vaartha.models

import com.example.vaartha.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)