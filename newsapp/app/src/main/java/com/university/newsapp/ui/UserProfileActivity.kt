package com.university.newsapp.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.university.newsapp.R
import com.university.newsapp.model.Post
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UserProfileActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var textName: TextView
    private lateinit var textUsername: TextView
    private lateinit var textEmail: TextView
    private lateinit var textPhone: TextView
    private lateinit var textWebsite: TextView
    private lateinit var textCompany: TextView
    private lateinit var textCatchPhrase: TextView
    private lateinit var textPostsLoading: TextView
    private lateinit var userPostsContainer: LinearLayout

    private val repository = PostRepository()
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userId = intent.getIntExtra("userId", 0)

        imgAvatar = findViewById(R.id.imgAvatar)
        textName = findViewById(R.id.textName)
        textUsername = findViewById(R.id.textUsername)
        textEmail = findViewById(R.id.textEmail)
        textPhone = findViewById(R.id.textPhone)
        textWebsite = findViewById(R.id.textWebsite)
        textCompany = findViewById(R.id.textCompany)
        textCatchPhrase = findViewById(R.id.textCatchPhrase)
        textPostsLoading = findViewById(R.id.textPostsLoading)
        userPostsContainer = findViewById(R.id.userPostsContainer)

        loadUserProfile()
        loadUserPosts()
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val user = repository.getUserById(userId)

                textName.text = user.name
                textUsername.text = "@${user.username}"
                textEmail.text = "Email: ${user.email}"
                textPhone.text = "Phone: ${user.phone}"
                textWebsite.text = "Website: ${user.website}"
                textCompany.text = "Company: ${user.company.name}"
                textCatchPhrase.text = "Catchphrase: ${user.company.catchPhrase}"

                val avatarUrl = "https://ui-avatars.com/api/?name=${Uri.encode(user.name)}&background=6A1B9A&color=fff"

                Glide.with(this@UserProfileActivity)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(imgAvatar)

            } catch (e: Exception) {
                textName.text = getErrorMessage(e)
            }
        }
    }

    private fun loadUserPosts() {
        lifecycleScope.launch {
            try {
                textPostsLoading.visibility = View.VISIBLE

                val posts = repository.getPostsByUser(userId)

                userPostsContainer.removeAllViews()

                for (post in posts) {
                    addPostCard(post)
                }

                textPostsLoading.visibility = View.GONE
            } catch (e: Exception) {
                textPostsLoading.text = getErrorMessage(e)
            }
        }
    }

    private fun addPostCard(post: Post) {
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

        val titleText = TextView(this)
        titleText.text = post.title
        titleText.setTypeface(null, Typeface.BOLD)
        titleText.setTextColor(Color.parseColor("#6A1B9A"))
        titleText.textSize = 17f

        val bodyText = TextView(this)
        bodyText.text = post.body
        bodyText.maxLines = 2
        bodyText.textSize = 15f
        bodyText.setPadding(0, 8, 0, 0)

        layout.addView(titleText)
        layout.addView(bodyText)

        card.addView(layout)

        card.setOnClickListener {
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("postId", post.id)
            startActivity(intent)
        }

        userPostsContainer.addView(card)
    }

    private fun getErrorMessage(e: Exception): String {
        return when (e) {
            is HttpException -> "Server error: ${e.code()}"
            is IOException -> "Network error. Check your connection."
            else -> "Something went wrong: ${e.message}"
        }
    }
}