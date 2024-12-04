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
import com.google.firebase.auth.userProfileChangeRequest

class SignupActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val notificationTV = findViewById<TextView>(R.id.signupTV_4)
        val usernameET = findViewById<EditText>(R.id.usernameET)
        val emailET = findViewById<EditText>(R.id.emailET)
        val passwordET = findViewById<TextInputEditText>(R.id.passwordET)
        val signupBtn = findViewById<Button>(R.id.signupBtn)
        val loginTV = findViewById<TextView>(R.id.loginTV)

        signupBtn.setOnClickListener() {
            val username = usernameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            if (TextUtils.isEmpty(username)) {
                notificationTV.text = getResources().getString(R.string.enter_username)
            }

            if (TextUtils.isEmpty(email)) {
                notificationTV.text = getResources().getString(R.string.enter_email)
            }

            if (TextUtils.isEmpty(password)) {
                notificationTV.text = getResources().getString(R.string.enter_password)
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set username through update user profile
                        val user = Firebase.auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }
                        user!!.updateProfile(profileUpdates)

                        // Return to login activity after finish sign up
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        loginTV.setOnClickListener() {
            loginTV.setPaintFlags(loginTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}