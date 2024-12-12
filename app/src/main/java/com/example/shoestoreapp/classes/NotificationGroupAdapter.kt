package com.example.shoestoreapp.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R

class NotificationGroupAdapter(private val notificationGroups: List<NotificationGroup>) :
    RecyclerView.Adapter<NotificationGroupAdapter.NotificationGroupViewHolder>() {

    class NotificationGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val notificationsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.notificationsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_group_item, parent, false)
        return NotificationGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationGroupViewHolder, position: Int) {
        val group = notificationGroups[position]
        holder.dateTextView.text = group.date

        val adapter = NotificationAdapter(group.notifications)
        holder.notificationsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.notificationsRecyclerView.adapter = adapter
    }

    override fun getItemCount(): Int = notificationGroups.size
}