package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.repository.ProductRepository
import com.example.shoestoreapp.data.repository.WishListRepository
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {
    private val wishlistRepository = WishListRepository()
    private val productRepository = ProductRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<View>(R.id.btnIntro).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }

        findViewById<View>(R.id.txtLogin).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


//        lifecycleScope.launch {
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "bM5LHhwLjMNQCn0qYLr0")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "o1KsR8EP2uwENbyznXsj")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "qRS7DStXHlCTaHZ8fn9u")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "sv3hMZC8EcOVkurNOaXY")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "vVTmaVW8dO8miDuaHZ09")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "vadTty3tZMBMBZjVirqo")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "xRpohv4fn1P6oeSmxiSk")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "yFputh8Dn4dsYrPuq66I")
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "z7dSFUDqx2RMdp744vPc")
//
//        }
        lifecycleScope.launch {

        }


    }

}










//
//
//package com.example.shoestoreapp.activity
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.shoestoreapp.MainActivity
//import com.example.shoestoreapp.R
//import com.example.shoestoreapp.data.repository.ProductRepository
//import com.example.shoestoreapp.data.repository.WishlistRepository
//import kotlinx.coroutines.launch
//
//class IntroActivity : AppCompatActivity() {
//        private val wishlistRepository = WishlistRepository()
//        private val productRepository = ProductRepository()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_intro)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        lifecycleScope.launch {
//            val result = wishlistRepository.getWishlistByUserId(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2")
//            if (result.isSuccess) {
//                val products = result.getOrNull() ?: emptyList()
//
//                Log.d("test10", "${products.size} products.")
//            } else {
//                val error = result.exceptionOrNull()
//                Log.e("test10", "Error: $error")
//            }
//        }
//        lifecycleScope.launch {
//            val result = productRepository.getAllProducts()
//            if (result.isSuccess) {
//                val products = result.getOrNull() ?: emptyList()
//
//                Log.d("ProductRepository", "${products.size} products.")
//            } else {
//                val error = result.exceptionOrNull()
//                Log.e("ProductRepository", "Error: $error")
//            }
//        }
//
//        lifecycleScope.launch {
//            wishlistRepository.addToWishlist(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2", productId = "7Y7yCSSZ50oIGMwqcMXV")
//        }
//        lifecycleScope.launch {
//            wishlistRepository.getWishlistByUserId(userId = "lyHYPLDPQaexgmxgYwMfULW8vLE2")
//        }
//
//        findViewById<View>(R.id.btnIntro).setOnClickListener {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//            finish()
//
//        }
//
//        findViewById<View>(R.id.txtLogin).setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }
//    }
//}



//
//
//
//
//
//

