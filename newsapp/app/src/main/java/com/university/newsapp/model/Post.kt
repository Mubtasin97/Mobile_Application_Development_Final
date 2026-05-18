package com.university.newsapp.model

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)