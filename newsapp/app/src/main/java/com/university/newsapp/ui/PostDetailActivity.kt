package com.university.newsapp.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.university.newsapp.R
import com.university.newsapp.model.User
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostDetailActivity : AppCompatActivity() {

    private lateinit var textPostLoading: TextView
    private lateinit var textPostTitle: TextView
    private lateinit var textPostBody: TextView
    private lateinit var textAuthorLoading: TextView
    private lateinit var cardAuthor: CardView
    private lateinit var textAuthorName: TextView
    private lateinit var textAuthorEmail: TextView
    private lateinit var textAuthorCompany: TextView
    private lateinit var textCommentsLoading: TextView
    private lateinit var commentsContainer: LinearLayout

    private val repository = PostRepository()
    private var currentUser: User? = null
    private var postId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        postId = intent.getIntExtra("postId", 0)

        textPostLoading = findViewById(R.id.textPostLoading)
        textPostTitle = findViewById(R.id.textPostTitle)
        textPostBody = findViewById(R.id.textPostBody)
        textAuthorLoading = findViewById(R.id.textAuthorLoading)
        cardAuthor = findViewById(R.id.cardAuthor)
        textAuthorName = findViewById(R.id.textAuthorName)
        textAuthorEmail = findViewById(R.id.textAuthorEmail)
        textAuthorCompany = findViewById(R.id.textAuthorCompany)
        textCommentsLoading = findViewById(R.id.textCommentsLoading)
        commentsContainer = findViewById(R.id.commentsContainer)

        cardAuthor.setOnClickListener {
            currentUser?.let {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("userId", it.id)
                startActivity(intent)
            }
        }

        loadPost()
        loadComments()
    }

    private fun loadPost() {
        lifecycleScope.launch {
            try {
                textPostLoading.visibility = View.VISIBLE

                val post = repository.getPostById(postId)

                textPostTitle.text = post.title
                textPostBody.text = post.body
                textPostLoading.visibility = View.GONE

                loadAuthor(post.userId)
            } catch (e: Exception) {
                textPostLoading.text = getErrorMessage(e)
            }
        }
    }

    private fun loadAuthor(userId: Int) {
        lifecycleScope.launch {
            try {
                textAuthorLoading.visibility = View.VISIBLE

                val user = repository.getUserById(userId)
                currentUser = user

                textAuthorName.text = user.name
                textAuthorEmail.text = user.email
                textAuthorCompany.text = "Company: ${user.company.name}"

                textAuthorLoading.visibility = View.GONE
            } catch (e: Exception) {
                textAuthorLoading.text = getErrorMessage(e)
            }
        }
    }

    private fun loadComments() {
        lifecycleScope.launch {
            try {
                textCommentsLoading.visibility = View.VISIBLE

                val comments = repository.getCommentsByPost(postId)

                commentsContainer.removeAllViews()

                for (comment in comments) {
                    addCommentCard(
                        name = comment.name,
                        email = comment.email,
                        body = comment.body
                    )
                }

                textCommentsLoading.visibility = View.GONE
            } catch (e: Exception) {
                textCommentsLoading.text = getErrorMessage(e)
            }
        }
    }

    private fun addCommentCard(name: String, email: String, body: String) {
        val card = CardView(this)
        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.setMargins(0, 12, 0, 0)
        card.layoutParams = cardParams
        card.radius = 18f
        card.cardElevation = 4f
        card.setContentPadding(18, 18, 18, 18)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val nameText = TextView(this)
        nameText.text = name
        nameText.setTypeface(null, Typeface.BOLD)
        nameText.setTextColor(Color.parseColor("#6A1B9A"))
        nameText.textSize = 16f

        val emailText = TextView(this)
        emailText.text = email
        emailText.textSize = 14f

        val bodyText = TextView(this)
        bodyText.text = body
        bodyText.textSize = 15f
        bodyText.setPadding(0, 8, 0, 0)

        layout.addView(nameText)
        layout.addView(emailText)
        layout.addView(bodyText)

        card.addView(layout)
        commentsContainer.addView(card)
    }

    private fun getErrorMessage(e: Exception): String {
        return when (e) {
            is HttpException -> "Server error: ${e.code()}"
            is IOException -> "Network error. Check your connection."
            else -> "Something went wrong: ${e.message}"
        }
    }
}