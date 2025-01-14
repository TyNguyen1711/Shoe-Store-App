package com.example.shoestoreapp.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shoestoreapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val notificationTV = findViewById<TextView>(R.id.loginTV_4)
        val emailET = findViewById<EditText>(R.id.emailET)
        val passwordET = findViewById<TextInputEditText>(R.id.passwordET)
        val forgotPasswordTV = findViewById<TextView>(R.id.forgotPasswordTV)
        val loginBtn = findViewById<Button>(R.id.signupBtn)
        val signupTV = findViewById<TextView>(R.id.signupTV)

        forgotPasswordTV.setOnClickListener() {
            forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            // val intent = Intent(this)
            // startActivity(intent)
        }

        loginBtn.setOnClickListener() {
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            notificationTV.text = ""

            if (TextUtils.isEmpty(email)) {
                notificationTV.text = getResources().getString(R.string.enter_email)
            }

            if (TextUtils.isEmpty(password)) {
                notificationTV.text = getResources().getString(R.string.enter_password)
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, switch to home activity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        notificationTV.text = task.exception?.message.toString()
/*                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()*/
                    }
                }
        }

        signupTV.setOnClickListener() {
            signupTV.setPaintFlags(signupTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}