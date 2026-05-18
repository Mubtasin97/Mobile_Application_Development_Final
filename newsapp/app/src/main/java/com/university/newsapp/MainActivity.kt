package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.university.newsapp.R
import com.university.newsapp.model.Post
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerPosts: RecyclerView
    private lateinit var recyclerUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var textError: TextView
    private lateinit var btnRetry: Button
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var postAdapter: PostAdapter
    private lateinit var userAdapter: UserAdapter

    private val repository = PostRepository()
    private var allPosts = listOf<Post>()
    private var currentTab = "posts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        recyclerPosts = findViewById(R.id.recyclerPosts)
        recyclerUsers = findViewById(R.id.recyclerUsers)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        textError = findViewById(R.id.textError)
        btnRetry = findViewById(R.id.btnRetry)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        postAdapter = PostAdapter(emptyList()) { post ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("postId", post.id)
            startActivity(intent)
        }

        userAdapter = UserAdapter(emptyList()) { user ->
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("userId", user.id)
            startActivity(intent)
        }

        recyclerPosts.layoutManager = LinearLayoutManager(this)
        recyclerPosts.adapter = postAdapter

        recyclerUsers.layoutManager = LinearLayoutManager(this)
        recyclerUsers.adapter = userAdapter

        setupSearch()
        setupBottomNavigation()

        swipeRefresh.setOnRefreshListener {
            loadPosts()
        }

        btnRetry.setOnClickListener {
            if (currentTab == "posts") {
                loadPosts()
            } else {
                loadUsers()
            }
        }

        loadPosts()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPosts(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navPosts -> {
                    currentTab = "posts"
                    searchView.visibility = View.VISIBLE
                    swipeRefresh.visibility = View.VISIBLE
                    recyclerUsers.visibility = View.GONE
                    filterPosts(searchView.query.toString())
                    true
                }

                R.id.navUsers -> {
                    currentTab = "users"
                    searchView.visibility = View.GONE
                    swipeRefresh.visibility = View.GONE
                    recyclerUsers.visibility = View.VISIBLE
                    loadUsers()
                    true
                }

                else -> false
            }
        }
    }

    private fun loadPosts() {
        showLoading()

        lifecycleScope.launch {
            try {
                allPosts = repository.getAllPosts()
                postAdapter.updateList(allPosts)
                showPostsSuccess()
            } catch (e: HttpException) {
                showError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            } finally {
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun loadUsers() {
        showLoading()

        lifecycleScope.launch {
            try {
                val users = repository.getAllUsers()
                userAdapter.updateList(users)
                showUsersSuccess()
            } catch (e: HttpException) {
                showError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun filterPosts(query: String) {
        val filteredPosts = if (query.isBlank()) {
            allPosts
        } else {
            allPosts.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }

        postAdapter.updateList(filteredPosts)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        recyclerPosts.visibility = View.GONE
        recyclerUsers.visibility = View.GONE
    }

    private fun showPostsSuccess() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        swipeRefresh.visibility = View.VISIBLE
        recyclerPosts.visibility = View.VISIBLE
        recyclerUsers.visibility = View.GONE
    }

    private fun showUsersSuccess() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        swipeRefresh.visibility = View.GONE
        recyclerPosts.visibility = View.GONE
        recyclerUsers.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        swipeRefresh.visibility = View.GONE
        recyclerPosts.visibility = View.GONE
        recyclerUsers.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        textError.text = message
    }
}