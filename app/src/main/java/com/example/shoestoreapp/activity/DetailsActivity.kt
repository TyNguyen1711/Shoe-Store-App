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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.model.User
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

class DetailsActivity : AppCompatActivity() {

    var isChange = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"

        val birthdayBtn = findViewById<ImageView>(R.id.birthdayBtn)
        val birthday: TextView = findViewById(R.id.birthdayEdit)
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        val bioEdit = findViewById<EditText>(R.id.bioEdit)
        val sex: TextView = findViewById(R.id.sexEdit)
        val phone: TextView = findViewById(R.id.phoneEdit)
        val email: TextView = findViewById(R.id.emailEdit)



        birthdayBtn.setOnClickListener {
            showDatePicker(birthday)
        }




        val userRepos = UserRepository()
        lifecycleScope.launch {
            val user = userRepos.getUser(userId)
            user.onSuccess { userData ->
                nameEdit.setText(userData.fullname)
                email.text = userData.email
                phone.text = userData.phoneNumber
                birthday.text = userData.birthday
                bioEdit.setText(userData.bio)
                sex.text = userData.sex
            }.onFailure { error ->
                println("Failed to fetch user information: ${error.message}")
            }
        }

        val sexBtn = findViewById<ImageView>(R.id.sexBtn)
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
                isChange = true
            }
        }

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener{
            lifecycleScope.launch {
                val user = userRepos.getUser(userId)
                user.onSuccess { userData ->
                    val newUser = User(userData.id,
                        userData.username,
                        email.text.toString(),
                        userData.searchHistory,
                        bioEdit.text.toString(),
                        sex.text.toString(),
                        birthday.text.toString(),
                        nameEdit.text.toString(),
                        phone.text.toString(),
                        userData.isAdmin,
                        userData.avatar
                    )
                    userRepos.updateUser(newUser)
                }.onFailure { error ->
                    println("Failed to fetch user information: ${error.message}")
                }
            }
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
                isChange = true
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
