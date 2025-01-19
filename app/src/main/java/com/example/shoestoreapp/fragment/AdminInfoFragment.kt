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
import com.cloudinary.utils.ObjectUtils
import com.example.shoestoreapp.R
import com.example.shoestoreapp.activity.HomeActivity
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
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        initializeViews(view)
        setupClickListeners(view)
        loadUserData(userId)
    }

    private fun initializeViews(view: View) {
        avatar = view.findViewById(R.id.avatar)
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<ImageView>(R.id.birthdayBtn).setOnClickListener {
            showDatePicker(view.findViewById(R.id.birthdayEdit))
        }

        view.findViewById<TextView>(R.id.changeAvatarBtn).setOnClickListener {
            openImagePicker(1)
        }

        view.findViewById<ImageView>(R.id.sexBtn).setOnClickListener {
            showSexSelectionPopup(view, view.findViewById(R.id.sexEdit))
        }


        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            handleSaveButton()
        }

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            handleLogout()
        }
    }

    private fun handleSaveButton() {
        val view = requireView()
        val userId = auth.currentUser?.uid ?: return

        saveUserDetails(
            userId = userId,
            nameEdit = view.findViewById(R.id.nameEdit),
            email = view.findViewById(R.id.emailEdit),
            bioEdit = view.findViewById(R.id.bioEdit),
            sex = view.findViewById(R.id.sexEdit),
            birthday = view.findViewById(R.id.birthdayEdit),
            phone = view.findViewById(R.id.phoneEdit),
            userRepos = UserRepository()
        )
    }

    private fun handleLogout() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đồng ý") { _, _ ->
                FirebaseAuth.getInstance().signOut()

                // Chuyển hướng người dùng về menu
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)

                activity?.finish()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun loadUserData(userId: String) {
        val userRepos = UserRepository()
        lifecycleScope.launch {
            val userResult = userRepos.getUser(userId)
            userResult.onSuccess { userData ->
                updateUIWithUserData(userData)
            }.onFailure { error ->
                Toast.makeText(context, "Không thể tải thông tin: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUIWithUserData(userData: User) {
        view?.let { view ->
            view.findViewById<EditText>(R.id.nameEdit).setText(userData.fullname)
            view.findViewById<TextView>(R.id.emailEdit).text = userData.email
            view.findViewById<TextView>(R.id.phoneEdit).text = userData.phoneNumber
            view.findViewById<TextView>(R.id.birthdayEdit).text = userData.birthday
            view.findViewById<EditText>(R.id.bioEdit).setText(userData.bio)
            view.findViewById<TextView>(R.id.sexEdit).text = userData.sex

            if (userData.avatar.isNotEmpty()) {
                Glide.with(this)
                    .load(userData.avatar)
                    .into(avatar)
            }
        }
    }

    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textView.text = formattedDate
            },
            year, month, day
        ).show()
    }

    private fun openImagePicker(requestCode: Int) {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), requestCode)
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

    private fun showSexSelectionPopup(view: View, sexTextView: TextView) {
        val items = arrayOf("Nam", "Nữ", "Khác")
        val popupWindow = PopupWindow(requireContext())

        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_spinner, null)

        val listView: ListView = popupView.findViewById(R.id.listView)
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

        popupWindow.apply {
            contentView = popupView
            isFocusable = true
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        val location = IntArray(2)
        sexTextView.getLocationOnScreen(location)
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + sexTextView.height)

        listView.setOnItemClickListener { _, _, position, _ ->
            sexTextView.text = items[position]
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
            try {
                val userResult = userRepos.getUser(userId)
                userResult.onSuccess { userData ->
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
                            Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.onFailure { error ->
                    Toast.makeText(context, "Lỗi khi lưu: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Đã xảy ra lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
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
                                    .into(avatar)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi khi tải ảnh lên: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}