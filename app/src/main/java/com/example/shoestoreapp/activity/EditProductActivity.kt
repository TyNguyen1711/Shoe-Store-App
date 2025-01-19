package com.example.shoestoreapp.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.utils.ObjectUtils
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.ImagesAdapter
import com.example.shoestoreapp.adapter.VariantsAdapter
import com.example.shoestoreapp.classes.ConfigCloudinary
import com.example.shoestoreapp.data.model.Product
import com.example.shoestoreapp.data.model.ProductVariant
import com.example.shoestoreapp.data.repository.ProductRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide
import com.example.shoestoreapp.data.repository.CategoryRepository

class EditProductActivity : AppCompatActivity() {
    private lateinit var thumbnailImageView: ImageView
    private lateinit var productImagesRecyclerView: RecyclerView
    private lateinit var nameEditText: EditText
    private lateinit var brandSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var discountPriceEditText: EditText
    private lateinit var salePercentageEditText: EditText
    private lateinit var variantsRecyclerView: RecyclerView
    private lateinit var btnDeleteThumbnail: ImageButton
    private lateinit var btnAddVariant: Button
    private lateinit var btnBack: Button
    private lateinit var variantsAdapter: VariantsAdapter
    private lateinit var currentProduct: Product
    private val productRepository = ProductRepository()
    private val imagesList = mutableListOf<Uri>()
    private val existingImageUrls = mutableListOf<String>()
    private var thumbnailUri: Uri? = null
    private var currentThumbnailUrl: String? = null
    private val variants = mutableListOf<ProductVariant>()
    private val categoryRepository = CategoryRepository()

    private var productId: String = ""
    private val PICK_THUMBNAIL_REQUEST = 1
    private val PICK_IMAGES_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        productId = intent.getStringExtra("product_id") ?: throw IllegalArgumentException("Product ID must be provided")

        initializeViews()
        setupRecyclerView()
        setupListeners()
        setupSpinners()

