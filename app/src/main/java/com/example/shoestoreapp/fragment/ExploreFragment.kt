package com.example.shoestoreapp.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.classes.Product
import com.example.shoestoreapp.classes.RecommendProductAdapter
import com.example.shoestoreapp.classes.Variant

class ExploreFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var searchBar: LinearLayout
    private lateinit var autoCompleteSearch: AutoCompleteTextView
    private lateinit var searchBtn: ImageButton
    private lateinit var recyclerView: RecyclerView

    // Sample data for products
    private val productList = listOf(
        Product(
            id = 1,
            name = "Giày Adidas EQ21 Nữ - Trắng Tím",
            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-01-800x800.jpg",
            description = "Adidas EQ21 là đôi giày chạy bộ mang lại sự thoải mái tối đa, giúp bạn tự tin hoàn thành từng bước chân đường. Adidas EQ21 là sự lựa chọn lý tưởng cho những người yêu thích chạy bộ, mang lại cảm giác thoải mái và hỗ trợ tối ưu trong suốt hành trình. Phần thân giày thoáng khí cho phép đôi chân luôn khô ráo và tươi mới trong suốt những quãng đường dài. Đệm nhẹ giúp từng bước chân êm ái và đầy năng lượng, từ lúc bắt đầu đến khi kết thúc.",
            images = listOf(
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-02-800x800.jpg",
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-04-800x800.jpg",
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-05-800x800.jpg",
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-06-800x800.jpg"
            ),
            categoryId = 2,
            brandId = 2,
            price = 2400000,
            discountPrice = 1990000,
            averageRating = 3f,
            reviewCount = 2,
            variants = listOf(
                Variant(id = 1, size = "35", stock = 25),
                Variant(id = 1, size = "36", stock = 25),
                Variant(id = 1, size = "37", stock = 25),
                Variant(id = 1, size = "38", stock = 25)
            )
        ),
        Product(
            id = 2,
            name = "Giày adidas Grand Court Base 2.0 Nam - Xám",
            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-01-800x800.jpg",
            description = "Adidas EQ21 là đôi giày chạy bộ mang lại sự thoải mái tối đa, giúp bạn tự tin hoàn thành từng bước chân đường. Adidas EQ21 là sự lựa chọn lý tưởng cho những người yêu thích chạy bộ, mang lại cảm giác thoải mái và hỗ trợ tối ưu trong suốt hành trình. Phần thân giày thoáng khí cho phép đôi chân luôn khô ráo và tươi mới trong suốt những quãng đường dài. Đệm nhẹ giúp từng bước chân êm ái và đầy năng lượng, từ lúc bắt đầu đến khi kết thúc.",
            images = listOf(
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-02-800x800.jpg",
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-03-800x800.jpg",
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-04-800x800.jpg",
                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-05-800x800.jpg"
            ),
            categoryId = 1,
            brandId = 2,
            price = 2400000,
            discountPrice = 1990000,
            averageRating = 5f,
            reviewCount = 1,
            variants = listOf(
                Variant(id = 1, size = "35", stock = 25),
                Variant(id = 1, size = "36", stock = 25),
                Variant(id = 1, size = "37", stock = 25),
                Variant(id = 1, size = "38", stock = 25)
            )
        )
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.explore, container, false)

        // Initialize views
        textView = view.findViewById(R.id.textView)
        searchBar = view.findViewById(R.id.searchBar)
        autoCompleteSearch = view.findViewById(R.id.autoCompleteSearch)
        searchBtn = view.findViewById(R.id.searchBtn)
        recyclerView = view.findViewById(R.id.recyclerView)

        setupRecyclerView()
        setupListeners()

        return view
    }

    private fun setupRecyclerView() {
        // Initialize ProductAdapter
        val productAdapter = RecommendProductAdapter(productList) { product ->
            // Handle item click
        }

        // Set up RecyclerView with GridLayoutManager (2 columns)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 là số cột, bạn có thể thay đổi số cột ở đây
        recyclerView.adapter = productAdapter
    }


    private fun setupListeners() {
        // Handle search button click
        searchBtn.setOnClickListener {
            val searchText = autoCompleteSearch.text.toString()
        }

    }
}