//package com.example.shoestoreapp.activity
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.shoestoreapp.R
//import com.example.shoestoreapp.data.model.Brand
//import com.example.shoestoreapp.data.model.Category
//import com.example.shoestoreapp.data.model.Product
//import com.example.shoestoreapp.data.model.ProductVariant
//import com.example.shoestoreapp.data.repository.BrandRepository
//import com.example.shoestoreapp.data.repository.CategoryRepository
//import com.example.shoestoreapp.data.repository.ProductRepository
//import kotlinx.coroutines.async
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.launch
//import java.util.UUID
//
//class IntroActivity : AppCompatActivity() {
//    private val categoryRepository = CategoryRepository()
//    private val brandRepository = BrandRepository()
//    private val productRepository = ProductRepository()
//
//    private val initialCategories = listOf(
//        Category(
//            id = UUID.randomUUID().toString(),
//            name = "MEN",
//            description = "A collection of shoes designed for men, including sneakers, formal shoes, and boots."
//        ),
//        Category(
//            id = UUID.randomUUID().toString(),
//            name = "WOMEN",
//            description = "Stylish and comfortable footwear for women, including heels, flats, and sneakers."
//        ),
//        Category(
//            id = UUID.randomUUID().toString(),
//            name = "UNISEX",
//            description = "High-performance shoes for various sports such as running, basketball, and football."
//        )
//    )
//
//    private val initialBrands = listOf(
//        Brand(id = UUID.randomUUID().toString(), name = "Nike"),
//        Brand(id = UUID.randomUUID().toString(), name = "Adidas"),
//        Brand(id = UUID.randomUUID().toString(), name = "Puma"),
//        Brand(id = UUID.randomUUID().toString(), name = "New Balance"),
//        Brand(id = UUID.randomUUID().toString(), name = "Rebook"),
//        Brand(id = UUID.randomUUID().toString(), name = "Lacoste"),
//
//        )
//
//    private val initialProducts = listOf(
//        Product(
//            id = "1",
//            name = "Giày Adidas EQ21 Nữ - Trắng Tím",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-01-800x800.jpg",
//            description = "Adidas EQ21 là đôi giày chạy bộ mang lại sự thoải mái tối đa, giúp bạn tự tin hoàn thành từng bước chân đường. Adidas EQ21 là sự lựa chọn lý tưởng cho những người yêu thích chạy bộ, mang lại cảm giác thoải mái và hỗ trợ tối ưu trong suốt hành trình. Phần thân giày thoáng khí cho phép đôi chân luôn khô ráo và tươi mới trong suốt những quãng đường dài. Đệm nhẹ giúp từng bước chân êm ái và đầy năng lượng, từ lúc bắt đầu đến khi kết thúc.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-02-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-04-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-05-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-eq21-nu-trang-tim-06-800x800.jpg"
//            ),
//            brand = "Adidas",
//            price = 2400000.0,
//            discountPrice = 1990000.0,
//            salePercentage = 20,
//            variants = listOf(
//                ProductVariant(id = "1", size = "35", stock = 25),
//                ProductVariant(id = "2", size = "36", stock = 25),
//                ProductVariant(id = "3", size = "37", stock = 25),
//                ProductVariant(id = "4", size = "38", stock = 25)
//            ),
//            averageRating = 3.0,
//            reviewCount = 2
//        ),
//
//        Product(
//            id = "2",
//            name = "Giày adidas Grand Court Base 2.0 Nam - Xám",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-01-800x800.jpg",
//            description = "Adidas EQ21 là đôi giày chạy bộ mang lại sự thoải mái tối đa, giúp bạn tự tin hoàn thành từng bước chân đường. Adidas EQ21 là sự lựa chọn lý tưởng cho những người yêu thích chạy bộ, mang lại cảm giác thoải mái và hỗ trợ tối ưu trong suốt hành trình. Phần thân giày thoáng khí cho phép đôi chân luôn khô ráo và tươi mới trong suốt những quãng đường dài. Đệm nhẹ giúp từng bước chân êm ái và đầy năng lượng, từ lúc bắt đầu đến khi kết thúc.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-02-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-03-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-04-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad5/giay-adidas-grand-court-base-2-nam-xam-05-800x800.jpg"
//            ),
//            brand = "Adidas",
//            price = 2400000.0,
//            discountPrice = 1990000.0,
//            salePercentage = 25,
//            variants = listOf(
//                ProductVariant(id = "1", size = "35", stock = 25),
//                ProductVariant(id = "2", size = "36", stock = 25),
//                ProductVariant(id = "3", size = "37", stock = 25),
//                ProductVariant(id = "4", size = "38", stock = 25)
//            ),
//            averageRating = 5.0,
//            reviewCount = 1
//        ),
//
//        Product(
//            id = "3",
//            name = "Giày Puma Shuffle Perforated Nam - Trắng Xanh",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-shuffle-perforated-nam-trang-xanh-01-800x800.jpg",
//            description = "Giày Puma Shuffle Perforated mẫu giày cổ điển lấy cảm hứng từ những 1980 của Puma. Chất liệu da cao cấp và đế cao su bền bỉ chắc chắn sẽ làm hài lòng những khách hàng khó tính nhất. Bạn sẽ luôn yên tâm rằng nó không bao giờ bị lỗi mốt. Giày Puma Shuffle Perforated shop của chúng tôi được phân phối chính hãng. Full box, đầy đủ phụ kiện.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-shuffle-perforated-nam-trang-xanh-02-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-shuffle-perforated-nam-trang-xanh-04-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-shuffle-perforated-nam-trang-xanh-03-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-shuffle-perforated-nam-trang-xanh-05-800x800.jpg"
//            ),
//            brand = "Puma",
//            price = 2600000.0,
//            discountPrice = 1690000.0,
//            salePercentage = 20,
//            variants = listOf(
//                ProductVariant(id = "1", size = "39", stock = 25),
//                ProductVariant(id = "2", size = "40", stock = 25),
//                ProductVariant(id = "3", size = "41", stock = 25),
//                ProductVariant(id = "4", size = "42", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//        Product(
//            id = "4",
//            name = "Giày Thể Thao Reebok Sole Fury SE DV6923 Nữ - Màu Trắng",
//            thumbnail = "https://cdn.vuahanghieu.com/unsafe/0x900/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2022/12/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-63a95bbc52f23-26122022153052.jpg",
//            description = "Đôi giày Reebok Sole Fury SE DV6923 được làm từ chất liệu vải dệt cao cấp cùng độ ôm được thiết kế đặc biệt hỗ trợ chuyển động cho đôi chân. Đế giày mềm nhẹ, có độ ma sát tốt hạn chế trơn trượt và mang lại sự thoải mái ngay cả khi bạn đi giày trong một khoảng thời gian dài.",
//            images = listOf(
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2021/08/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-6110e013e8203-09082021145811.jpg",
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2021/08/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-6110e013ed545-09082021145811.jpg",
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2021/08/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-6110e01405630-09082021145812.jpg",
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2021/08/gia-y-the-thao-reebok-sole-fury-se-dv6923-ma-u-tra-ng-size-36-6110e013ed545-09082021145811.jpg"
//            ),
//            brand = "Rebook",
//            price = 2100000.0,
//            discountPrice = 1890000.0,
//            salePercentage = 15,
//            variants = listOf(
//                ProductVariant(id = "1", size = "36", stock = 25),
//                ProductVariant(id = "2", size = "37", stock = 24),
//                ProductVariant(id = "3", size = "37.5", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//
//        Product(
//            id = "5",
//            name = "Giày Nike Air Max SC Nữ - Trắng Tím",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-01-800x800.jpg",
//            description = "Giày Nike Air Max SC mang nét huyền thoại của Nike, với bộ đệm Air Max trứ danh đây là mẫu giày có thể kết hợp với bất cứ trang phục nào mà bạn vẫn hoàn toàn tự tin trong mọi hoàn cảnh.\nGiày Nike Air Max SC tại Myshoes.vn được nhập khẩu chính hãng. Full box, đầy đủ phụ kiện.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-02-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-04-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-05-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/nike/nk8/giay-nike-air-max-sc-nu-trang-tim-03-800x800.jpg"
//            ),
//            brand = "Nike",
//            price = 2500000.0,
//            discountPrice = 1990000.0,
//            salePercentage = 25,
//            variants = listOf(
//                ProductVariant(id = "1", size = "36.5", stock = 25),
//                ProductVariant(id = "2", size = "37.5", stock = 24),
//                ProductVariant(id = "3", size = "38.5", stock = 25),
//                ProductVariant(id = "4", size = "39", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//
//        Product(
//            id = "6",
//            name = "Giày Puma Rebound v6 Low Nam - Đen Trắng",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/puma/pm1/giay-puma-rebound-v6-low-nam-den-trang-01-800x800.jpg",
//            description = "Giày Puma Rebound v6 Low là mẫu giày có thiết kế tuyệt đẹp cổ điển mà rất tinh tế. Chất liệu cao cấp và đế cao su bền bỉ chắc chắn sẽ làm hài lòng những khách hàng khó tính nhất. Bạn sẽ luôn yên tâm rằng nó không bao giờ bị lỗi mốt.\nGiày Puma Rebound v6 Low tại cửa hàng của chúng tôi được nhập khẩu chính hãng Full box, đầy đủ phụ kiện.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024/puma/pm1/giay-puma-rebound-v6-low-nam-den-trang-02-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/puma/pm1/giay-puma-rebound-v6-low-nam-den-trang-03-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/puma/pm1/giay-puma-rebound-v6-low-nam-den-trang-04-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/puma/pm1/giay-puma-rebound-v6-low-nam-den-trang-06-800x800.jpg"
//            ),
//            brand = "Puma",
//            price = 2550000.0,
//            salePercentage = 30,
//            discountPrice = 1790000.0,
//            variants = listOf(
//                ProductVariant(id = "1", size = "40", stock = 25),
//                ProductVariant(id = "2", size = "40.5", stock = 24),
//                ProductVariant(id = "3", size = "41", stock = 25),
//                ProductVariant(id = "4", size = "42", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//
//        Product(
//            id = "7",
//            name = "Giày Thời Trang Nam New Balance Xc-72 / Daydream Nam - Trắng",
//            thumbnail = "https://supersports.com.vn/cdn/shop/files/UXC72DG-1.jpg?v=1695896969&width=1000",
//            description = "XC-72 mang đến nguồn cảm hứng khám phá bất tận, với thiết kế bẻ cong thời gian được lấy ý tưởng từ bộ sưu tập xe hơi những năm 1970. Thiết kế đế ngoài đặc biệt hỗ trợ lực kéo tối đa và các đặc điểm góc cạnh được sử dụng tạo nên kiểu dáng độc đáo, thu hút ngay từ ánh nhìn đầu tiên. XC-72 là tương lai mà quá khứ mơ ước và đã được hiện thực hóa thành công.",
//            images = listOf(
//                "https://supersports.com.vn/cdn/shop/files/UXC72DG-2.jpg?v=1695896969&width=1000",
//                "https://supersports.com.vn/cdn/shop/files/UXC72DG-3.jpg?v=1695896969&width=1000",
//                "https://supersports.com.vn/cdn/shop/files/UXC72DG-4.jpg?v=1695896970&width=1000",
//                "https://supersports.com.vn/cdn/shop/files/UXC72DG-5.jpg?v=1695896969&width=1000"
//            ),
//            brand = "New Balance",
//            price = 2859000.0,
//            discountPrice = 1588000.0,
//            salePercentage = 45,
//            variants = listOf(
//                ProductVariant(id = "1", size = "40", stock = 25),
//                ProductVariant(id = "2", size = "40.5", stock = 24),
//                ProductVariant(id = "3", size = "41", stock = 25),
//                ProductVariant(id = "4", size = "42", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//
//        Product(
//            id = "8",
//            name = "Giày Nike Pegasus 41 Special Nam - Đen Xanh",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-02-800x800.jpg",
//            description = "Nike Pegasus 41 là siêu phẩm giày thể thao mới nhất nhà Nike trong năm 2024 này. Với thiết kế tập trung vào sự thoải mái và hiệu suất cao, đôi giày này hứa hẹn mang đến cho bạn một trải nghiệm vượt trội.\nGiày Nike Pegasus 41 Special là phiên bản với phối màu đặc biệt và họa tiết hiếm có lần đầu tiên xuất hiện của Nike.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-03-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-05-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-04-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-06-800x800.jpg"
//            ),
//            brand = "Nike",
//            price = 4110000.0,
//            discountPrice = 3790000.0,
//            salePercentage = 10,
//            variants = listOf(
//                ProductVariant(id = "1", size = "40", stock = 25),
//                ProductVariant(id = "2", size = "40.5", stock = 24),
//                ProductVariant(id = "3", size = "41", stock = 25),
//                ProductVariant(id = "4", size = "42", stock = 25),
//                ProductVariant(id = "5", size = "42.5", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//
//        Product(
//            id = "9",
//            name = "Giày Thể Thao Adidas NMD_R1 Shoes GX9525 Màu Trắng",
//            thumbnail = "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2024/03/giay-the-thao-adidas-nmd-nmd_r1-shoes-gx9525-mau-trang-65eeca6775838-11032024160959.jpg",
//            description = "XC-72 mang đến nguồn cảm hứng khám phá bất tận, với thiết kế bẻ cong thời gian được lấy ý tưởng từ bộ sưu tập xe hơi những năm 1970. Thiết kế đế ngoài đặc biệt hỗ trợ lực kéo tối đa và các đặc điểm góc cạnh được sử dụng tạo nên kiểu dáng độc đáo, thu hút ngay từ ánh nhìn đầu tiên. XC-72 là tương lai mà quá khứ mơ ước và đã được hiện thực hóa thành công. \nGiày Thể Thao Adidas NMD_R1 Shoes GX9525 Màu Trắng được hoàn thiện từ chất liệu vải cao cấp, thiết kế thoáng khí giúp bạn luôn cảm thấy thoải mái. Kiểu dáng gọn với độ ôm vừa phải mang lại cảm giác nâng đỡ rất linh hoạt phù hợp mang hàng ngày hay mọi hoạt động thể thao. \nPhần đế giày được làm bằng cao su có độ ma sát, chịu lực cao hạn chế trơn trượt. Công nghệ đế Boost được sử dụng để mang lại trải nghiệm đi chân thoải mái tuyệt đối. Đệm Boost cũng cung cấp năng lượng hoàn lại vô tận với mỗi sải chân. Phần trên của NMD_R1 đặc biệt mềm mại, ôm trọn bàn chân và vừa khít để tạo sự thoải mái tuyệt đối.",
//            images = listOf(
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2024/03/giay-the-thao-adidas-nmd-nmd_r1-shoes-gx9525-mau-trang-65eeca6775aa9-11032024160959.jpg",
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2024/03/giay-the-thao-adidas-nmd-nmd_r1-shoes-gx9525-mau-trang-65eeca6776205-11032024160959.jpg",
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2024/03/giay-the-thao-adidas-nmd-nmd_r1-shoes-gx9525-mau-trang-65eeca677647c-11032024160959.jpg",
//                "https://cdn.vuahanghieu.com/unsafe/0x500/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2024/03/giay-the-thao-adidas-nmd-nmd_r1-shoes-gx9525-mau-trang-65eeca6775fb0-11032024160959.jpg"
//            ),
//            brand = "Adidas",
//            price = 3800000.0,
//            discountPrice = 2350000.0,
//            salePercentage = 40,
//            variants = listOf(
//                ProductVariant(id = "1", size = "40", stock = 25),
//                ProductVariant(id = "2", size = "40.5", stock = 24),
//                ProductVariant(id = "3", size = "41", stock = 25),
//                ProductVariant(id = "4", size = "42", stock = 25),
//                ProductVariant(id = "5", size = "42.5", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//        Product(
//            id = "10",
//            name = "Giày Puma Slipstream Everywhere Nữ - Trắng",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2023/puma/pu01/giay-puma-slipstream-everywhere-nam-nu-trang-01-800x800.jpg",
//            description = "Giày Puma Slipstream Everywhere Nữ - Trắng mang đến thiết kế thời trang, năng động, với đế ngoài đặc biệt hỗ trợ lực kéo tối đa, kết hợp với kiểu dáng thể thao đầy thu hút.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2023/puma/pu01/giay-puma-slipstream-everywhere-nam-nu-trang-02-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/puma/pu01/giay-puma-slipstream-everywhere-nam-nu-trang-03-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/puma/pu01/giay-puma-slipstream-everywhere-nam-nu-trang-04-800x800.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/puma/pu01/giay-puma-slipstream-everywhere-nam-nu-trang-06-800x800.jpg"
//            ),
//            brand = "Puma",
//            price = 3200000.0,
//            discountPrice = 1990000.0,
//            salePercentage = 40,
//            variants = listOf(
//                ProductVariant(id = "1", size = "40", stock = 25),
//                ProductVariant(id = "2", size = "40.5", stock = 24),
//                ProductVariant(id = "3", size = "41", stock = 25),
//                ProductVariant(id = "4", size = "42", stock = 25),
//                ProductVariant(id = "5", size = "42.5", stock = 25)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//        Product(
//            id = "11",
//            name = "Giày adidas Grand Court Base 2.0 Nam Nữ - Trắng",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad4/giay-adidas-grand-court-base-2-nam-trang-01-600x315w.jpg",
//            description = "Giày adidas Grand Court Base 2.0 phiên bản nâng cấp, với thiết kế bền đẹp và năng động, thích hợp cho mọi hoạt động thể thao và hàng ngày.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad4/giay-adidas-grand-court-base-2-nam-trang-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad4/giay-adidas-grand-court-base-2-nam-trang-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad4/giay-adidas-grand-court-base-2-nam-trang-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad4/giay-adidas-grand-court-base-2-nam-trang-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad4/giay-adidas-grand-court-base-2-nam-trang-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad4/giay-adidas-grand-court-base-2-nam-trang-06-1000x1000.jpg"
//            ),
//            brand = "Adidas",
//            price = 2590000.0,
//            discountPrice = 2000000.0,
//            salePercentage = 15,
//            variants = listOf(
//                ProductVariant(id = "1", size = "36.5", stock = 26),
//                ProductVariant(id = "2", size = "37.0", stock = 22),
//                ProductVariant(id = "3", size = "40.0", stock = 4),
//                ProductVariant(id = "4", size = "40.5", stock = 22),
//                ProductVariant(id = "5", size = "41.0", stock = 26),
//                ProductVariant(id = "6", size = "42.0", stock = 10)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//        Product(
//            id = "12",
//            name = "Giày adidas Advancourt Base Nam - Trắng Xanh",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/adidas/ad2/giay-adidas-advancourt-base-nam-trang-xanh-01-600x315w.jpg",
//            description = "Giày adidas Advancourt Base với thiết kế cơ bản nhưng luôn giữ được sự hiện đại, dễ dàng kết hợp với mọi trang phục.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad2/giay-adidas-advancourt-base-nam-trang-xanh-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad2/giay-adidas-advancourt-base-nam-trang-xanh-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad2/giay-adidas-advancourt-base-nam-trang-xanh-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad2/giay-adidas-advancourt-base-nam-trang-xanh-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad2/giay-adidas-advancourt-base-nam-trang-xanh-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/adidas/ad2/giay-adidas-advancourt-base-nam-trang-xanh-06-1000x1000.jpg"
//            ),
//            brand = "Adidas",
//            price = 2890000.0,
//            discountPrice = 2000000.0,
//            salePercentage = 20,
//            variants = listOf(
//                ProductVariant(id = "1", size = "41", stock = 3),
//                ProductVariant(id = "2", size = "42", stock = 28),
//                ProductVariant(id = "3", size = "42.5", stock = 6)
//            ),
//            averageRating = 5.0,
//            reviewCount = 1
//        ),
//        Product(
//            id = "13",
//            name = "Giày Puma Scend Pro Engineered Nam - Xanh Rêu",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-xanh-reu-01-600x315w.jpg",
//            description = "Giày Puma Scend Pro Engineered là sự kết hợp hoàn hảo giữa phong cách và hiệu suất, mang đến trải nghiệm vượt trội cho cả tập luyện và sử dụng hàng ngày.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-xanh-reu-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-xanh-reu-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-xanh-reu-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-xanh-reu-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-xanh-reu-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-xanh-reu-06-1000x1000.jpg"
//            ),
//            brand = "Puma",
//            price = 2850000.0,
//            discountPrice = 1890000.0,
//            salePercentage = 25,
//            variants = listOf(
//                ProductVariant(id = "1", size = "39", stock = 21),
//                ProductVariant(id = "2", size = "40", stock = 22),
//                ProductVariant(id = "3", size = "40.5", stock = 5),
//                ProductVariant(id = "4", size = "41", stock = 3),
//                ProductVariant(id = "5", size = "42", stock = 28),
//                ProductVariant(id = "6", size = "42.5", stock = 17)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//        Product(
//            id = "14",
//            name = "Giày Puma Scend Pro Engineered Nam - Đen",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-den-01-600x315w.jpg",
//            description = "Giày Puma Scend Pro Engineered là sự kết hợp hoàn hảo giữa phong cách và hiệu suất, mang đến trải nghiệm vượt trội cho cả tập luyện và sử dụng hàng ngày.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-den-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-den-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-den-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-den-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-den-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/puma/giay-puma-scend-pro-engineered-nam-den-06-1000x1000.jpg"
//            ),
//            brand = "Adidas",
//            price = 2850000.0,
//            discountPrice = 1890000.0,
//            salePercentage = 35,
//            variants = listOf(
//                ProductVariant(id = "1", size = "39", stock = 22),
//                ProductVariant(id = "2", size = "40", stock = 22),
//                ProductVariant(id = "3", size = "40.5", stock = 21),
//                ProductVariant(id = "4", size = "41", stock = 10),
//                ProductVariant(id = "5", size = "42", stock = 27),
//                ProductVariant(id = "6", size = "42.5", stock = 6)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//        Product(
//            id = "15",
//            name = "Giày Asics GlideRide 2 Nam - Xanh",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-01-600x315w.jpg",
//            description = "Giày Asics GlideRide 2 là siêu phẩm chạy bộ tốt của Asics, tập trung vào việc tiết kiệm năng lượng tối đa cho người sử dụng. Ngoài ra, Asics GlideRide 2 sử dụng những công nghệ tiên tiến nhất và có thiết kế rất đẹp có thể sử dụng đi lại hàng ngày.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-06-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2022/asics/22.7/giay-asics-glideride-2-nam-xanh-07-1000x1000.jpg"
//            ),
//            brand = "Lacoste",
//            price = 4050000.0,
//            discountPrice = 2390000.0,
//            salePercentage = 45,
//            variants = listOf(
//                ProductVariant(id = "1", size = "40", stock = 24),
//                ProductVariant(id = "2", size = "41", stock = 24),
//                ProductVariant(id = "3", size = "42", stock = 14),
//                ProductVariant(id = "4", size = "42.5", stock = 18)
//            ),
//            averageRating = 5.0,
//            reviewCount = 11
//        ),
//        Product(
//            id = "16",
//            name = "Giày adidas Advancourt Base Nam - Trắng",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2023/adidas/ad6/giay-adidas-advancourt-base-nam-trang-01-600x315w.jpg",
//            description = "Giày adidas Advancourt Base được thừa hưởng lối thiết kế của đàn anh Giày adidas Advantage song vẫn có thêm nhiều điểm mới cải thiện.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2023/adidas/ad6/giay-adidas-advancourt-base-nam-trang-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/adidas/ad6/giay-adidas-advancourt-base-nam-trang-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/adidas/ad6/giay-adidas-advancourt-base-nam-trang-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/adidas/ad6/giay-adidas-advancourt-base-nam-trang-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/adidas/ad6/giay-adidas-advancourt-base-nam-trang-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/adidas/ad6/giay-adidas-advancourt-base-nam-trang-06-1000x1000.jpg"
//            ),
//            brand = "Adidas",
//            price = 2000000.0,
//            discountPrice = 1490000.0,
//            salePercentage = 25,
//            variants = listOf(
//                ProductVariant(id = "1", size = "40", stock = 24),
//                ProductVariant(id = "2", size = "41", stock = 24),
//                ProductVariant(id = "3", size = "42", stock = 14),
//                ProductVariant(id = "4", size = "42.5", stock = 14)
//            ),
//            averageRating = 5.0,
//            reviewCount = 3
//        ),
//        Product(
//            id = "17",
//            name = "Giày Lacoste T-Clip Velcro 223 Nam - Đen",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-t-clip-velcro-223-nam-den-01-600x315w.jpg",
//            description = "Giày Lacoste T-Clip Velcro 223 là mẫu giày thời trang của Lacoste lấy cảm hứng từ những năm 80 với thiết kế cổ điển, nhưng vẫn toát lên sang trọng đặc trưng của Lacoste.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-t-clip-velcro-223-nam-den-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-t-clip-velcro-223-nam-den-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-t-clip-velcro-223-nam-den-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-t-clip-velcro-223-nam-den-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-t-clip-velcro-223-nam-den-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024/lacoste/lc02/giay-lacoste-t-clip-velcro-223-nam-den-06-1000x1000.jpg"
//            ),
//            brand = "Lacoste",
//            price = 3850000.0,
//            discountPrice = 2590000.0,
//            salePercentage = 40,
//            variants = listOf(
//                ProductVariant(id = "1", size = "39", stock = 18),
//                ProductVariant(id = "2", size = "40", stock = 14),
//                ProductVariant(id = "3", size = "41", stock = 8),
//                ProductVariant(id = "4", size = "42", stock = 24),
//                ProductVariant(id = "5", size = "43", stock = 3)
//            ),
//            averageRating = 5.0,
//            reviewCount = 1
//        ),
//        Product(
//            id = "18",
//            name = "Giày Nike Air Zoom Pegasus 40 Nam - Trắng",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2023/nike/pegasus-40/giay-nike-air-zoom-pegasus-40-nam-trang-01-600x315w.jpg",
//            description = "Giày Nike Air Zoom Pegasus 40 được thiết kế với sự thoải mái và linh hoạt vượt trội, giúp bạn tăng tốc độ trong từng bước chạy.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2023/nike/pegasus-40/giay-nike-air-zoom-pegasus-40-nam-trang-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/nike/pegasus-40/giay-nike-air-zoom-pegasus-40-nam-trang-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/nike/pegasus-40/giay-nike-air-zoom-pegasus-40-nam-trang-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/nike/pegasus-40/giay-nike-air-zoom-pegasus-40-nam-trang-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/nike/pegasus-40/giay-nike-air-zoom-pegasus-40-nam-trang-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2023/nike/pegasus-40/giay-nike-air-zoom-pegasus-40-nam-trang-06-1000x1000.jpg"
//            ),
//            brand = "Nike",
//            price = 3800000.0,
//            discountPrice = 2490000.0,
//            salePercentage = 50,
//            variants = listOf(
//                ProductVariant(id = "1", size = "39", stock = 4),
//                ProductVariant(id = "2", size = "40", stock = 16),
//                ProductVariant(id = "3", size = "40.5", stock = 23),
//                ProductVariant(id = "4", size = "41", stock = 15),
//                ProductVariant(id = "5", size = "42", stock = 17)
//            ),
//            averageRating = 4.0,
//            reviewCount = 11
//        ),
//        Product(
//            id = "19",
//            name = "Giày Nike Air Winflo 11 Nam - Xám Xanh",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-air-winflo-11-nam-xam-xanh-01-600x315w.jpg",
//            description = "Giày Nike Air Winflo 11 là một trong những mẫu giày thể thao tốt nhất của Nike vừa được ra mắt. Với kiểu dáng cực đẹp cùng công nghệ đỉnh cao, Nike Air Winflo 11 hứa hẹn sẽ mẫu giày cực hot của Nike trong năm nay.",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-air-winflo-11-nam-xam-xanh-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-air-winflo-11-nam-xam-xanh-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-air-winflo-11-nam-xam-xanh-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-air-winflo-11-nam-xam-xanh-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-air-winflo-11-nam-xam-xanh-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-air-winflo-11-nam-xam-xanh-06-1000x1000.jpg"
//            ),
//            brand = "Nike",
//            price = 3220000.0,
//            discountPrice = 2890000.0,
//            salePercentage = 10,
//            variants = listOf(
//                ProductVariant(id = "1", size = "39", stock = 26),
//                ProductVariant(id = "2", size = "40", stock = 13),
//                ProductVariant(id = "3", size = "40.5", stock = 14),
//                ProductVariant(id = "4", size = "41", stock = 2),
//                ProductVariant(id = "5", size = "42", stock = 8),
//                ProductVariant(id = "6", size = "42.5", stock = 2),
//                ProductVariant(id = "7", size = "43", stock = 4)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        ),
//        Product(
//            id = "20",
//            name = "Giày Nike Pegasus 41 Special Nam - Đen Xanh",
//            thumbnail = "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-01-600x315w.jpg",
//            description = "",
//            images = listOf(
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-01-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-02-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-03-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-04-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-05-1000x1000.jpg",
//                "https://myshoes.vn/image/cache/catalog/2024_11/nike/giay-nike-pegasus-41-nam-den-xanh-06-1000x1000.jpg"
//            ),
//            brand = "Nike",
//            price = 4110000.0,
//            discountPrice = 3790000.0,
//            salePercentage = 10,
//
//            variants = listOf(
//                ProductVariant(id = "1", size = "39", stock = 17),
//                ProductVariant(id = "2", size = "40", stock = 12),
//                ProductVariant(id = "3", size = "40.5", stock = 14),
//                ProductVariant(id = "4", size = "41", stock = 15),
//                ProductVariant(id = "5", size = "42", stock = 15),
//                ProductVariant(id = "6", size = "42.5", stock = 26),
//                ProductVariant(id = "7", size = "43", stock = 10)
//            ),
//            averageRating = 0.0,
//            reviewCount = 0
//        )
//
//
//    )
//
//    private val productMappings = mapOf(
//        "1" to "WOMEN",
//        "2" to "MEN",
//        "3" to "MEN",
//        "4" to "WOMEN",
//        "5" to "WOMEN",
//        "6" to "MEN",
//        "7" to "MEN",
//        "8" to "MEN",
//        "9" to "UNISEX",
//        "10" to "WOMEN",
//        "11" to "UNISEX",
//        "12" to "MEN",
//        "13" to "MEN",
//        "14" to "MEN",
//        "15" to "MEN",
//        "16" to "MEN",
//        "17" to "MEN",
//        "18" to "MEN",
//        "19" to "MEN",
//        "20" to "MEN",
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_intro)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        initializeFirestoreData()
//    }
//
//    private fun initializeFirestoreData() {
//        lifecycleScope.launch {
//            try {
//                // Initialize categories
//                val categoryResults = initialCategories.map { category ->
//                    async {
//                        categoryRepository.updateCategory(category)
//                            .onFailure { exception ->
//                                Log.e(
//                                    "IntroActivity",
//                                    "Error adding category: ${category.name}",
//                                    exception
//                                )
//                            }
//                    }
//                }.awaitAll()
//
//                                // Initialize brands
//                val brandResults = initialBrands.map { brand ->
//                    async {
//                        brandRepository.updateBrand(brand)
//                            .onFailure { exception ->
//                                Log.e(
//                                    "IntroActivity",
//                                    "Error adding brand: ${brand.name}",
//                                    exception
//                                )
//                            }
//                    }
//                }.awaitAll()
//
//                // Initialize products
//                val productResults = initialProducts.map { product ->
//                    async {
//                        // Lấy danh mục sản phẩm từ productMappings dựa trên product.id
//                        val categoryName = productMappings[product.id]
//                            ?: return@async // Nếu không có categoryName, thì bỏ qua
//
//                        productRepository.createProductWithNames(
//                            product = product,
//                            categoryName = categoryName,
//                        ).onSuccess { createdProduct ->
//                            Log.d(
//                                "IntroActivity",
//                                "Product added successfully: ${createdProduct.id}"
//                            )
//                        }.onFailure { exception ->
//                            Log.e(
//                                "IntroActivity",
//                                "Error adding product: ${product.name}",
//                                exception
//                            )
//                        }
//                    }
//                }.awaitAll()
//
//                Log.d("IntroActivity", "Initial data loaded successfully")
//            } catch (e: Exception) {
//                Log.e("IntroActivity", "Error initializing data", e)
//                Toast.makeText(
//                    this@IntroActivity,
//                    "Error initializing app data",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }


//}


//
//
//
//
//
