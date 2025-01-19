package com.example.shoestoreapp.fragment


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.cloudinary.utils.ObjectUtils
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.ConfigCloudinary
import com.example.shoestoreapp.classes.SaveInformationDialog
import com.example.shoestoreapp.data.model.User
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class AdminInfoFragment : Fragment() {
    private var thumbnailUri: Uri? = null
    private lateinit var avatar: ImageView
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_my_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "example_user_id"

        val birthdayBtn = view.findViewById<ImageView>(R.id.birthdayBtn)
        val birthday: TextView = view.findViewById(R.id.birthdayEdit)
        val nameEdit = view.findViewById<EditText>(R.id.nameEdit)
        val bioEdit = view.findViewById<EditText>(R.id.bioEdit)
        val sex: TextView = view.findViewById(R.id.sexEdit)
        val phone: TextView = view.findViewById(R.id.phoneEdit)
        val email: TextView = view.findViewById(R.id.emailEdit)
        val changeTV: TextView = view.findViewById(R.id.changeAvatarBtn)
        avatar = view.findViewById(R.id.avatar)

        birthdayBtn.setOnClickListener {
            showDatePicker(birthday)
        }

        changeTV.setOnClickListener {
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
                    Glide.with(this@AdminInfoFragment)
                        .load(userData.avatar)
                        .transform(CircleCrop())
                        .into(avatar)
                }
            }.onFailure { error ->
                println("Failed to fetch user information: ${error.message}")
            }
        }

        val sexBtn = view.findViewById<ImageView>(R.id.sexBtn)
        sexBtn.setOnClickListener {
            showSexSelectionPopup(view, sex)
        }

        val backButton = view.findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            saveUserDetails(userId, nameEdit, email, bioEdit, sex, birthday, phone, userRepos)
        }
    }

    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textView.text = formattedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private suspend fun uploadImageToCloudinary(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBytes = inputStream?.readBytes()

        val resizedBitmap = withContext(Dispatchers.IO) {
            Glide.with(this@AdminInfoFragment)
                .asBitmap()
                .load(uri)
                .submit(1000, 1000)
                .get()
        }

        val resizedByteArray = ByteArrayOutputStream().apply {
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, this)
        }.toByteArray()

        return withContext(Dispatchers.IO) {
            val result = (requireActivity().application as ConfigCloudinary).cloudinary.uploader()
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

    private fun showSexSelectionPopup(view: View, sex: TextView) {
        val items = arrayOf("Male", "Female", "Other")
        val popupWindow = PopupWindow(requireContext())

        val layoutInflater = LayoutInflater.from(requireContext())
        val popupView = layoutInflater.inflate(R.layout.popup_spinner, null)
        val listView: ListView = popupView.findViewById(R.id.listView)
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

        popupWindow.contentView = popupView
        popupWindow.isFocusable = true
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT

        val location = IntArray(2)
        sex.getLocationOnScreen(location)
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + sex.height)

        listView.setOnItemClickListener { _, _, position, _ ->
            sex.text = items[position]
            popupWindow.dismiss()
        }
    }

    private fun saveUserDetails(
        userId: String,
        nameEdit: EditText,
        email: TextView,
        bioEdit: EditText,
        sex: TextView,
        birthday: TextView,
        phone: TextView,
        userRepos: UserRepository
    ) {
        lifecycleScope.launch {
            val user = userRepos.getUser(userId)
            user.onSuccess { userData ->
                if (imageUrl == null) {
                    imageUrl = userData.avatar
                }
                val newUser = User(
                    userData.id,
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
                if (userData != newUser) {
                    val shouldSave = SaveInformationDialog(requireContext())
                    if (shouldSave) {
                        userRepos.updateUser(newUser)
                    }
                }
            }.onFailure { error ->
                println("Failed to fetch user information: ${error.message}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                1 -> {
                    data.data?.let { uri ->
                        thumbnailUri = uri

                        lifecycleScope.launch {
                            try {
                                imageUrl = uploadImageToCloudinary(uri).replace("http://", "https://")
                                Glide.with(this@AdminInfoFragment)
                                    .load(imageUrl)
                                    .transform(CircleCrop())
                                    .into(avatar)
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
