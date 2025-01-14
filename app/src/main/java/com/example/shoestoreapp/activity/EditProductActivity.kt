//package com.example.shoestoreapp.activity
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.shoestoreapp.R
//import com.example.shoestoreapp.data.model.Product
//import com.bumptech.glide.Glide
//import androidx.recyclerview.widget.RecyclerView
//import android.widget.*
//import com.google.android.material.textfield.TextInputEditText
//
//class EditProductActivity : AppCompatActivity() {
//    private lateinit var ivThumbnail: ImageView
//    private lateinit var btnAddThumbnail: Button
//    private lateinit var rvProductImages: RecyclerView
//    private lateinit var btnAddImages: Button
//    private lateinit var etName: TextInputEditText
//    private lateinit var etBrand: TextInputEditText
//    private lateinit var etDescription: TextInputEditText
//    private lateinit var etCategoryId: TextInputEditText
//    private lateinit var etPrice: TextInputEditText
//    private lateinit var etDiscountPrice: TextInputEditText
//    private lateinit var etSalePercentage: TextInputEditText
//    private lateinit var btnSave: Button
//    private lateinit var tvTitle: TextView
//
//    private var thumbnailUri: Uri? = null
//    private val productImages = mutableListOf<Uri>()
//    private var editingProduct: Product? = null
//
//    private val thumbnailPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            result.data?.data?.let { uri ->
//                thumbnailUri = uri
//                Glide.with(this)
//                    .load(uri)
//                    .into(ivThumbnail)
//            }
//        }
//    }
//
//    private val imagesPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            result.data?.clipData?.let { clipData ->
//                for (i in 0 until clipData.itemCount) {
//                    productImages.add(clipData.getItemAt(i).uri)
//                }
//            } ?: result.data?.data?.let { uri ->
//                productImages.add(uri)
//            }
//            // TODO: Update RecyclerView with selected images
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_product)
//
//        initViews()
//        setupListeners()
//
//        // Check if we're editing an existing product
//        editingProduct = intent.getParcelableExtra("product")
//        editingProduct?.let { product ->
//            tvTitle.text = "Edit Product"
//            populateProductData(product)
//        }
//    }
//
//    private fun initViews() {
//        ivThumbnail = findViewById(R.id.ivThumbnail)
//        btnAddThumbnail = findViewById(R.id.btnAddThumbnail)
//        rvProductImages = findViewById(R.id.rvProductImages)
//        rvProductImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        btnAddImages = findViewById(R.id.btnAddImages)
//        etName = findViewById(R.id.etName)
//        etBrand = findViewById(R.id.etBrand)
//        etDescription = findViewById(R.id.etDescription)
//        etCategoryId = findViewById(R.id.etCategoryId)
//        etPrice = findViewById(R.id.etPrice)
//        etDiscountPrice = findViewById(R.id.etDiscountPrice)
//        etSalePercentage = findViewById(R.id.etSalePercentage)
//        btnSave = findViewById(R.id.btnSave)
//        tvTitle = findViewById(R.id.tvTitle)
//    }
//
//    private fun setupListeners() {
//        btnAddThumbnail.setOnClickListener {
//            openImagePicker(thumbnailPicker)
//        }
//
//        btnAddImages.setOnClickListener {
//            openImagePicker(imagesPicker)
//        }
//
//        btnSave.setOnClickListener {
//            if (validateInputs()) {
//                saveProduct()
//            }
//        }
//    }
//
//    private fun openImagePicker(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
//            type = "image/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        }
//        launcher.launch(intent)
//    }
//
//    private fun populateProductData(product: Product) {
//        etName.setText(product.name)
//        etBrand.setText(product.brand)
//        etDescription.setText(product.description)
//        etCategoryId.setText(product.categoryId)
//        etPrice.setText(product.price.toString())
//        etDiscountPrice.setText(product.discountPrice?.toString() ?: "")
//        etSalePercentage.setText(product.salePercentage.toString())
//
//        // Load thumbnail
//        Glide.with(this)
//            .load(product.thumbnail)
//            .into(ivThumbnail)
//
//        // TODO: Implement loading of product images into RecyclerView
//    }
//
//    private fun validateInputs(): Boolean {
//        var isValid = true
//
//        if (etName.text.isNullOrEmpty()) {
//            etName.error = "Name is required"
//            isValid = false
//        }
//        if (etPrice.text.isNullOrEmpty()) {
//            etPrice.error = "Price is required"
//            isValid = false
//        }
//        if (etBrand.text.isNullOrEmpty()) {
//            etBrand.error = "Brand is required"
//            isValid = false
//        }
//
//        return isValid
//    }
//
//    private fun saveProduct() {
//        val product = Product(
//            id = editingProduct?.id ?: "",
//            name = etName.text.toString(),
//            thumbnail = thumbnailUri?.toString() ?: editingProduct?.thumbnail ?: "",
//            description = etDescription.text.toString(),
//            categoryId = etCategoryId.text.toString(),
//            brand = etBrand.text.toString(),
//            price = etPrice.text.toString().toDoubleOrNull() ?: 0.0,
//            discountPrice = etDiscountPrice.text.toString().toDoubleOrNull(),
//            salePercentage = etSalePercentage.text.toString().toIntOrNull() ?: 0,
//            images = productImages.map { it.toString() }
//        )
//
//        // TODO: Save the product (e.g., send to database or API)
//        Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show()
//        finish()
//    }
//}
