package com.example.shoestoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import android.widget.RatingBar
import com.example.shoestoreapp.data.model.Comment


class CommentAdapter(private val commentList: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTV: TextView = itemView.findViewById(R.id.comment_usernameTV)
        val emailTV: TextView = itemView.findViewById(R.id.comment_emailTV)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val commentTV: TextView = itemView.findViewById(R.id.commentTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        Log.d("CommentAdapter", "Binding comment: $comment")
        holder.usernameTV.text = comment.username
        holder.emailTV.text = comment.email
        holder.ratingBar.rating = comment.rating.toFloat()
        holder.commentTV.text = comment.comment
    }

    override fun getItemCount(): Int = commentList.size
}