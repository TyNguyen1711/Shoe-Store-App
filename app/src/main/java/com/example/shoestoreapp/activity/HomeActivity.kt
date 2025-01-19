package com.example.shoestoreapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.shoestoreapp.R
import com.example.shoestoreapp.fragment.AccountFragment
import com.example.shoestoreapp.fragment.ExploreFragment
import com.example.shoestoreapp.fragment.HomeFragment
import com.example.shoestoreapp.fragment.MyCartFragment
import com.example.shoestoreapp.fragment.WishlistFragment

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val code = intent.getStringExtra("code")
        val discount = intent.getStringExtra("discount")
        val selectedProductIds = intent.getStringArrayListExtra("selectedProductIds")
        Log.d("code", code.toString())
        Log.d("discount", discount.toString())
        println("Checked Product IDs_Home: $selectedProductIds")

        if (!code.isNullOrEmpty() && !discount.isNullOrEmpty()) {
            // Tạo Bundle và truyền dữ liệu vào MyCartFragment
            val bundle = Bundle().apply {
                putString("code", code)
                putString("discount", discount)
                putStringArrayList("selectedProductIds", selectedProductIds)
            }

            // Tạo MyCartFragment và truyền Bundle vào
            val myCartFragment = MyCartFragment().apply {
                arguments = bundle
            }

            switchFragment(myCartFragment)

        } else {
            // Hiển thị Fragment mặc định (HomeFragment)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // Gán sự kiện cho các button
        findViewById<Button>(R.id.button).setOnClickListener {
            switchFragment(HomeFragment())
        }

        // Gán sự kiện cho các button
        findViewById<Button>(R.id.button3).setOnClickListener {
            switchFragment(ExploreFragment())
        }

        // Gán sự kiện cho các button
        findViewById<Button>(R.id.button4).setOnClickListener {
            switchFragment(MyCartFragment())
        }

        // Gán sự kiện cho các button
        findViewById<Button>(R.id.button5).setOnClickListener {
            switchFragment(WishlistFragment())
        }

        // Gán sự kiện cho các button
        findViewById<Button>(R.id.button6).setOnClickListener {
            switchFragment(AccountFragment())
        }
    }

    // Hàm chuyển đổi fragment
    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}