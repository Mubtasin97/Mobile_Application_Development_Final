package com.university.newsapp.repository

import com.university.newsapp.network.RetrofitClient

class PostRepository {

    private val api = RetrofitClient.instance

    suspend fun getAllPosts() = api.getAllPosts()

    suspend fun getPostById(id: Int) = api.getPostById(id)

    suspend fun getCommentsByPost(postId: Int) = api.getCommentsByPost(postId)

    suspend fun getAllUsers() = api.getAllUsers()

    suspend fun getUserById(id: Int) = api.getUserById(id)

    suspend fun getPostsByUser(userId: Int) = api.getPostsByUser(userId)
}