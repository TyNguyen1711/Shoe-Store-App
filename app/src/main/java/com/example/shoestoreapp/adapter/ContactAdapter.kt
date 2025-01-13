package com.example.shoestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R

data class Contact(
    val namePhone: String,
    val addressLine1: String,
    val addressLine2: String
)

class ContactAdapter(private val contactList: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namePhoneText: TextView = view.findViewById(R.id.namePhoneText)
        val detailAdr: TextView = view.findViewById(R.id.detailAdr)
        val detailAdr2: TextView = view.findViewById(R.id.detailAdr2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address_1, parent, false)
        return ContactViewHolder(view)
    }


    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.namePhoneText.text = contact.namePhone
        holder.detailAdr.text = contact.addressLine1
        holder.detailAdr2.text = contact.addressLine2
    }

    override fun getItemCount(): Int = contactList.size
}