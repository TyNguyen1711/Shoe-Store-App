
package com.example.shoestoreapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shoestoreapp.R

class FaqFragment : Fragment() {
    private var faqItems: List<FAQItem> = emptyList()
    private var expandedPosition = -1
    private lateinit var faqList: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        faqItems = listOf(
            FAQItem("Is there a return policy ?",
                "Yes, you can return items within 14 days of receiving your order, as long as the product is unused and in its original packaging."),
            FAQItem("How can I track my order ?",
                "You can track your order by going to \"Order History\" in your account or by using the tracking number sent to your email."),
            FAQItem("What payment methods are accepted ?",
                "We accept payments via credit card, debit card, PayPal, and cash on delivery (depending on your location)."),
            FAQItem("How do I change my shipping address ?",
                "You can update your shipping address during checkout or in the \"Account Settings\" section before completing your order.")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        faqList = view.findViewById(R.id.faq_list)

        for ((index, item) in faqItems.withIndex()) {
            val itemView = inflater.inflate(R.layout.item_faq, faqList, false)
            itemView.findViewById<TextView>(R.id.question_text).text = item.question
            itemView.findViewById<TextView>(R.id.answer_text).visibility = if (index == expandedPosition) View.VISIBLE else View.GONE
            itemView.findViewById<TextView>(R.id.answer_text).text = item.answer

            itemView.setOnClickListener {
                expandedPosition = if (expandedPosition == index) -1 else index
                notifyDataSetChanged()
            }

            faqList.addView(itemView)
        }

        return view
    }

    private fun notifyDataSetChanged() {
        for (i in 0 until faqList.childCount) {
            val itemView = faqList.getChildAt(i)
            val answerText = itemView.findViewById<TextView>(R.id.answer_text)
            val viewBorder = itemView.findViewById<View>(R.id.view_border)
            answerText.visibility = if (i == expandedPosition) View.VISIBLE else View.GONE
            viewBorder.visibility = if (i == expandedPosition) View.VISIBLE else View.GONE
        }
    }

    data class FAQItem(
        val question: String,
        val answer: String
    )
}