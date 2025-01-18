package com.example.shoestoreapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shoestoreapp.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var email: String = ""
    private lateinit var emailTV: TextView
    private lateinit var sendBtn: Button
    private lateinit var emailET: EditText
    private lateinit var backBtn: Button
    private lateinit var sendPB: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sendPB = findViewById(R.id.sendPB)
        backBtn = findViewById(R.id.backBtn)
        emailET = findViewById(R.id.emailET)
        sendBtn = findViewById(R.id.sendBtn)
        emailTV = findViewById(R.id.notificationTV)

        sendBtn.setOnClickListener{
            email = emailET.text.toString()
            if (email.isNotEmpty()) {
                resetPassword()
            }
            else {
                emailTV.text = "Email can't be error"
            }
        }

        backBtn.setOnClickListener{
            finish()
        }
    }

    private fun resetPassword() {
        sendPB.visibility = VISIBLE
        sendBtn.visibility = INVISIBLE

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener { e ->
            emailTV.text = "Error: ${e.message}"
            sendPB.visibility = INVISIBLE
            sendBtn.visibility = VISIBLE
        }
    }
}