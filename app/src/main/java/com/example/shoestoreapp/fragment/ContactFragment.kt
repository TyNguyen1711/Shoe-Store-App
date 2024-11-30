

package com.example.shoestoreapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.shoestoreapp.R

class ContactFragment : Fragment() {
    private var contactItems: List<ContactItem> = emptyList()
    private var expandedPosition = -1
    private lateinit var contactList: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactItems = listOf(
            ContactItem("Facebook",
                "https://www.facebook.com/groups/SV.HCMUS",
                R.drawable.icon_facebook),
            ContactItem("Zalo",
                "0123456789",
                R.drawable.icon_zalo),
            ContactItem("Instagram",
                "https://www.facebook.com/groups/SV.HCMUS",
                R.drawable.icon_instagram)
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        contactList = view.findViewById(R.id.contact_list)

        for ((index, item) in contactItems.withIndex()) {
            val itemView = inflater.inflate(R.layout.item_contact, contactList, false)

            val iconView = itemView.findViewById<ImageView>(R.id.imageView)
            iconView.setImageResource(item.iconResId)

            itemView.findViewById<TextView>(R.id.contact_name).text = item.name
            itemView.findViewById<TextView>(R.id.contact_info).visibility = if (index == expandedPosition) View.VISIBLE else View.GONE
            itemView.findViewById<TextView>(R.id.contact_info).text = item.info

            itemView.setOnClickListener {
                expandedPosition = if (expandedPosition == index) -1 else index
                notifyDataSetChanged()
            }

            contactList.addView(itemView)
        }

        return view
    }


    private fun notifyDataSetChanged() {
        for (i in 0 until contactList.childCount) {
            val itemView = contactList.getChildAt(i)
            val contactInfo = itemView.findViewById<TextView>(R.id.contact_info)
            val viewBorder = itemView.findViewById<View>(R.id.view_border)
            contactInfo.visibility = if (i == expandedPosition) View.VISIBLE else View.GONE
            viewBorder.visibility = if (i == expandedPosition) View.VISIBLE else View.GONE
        }
    }

    data class ContactItem(
        val name: String,
        val info: String,
        val iconResId: Int,
    )
}