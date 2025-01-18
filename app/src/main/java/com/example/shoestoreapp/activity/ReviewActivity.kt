package com.example.shoestoreapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.CommentAdapter
import com.example.shoestoreapp.data.model.Comment
import com.example.shoestoreapp.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ReviewActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private var commentList = mutableListOf<Comment>()
    private val reviewRepository = ReviewRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_review)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productId = intent.getStringExtra("productId")

        recyclerView = findViewById(R.id.reviewRC)
        recyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(commentList)
        recyclerView.adapter = commentAdapter

        lifecycleScope.launch {
            val reviewRepo = reviewRepository.getReview(productId!!)
            reviewRepo.onSuccess { items ->
                commentList.clear()
                commentList.addAll(items.commentList)
                Log.d("Review Success1", "$commentList")
                commentAdapter.notifyDataSetChanged()
            }.onFailure { error ->
                Log.d("Review Error", "$error")
            }
        }

        val backBtn = findViewById<Button>(R.id.reviewact_backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}