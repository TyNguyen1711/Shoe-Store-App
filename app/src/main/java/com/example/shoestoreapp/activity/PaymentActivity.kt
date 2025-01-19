package com.example.shoestoreapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.shoestoreapp.R
import coil.load



class PaymentActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        var hideStatus: Boolean = false
        val wallet = findViewById<TextView>(R.id.balanceWallet)
        val hideEye = findViewById<ImageView>(R.id.hideEyeImg)
        val money: String = "300.000"
        val notice = findViewById<TextView>(R.id.noticeText)
        val money20k = findViewById<TextView>(R.id.text20k)
        val money50k = findViewById<TextView>(R.id.text50k)
        val money100k = findViewById<TextView>(R.id.text100k)
        val money200k = findViewById<TextView>(R.id.text200k)
        val money500k = findViewById<TextView>(R.id.text500k)
        val money1000k = findViewById<TextView>(R.id.text1m)
        notice.visibility = View.INVISIBLE

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }

        val deposit = findViewById<ConstraintLayout>(R.id.deposit_block)
        val transfer = findViewById<ConstraintLayout>(R.id.transfer_block)

        deposit.setOnClickListener {
            deposit.setBackgroundResource(R.drawable.corner_shape_chosen)
            transfer.background = null;
        }

        transfer.setOnClickListener {
            transfer.setBackgroundResource(R.drawable.corner_shape_chosen)
            deposit.background = null;
        }


        var isFormatting = false

        val inputMoney = findViewById<EditText>(R.id.inputMoney)
        money20k.setOnClickListener { inputMoney.setText("20000đ") }
        money50k.setOnClickListener { inputMoney.setText("50000đ") }
        money100k.setOnClickListener { inputMoney.setText("100000đ") }
        money200k.setOnClickListener { inputMoney.setText("200000đ") }
        money500k.setOnClickListener { inputMoney.setText("500000đ") }
        money1000k.setOnClickListener { inputMoney.setText("1000000đ") }
        inputMoney.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()
                // Kiểm tra xem liệu text có chứa "đ" ở cuối không, nếu có thì không thêm nữa
                if (!text.endsWith("đ") && text.isNotEmpty()) {
                    val formattedText = formatMoney(text)
                    if (text != formattedText) {
                        inputMoney.setText(formattedText)
                        inputMoney.setSelection(formattedText.length - 1) // Giữ con trỏ ở cuối
                    }
                }
            }
        })

        hideEye.setOnClickListener {
            hideStatus = !hideStatus
            if (hideStatus) {
                wallet.text = "₫ $money"
            } else {
                wallet.text = "₫ ***"
            }
        }

        val qrCodeImageView = findViewById<ImageView>(R.id.qrCodeImageView)
        qrCodeImageView.visibility = ImageView.GONE
        val payButton = findViewById<Button>(R.id.payBtn)
        payButton.setOnClickListener {
            val moneyPay = inputMoney.text.toString()
            val moneyPayRaw = removeCurrencySymbol(moneyPay.toString())
            val format = formatMoney(moneyPayRaw)
            notice.text = "You are paying $format"
            val imageUrl =
                "https://img.vietqr.io/image/vpbank-0344448590-compact2.jpg?amount=$moneyPayRaw&addInfo=username%20yyyyy%20pay%20$moneyPayRaw&accountName=Tran%20Duc%20Minh"

            qrCodeImageView.load(imageUrl) {
                crossfade(true)
            }
            qrCodeImageView.visibility = ImageView.VISIBLE
            notice.visibility = View.VISIBLE
        }

    }

    fun formatMoney(value: String): String {
        var formatted = value

        // Đảm bảo rằng số nhập vào là số hợp lệ và có thể chuyển thành Long
        try {
            formatted = formatted // Xóa dấu phẩy nếu có
                .toLong().toString() // Chuyển giá trị thành số nguyên
                .reversed() // Đảo ngược chuỗi
                .chunked(3) // Chia thành các khối 3 ký tự
                .joinToString(".") // Kết hợp lại thành chuỗi với dấu chấm
                .reversed() // Đảo ngược lại để có kết quả đúng
        } catch (e: Exception) {
            // Nếu có lỗi, chỉ trả về giá trị ban đầu
            formatted = value
        }

        return formatted + "đ" // Thêm dấu "đ" vào cuối
    }

    fun removeCurrencySymbol(value: String): String {
        return value.replace("đ", "") // Loại bỏ ký tự "đ"
    }
}


