package com.example.shoestoreapp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import com.bumptech.glide.Glide
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
import java.io.ByteArrayOutputStream

class AddProductActivity : AppCompatActivity() {
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
    private val productRepository = ProductRepository()
    private val imagesList = mutableListOf<Uri>()
    private var thumbnailUri: Uri? = null
    private val variants = mutableListOf<ProductVariant>()
    private lateinit var variantsAdapter: VariantsAdapter

    private val PICK_THUMBNAIL_REQUEST = 1
    private val PICK_IMAGES_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initializeViews()
        setupRecyclerView()
        setupListeners()
        setupSpinners()
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

    }

    private fun setupRecyclerView() {
        variantsAdapter = VariantsAdapter(variants) { position ->
            variants.removeAt(position)
            variantsAdapter.notifyItemRemoved(position)
        }
        variantsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddProductActivity)
            adapter = variantsAdapter
        }
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
            addProduct()
        }
        btnDeleteThumbnail.setOnClickListener {
            thumbnailUri = null
            thumbnailImageView.setImageDrawable(null)
            btnDeleteThumbnail.visibility = View.GONE
        }

    }

    suspend fun uploadImageToCloudinary(uri: Uri): String {
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

    private fun addProduct() {
        if (!validateInputs()) {
            return
        }

        val loadingDialog = AlertDialog.Builder(this)
            .setMessage("Adding product...")
            .setCancelable(false)
            .create()
        loadingDialog.show()

        lifecycleScope.launch {
            try {
                // Upload thumbnail
                val thumbnailUrl = uploadImageToCloudinary(thumbnailUri!!)

                // Upload product images
                val imageUrls = imagesList.map { uri ->
                    uploadImageToCloudinary(uri)
                }

                val product = Product(
                    name = nameEditText.text.toString(),
                    thumbnail = thumbnailUrl,
                    description = descriptionEditText.text.toString(),
                    brand = brandSpinner.selectedItem.toString(),
                    price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
                    discountPrice = discountPriceEditText.text.toString().toDoubleOrNull(),
                    salePercentage = salePercentageEditText.text.toString().toIntOrNull() ?: 0,
                    images = imageUrls,
                    variants = variants
                )

                // Save to Firebase using repository
                val result = productRepository.createProductWithNames(
                    product = product,
                    categoryName = categorySpinner.selectedItem.toString(),
                    brandName = brandSpinner.selectedItem.toString()
                )

                // Handle result
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    when {
                        result.isSuccess -> {
                            Toast.makeText(this@AddProductActivity, "Product added successfully", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        result.isFailure -> {
                            val error = result.exceptionOrNull()?.message ?: "Unknown error"
                            showError("Failed to add product: $error")
                        }
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
                        findViewById<ImageButton>(R.id.btn_delete_thumbnail).visibility = View.VISIBLE
                    }
                }
                PICK_IMAGES_REQUEST -> {
                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            imagesList.add(imageUri)
                        }
                    } else {
                        data.data?.let { uri ->
                            imagesList.add(uri)
                        }
                    }
                    updateImagesListView()
                }
            }
        }
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

    private fun updateImagesListView() {
        val adapter = ImagesAdapter(imagesList) { position ->
            imagesList.removeAt(position)
            productImagesRecyclerView.adapter?.notifyItemRemoved(position)
        }
        productImagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        productImagesRecyclerView.adapter = adapter
    }
    class ImagesAdapter(private val images: List<Uri>) :
        RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

        class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.iv_product_image)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.imageView.setImageURI(images[position])
        }

        override fun getItemCount() = images.size
    }

    private fun validateInputs(): Boolean {
        val selectedBrand = brandSpinner.selectedItem?.toString()
        val selectedCategory = categorySpinner.selectedItem?.toString()

        if (selectedBrand.isNullOrBlank()) {
            showError("Please select a brand")
            return false
        }
        if (selectedCategory.isNullOrBlank()) {
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
        if (thumbnailUri == null) {
            showError("Thumbnail image is required")
            return false
        }
        if (variants.isEmpty()) {
            showError("At least one variant is required")
            return false
        }
        return true
    }


    private fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
}

