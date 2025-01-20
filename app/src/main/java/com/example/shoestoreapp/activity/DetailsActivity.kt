package com.example.shoestoreapp.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.cloudinary.utils.ObjectUtils
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.ConfigCloudinary
import com.example.shoestoreapp.classes.SaveInformationDialog
import com.example.shoestoreapp.data.model.User
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private var thumbnailUri: Uri? = null
    private lateinit var avatar: ImageView
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"
        val thisUser = Firebase.auth.currentUser

        val birthdayBtn = findViewById<ImageView>(R.id.birthdayBtn)
        val birthday: TextView = findViewById(R.id.birthdayEdit)
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        val bioEdit = findViewById<EditText>(R.id.bioEdit)
        val sex: TextView = findViewById(R.id.sexEdit)
        val phone: TextView = findViewById(R.id.phoneEdit)
        val email: TextView = findViewById(R.id.emailEdit)
        val changeTV: TextView = findViewById(R.id.changeAvatarBtn)
        avatar = findViewById(R.id.avatar)


        birthdayBtn.setOnClickListener {
            showDatePicker(birthday)
        }

        changeTV.setOnClickListener{
            openImagePicker(1)
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
                if (userData.avatar.isNotEmpty()) {
                    Glide.with(this@DetailsActivity)
                        .load(userData.avatar) // Glide sẽ tải ảnh từ URL
                        .transform(CircleCrop())
                        .into(avatar) // Đặt ảnh vào ImageView avatar
                }
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
            }
        }

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener{

            lifecycleScope.launch {
                val user = userRepos.getUser(userId)
                user.onSuccess { userData ->
                    if (imageUrl==null) {
                        imageUrl = userData.avatar
                    }
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
                        imageUrl!!
                    )
                    if(userData != newUser) {
                        val shouldSave = SaveInformationDialog(this@DetailsActivity) // Sử dụng dialog
                        if (shouldSave) {
                            userRepos.updateUser(newUser) // Cập nhật thông tin nếu người dùng đồng ý
                            if(userData.email != newUser.email){
                                println("Ming3993 $thisUser")
                                thisUser!!.updateEmail(newUser.email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "User email address updated.")
                                        }
                                    }
                            }
                        }
                    }
                    finish()
                }.onFailure { error ->
                    println("Failed to fetch user information: ${error.message}")
                    finish()
                }
            }
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

    private suspend fun uploadImageToCloudinary(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val originalBytes = inputStream?.readBytes()

        // Giảm kích thước ảnh trước khi upload (giảm độ phân giải)
        val resizedBitmap = withContext(Dispatchers.IO) {
            Glide.with(this@DetailsActivity)
                .asBitmap()
                .load(uri)
                .submit(1000, 1000) // Thay đổi kích thước ảnh (800x800 px)
                .get()
        }

        val resizedByteArray = ByteArrayOutputStream().apply {
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, this) // Giảm chất lượng xuống 80%
        }.toByteArray()

        return withContext(Dispatchers.IO) {  // Đảm bảo thực hiện trong Dispatchers.IO
            val result = (application as ConfigCloudinary).cloudinary.uploader()
                .upload(resizedByteArray, ObjectUtils.emptyMap())
            result["url"] as String
        }
    }



    private fun openImagePicker(requestCode: Int) {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                1 -> {
                    data.data?.let { uri ->
                        thumbnailUri = uri

                        // Upload ảnh lên Cloudinary và lấy URL
                        lifecycleScope.launch {
                            try {
                                imageUrl = uploadImageToCloudinary(uri).replace("http://", "https://") // Dùng hàm upload đã có sẵn
                                // Cập nhật avatar bằng URL mới
                                Glide.with(this@DetailsActivity)
                                    .load(imageUrl) // Glide sẽ tải ảnh từ URL
                                    .transform(CircleCrop())
                                    .into(avatar) // Đặt ảnh vào ImageView avatar

                            } catch (e: Exception) {
                                println("Error uploading image: ${e.message}")
                            }
                        }
                    }
                }
            }
        }
    }
}
