package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.User

class UserAdapter(
    private val onDeleteClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(
        view: View,
        private val onDeleteClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val avatarImageView: ImageView = view.findViewById(R.id.imageViewAvatar)
        private val usernameTextView: TextView = view.findViewById(R.id.textViewUsername)
        private val emailTextView: TextView = view.findViewById(R.id.textViewEmail)
        private val deleteImageView: ImageView = view.findViewById(R.id.imageViewDelete)
        private val adminBadge: TextView = view.findViewById(R.id.textViewAdminBadge)
        private var currentUser: User? = null

        init {
            deleteImageView.setOnClickListener {
                currentUser?.let { user ->
                    onDeleteClick(user)
                }
            }
        }

        fun bind(user: User) {
            currentUser = user
            usernameTextView.text = user.username
            emailTextView.text = user.email

            // Load avatar using Glide
            Glide.with(itemView.context)
                .load(user.avatar)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .circleCrop()
                .into(avatarImageView)

            // Show/hide admin badge
            adminBadge.text = if (user.isAdmin) "Admin" else "User"
            adminBadge.visibility = View.VISIBLE
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User) =
            oldItem == newItem
    }
}