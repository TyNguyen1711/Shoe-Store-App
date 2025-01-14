package com.example.shoestoreapp.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shoestoreapp.R
import java.util.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)

        val birthdayBtn = findViewById<ImageView>(R.id.birthdayBtn)
        val birthday: TextView = findViewById(R.id.birthdayEdit)
        birthdayBtn.setOnClickListener {
            showDatePicker(birthday)
        }

        val sexBtn = findViewById<ImageView>(R.id.sexBtn)
        val sex: TextView = findViewById(R.id.sexEdit)
        sexBtn.setOnClickListener {
            val items = arrayOf("Male", "Female", "Other")
            val popupWindow = PopupWindow(this)

            val layoutInflater = LayoutInflater.from(this)
            val popupView = layoutInflater.inflate(R.layout.popup_spinner, null)
            val listView: ListView = popupView.findViewById(R.id.listView)
            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

            popupWindow.contentView = popupView
            popupWindow.isFocusable = true
            popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
            popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT

            val location = IntArray(2)
            sexBtn.getLocationOnScreen(location)
            popupWindow.showAtLocation(sexBtn, Gravity.NO_GRAVITY, location[0], location[1] + sexBtn.height)

            listView.setOnItemClickListener { _, _, position, _ ->
                sex.text = items[position]
                popupWindow.dismiss()
            }
        }

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener{
            finish()
        }
    }

    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textView.text = formattedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