        fetchProductData(productId)
    }
    private fun setupSpinners() {
        val brands = listOf("Chọn thương hiệu", "Nike", "Adidas", "Puma", "Lacoste", "New Balance", "Rebook")
        val categories = listOf("Chọn danh mục", "MEN", "WOMEN", "UNISEX")

        val brandAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, brands)
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        brandSpinner.adapter = brandAdapter

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
    }
    private fun fetchProductData(productId: String) {
        lifecycleScope.launch {
            try {
                val result = productRepository.getProduct(productId)
                if (result.isSuccess) {
                    currentProduct = result.getOrNull() ?: return@launch
                    Log.d("112233: ", "${currentProduct}")
                    loadProductData()
                } else {
                    showError("Failed to fetch product data")
                }
            } catch (e: Exception) {
                showError("Error fetching product data: ${e.message}")
            }
        }
    }

    private fun loadProductData() {
        Log.d("222: ", "${currentProduct}")
        nameEditText.setText(currentProduct.name)
        descriptionEditText.setText(currentProduct.description)
        priceEditText.setText(currentProduct.price.toString())
        discountPriceEditText.setText(currentProduct.discountPrice?.toString())
        salePercentageEditText.setText(currentProduct.salePercentage.toString())

        if (currentProduct.thumbnail.isNotEmpty()) {
            currentThumbnailUrl = currentProduct.thumbnail
            Glide.with(this)
                .load(currentProduct.thumbnail)
                .into(thumbnailImageView)
            btnDeleteThumbnail.visibility = View.VISIBLE
        }

        if (currentProduct.images.isNotEmpty()) {
            existingImageUrls.addAll(currentProduct.images)
            updateExistingImagesView()
        }

        if (currentProduct.variants.isNotEmpty()) {
            variants.addAll(currentProduct.variants)
            variantsAdapter.notifyDataSetChanged()
        }

        fetchCategoryName(currentProduct.categoryId)

        setupSpinnersWithSelection()
    }

    private fun fetchCategoryName(categoryId: String) {
        lifecycleScope.launch {
            try {
                val result = categoryRepository.getCategory(categoryId)

                if (result.isSuccess) {
                    val category = result.getOrNull()
                    val categories = listOf("Chọn danh mục", "MEN", "WOMEN", "UNISEX")
                    val selectedCategory = categories.indexOf(category?.name)
                    categorySpinner.setSelection(selectedCategory)
                } else {
                    showError("Category not found")
                }
            } catch (e: Exception) {
                showError("Failed to load category: ${e.message}")
            }
        }
    }

    private fun setupRecyclerView() {
        variantsAdapter = VariantsAdapter(variants) { position ->
            variants.removeAt(position)
            variantsAdapter.notifyItemRemoved(position)
        }
        variantsRecyclerView.layoutManager = LinearLayoutManager(this)
        variantsRecyclerView.adapter = variantsAdapter
    }


    private fun updateExistingImagesView() {
        val allImages = mutableListOf<Uri>()
        allImages.addAll(existingImageUrls.map { Uri.parse(it) })
        allImages.addAll(imagesList)

        val adapter = ImagesAdapter(allImages) { position ->
            if (position < existingImageUrls.size) {
                existingImageUrls.removeAt(position)
            } else {
                imagesList.removeAt(position - existingImageUrls.size)
            }
            updateExistingImagesView()
        }

        productImagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productImagesRecyclerView.adapter = adapter
    }


    private fun initializeViews() {
        thumbnailImageView = findViewById(R.id.iv_thumbnail)
        productImagesRecyclerView = findViewById(R.id.rv_product_images)
        nameEditText = findViewById(R.id.et_name)
        brandSpinner = findViewById(R.id.sp_brand)
        categorySpinner = findViewById(R.id.sp_category)
        descriptionEditText = findViewById(R.id.et_description)
        priceEditText = findViewById(R.id.et_price)
        discountPriceEditText = findViewById(R.id.et_discount_price)
        salePercentageEditText = findViewById(R.id.et_sale_percentage)
        variantsRecyclerView = findViewById(R.id.rv_variants)
        btnAddVariant = findViewById(R.id.btn_add_variant)
        btnDeleteThumbnail = findViewById(R.id.btn_delete_thumbnail)
        btnBack = findViewById(R.id.btn_back)

        findViewById<TextView>(R.id.tv_title).text = "Chỉnh sửa sản phẩm"
        findViewById<Button>(R.id.btn_add).text = "Cập nhật"
    }

    private fun setupSpinnersWithSelection() {
        val brands = listOf("Chọn thương hiệu", "Nike", "Adidas", "Puma", "Lacoste", "New Balance", "Rebook")
        val categories = listOf("Chọn danh mục", "MEN", "WOMEN", "UNISEX")

        val brandAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, brands)
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        brandSpinner.adapter = brandAdapter

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        val brandPosition = brands.indexOf(currentProduct.brand)
        if (brandPosition != -1) {
            brandSpinner.setSelection(brandPosition)
        }

        val categoryPosition = categories.indexOf(currentProduct.categoryId)
        if (categoryPosition != -1) {
            categorySpinner.setSelection(categoryPosition)
        }
    }
    private fun showAddVariantDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_variant, null)
        val sizeEditText = dialogView.findViewById<EditText>(R.id.et_size)
        val stockEditText = dialogView.findViewById<EditText>(R.id.et_stock)

        AlertDialog.Builder(this)
            .setTitle("Add Variant")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val size = sizeEditText.text.toString()
                val stock = stockEditText.text.toString().toIntOrNull() ?: 0

                if (size.isNotBlank() && stock > 0) {
                    variants.add(ProductVariant(size = size, stock = stock))
                    variantsAdapter.notifyItemInserted(variants.size - 1)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
    private fun setupListeners() {
        findViewById<Button>(R.id.btn_add_thumbnail).setOnClickListener {
            openImagePicker(PICK_THUMBNAIL_REQUEST)
        }

        findViewById<Button>(R.id.btn_add_images).setOnClickListener {
            openImagePicker(PICK_IMAGES_REQUEST)
        }

        btnAddVariant.setOnClickListener {
            showAddVariantDialog()
        }

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_add).setOnClickListener {
            updateProduct()
        }
        btnBack.setOnClickListener {
            finish()
        }
        btnDeleteThumbnail.setOnClickListener {
            thumbnailUri = null
            currentThumbnailUrl = null
            thumbnailImageView.setImageDrawable(null)
            btnDeleteThumbnail.visibility = View.GONE
        }
    }

    private suspend fun uploadImageToCloudinary(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()

        return withContext(Dispatchers.IO) {
            val result = (application as ConfigCloudinary).cloudinary.uploader().upload(bytes, ObjectUtils.emptyMap())
            var imageUrl = result["url"] as String
            if (!imageUrl.startsWith("https://")) {
                imageUrl = imageUrl.replace("http://", "https://")
            }

            imageUrl
        }
    }

    private fun updateProduct() {
        if (!validateInputs()) {
            return
        }

        val loadingDialog = AlertDialog.Builder(this)
            .setMessage("Updating product...")
            .setCancelable(false)
            .create()
        loadingDialog.show()

        lifecycleScope.launch {
            try {

                val finalThumbnailUrl = if (thumbnailUri != null) {
                    uploadImageToCloudinary(thumbnailUri!!)
                } else {
                    currentThumbnailUrl
                } ?: ""

                val newImageUrls = imagesList.map { uri -> uploadImageToCloudinary(uri) }
                val finalImageUrls = existingImageUrls + newImageUrls

                val updatedProduct = currentProduct.copy(
                    name = nameEditText.text.toString(),
                    thumbnail = finalThumbnailUrl,
                    description = descriptionEditText.text.toString(),
                    brand = brandSpinner.selectedItem.toString(),
                    price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
                    discountPrice = discountPriceEditText.text.toString().toDoubleOrNull(),
                    salePercentage = salePercentageEditText.text.toString().toIntOrNull() ?: 0,
                    images = finalImageUrls,
                    variants = variants
                )

                val selectedCategory = categorySpinner.selectedItem.toString()
                val selectedBrand = brandSpinner.selectedItem.toString()

                val result = productRepository.updateProductWithNames(
                    product = updatedProduct,
                    categoryName = selectedCategory,
                    brandName = selectedBrand
                )

                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    if (result.isSuccess) {
                        Toast.makeText(this@EditProductActivity, "Product updated successfully", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        val error = result.exceptionOrNull()?.message ?: "Unknown error"
                        showError("Failed to update product: $error")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    showError("Error: ${e.message}")
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val selectedBrand = brandSpinner.selectedItem?.toString()
        val selectedCategory = categorySpinner.selectedItem?.toString()

        if (selectedBrand.isNullOrBlank() || selectedBrand == "Chọn thương hiệu") {
            showError("Please select a brand")
            return false
        }
        if (selectedCategory.isNullOrBlank() || selectedCategory == "Chọn danh mục") {
            showError("Please select a category")
            return false
        }
        if (nameEditText.text.isNullOrBlank()) {
            showError("Product name is required")
            return false
        }
        if (priceEditText.text.isNullOrBlank()) {
            showError("Price is required")
            return false
        }
        if (thumbnailUri == null && currentThumbnailUrl.isNullOrEmpty()) {
            showError("Thumbnail image is required")
            return false
        }
        if (variants.isEmpty()) {
            showError("At least one variant is required")
            return false
        }

        val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
        val discountPrice = discountPriceEditText.text.toString().toDoubleOrNull()
        if (discountPrice != null && discountPrice >= price) {
            showError("Discount price must be less than original price")
            return false
        }

        val salePercentage = salePercentageEditText.text.toString().toIntOrNull() ?: 0
        if (salePercentage !in 0..100) {
            showError("Sale percentage must be between 0 and 100")
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    private fun openImagePicker(requestCode: Int) {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            if (requestCode == PICK_IMAGES_REQUEST) {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PICK_THUMBNAIL_REQUEST -> {
                    data.data?.let { uri ->
                        thumbnailUri = uri
                        thumbnailImageView.setImageURI(uri)
                        btnDeleteThumbnail.visibility = View.VISIBLE
                    }
                }

                PICK_IMAGES_REQUEST -> {
                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            imagesList.add(imageUri) // Đảm bảo Uri được thêm chính xác
                        }
                    } else {
                        data.data?.let { uri ->
                            imagesList.add(uri) // Thêm Uri từ intent
                        }
                    }
                    updateExistingImagesView() // Cập nhật RecyclerView

                }
            }
        }
    }
}
