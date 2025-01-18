package com.example.shoestoreapp.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.shoestoreapp.R
import com.example.shoestoreapp.data.repository.UserRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            lifecycleScope.launch {
                var flag = false
                val userRepo = userRepository.getUser(currentUser.uid)
                Log.d("test13", "User: $userRepo")
                userRepo.onSuccess {
                    flag = it.isAdmin
                }.onFailure {
                    flag = false
                }
                if (flag) {
                    val intent = Intent(this@LoginActivity, DashboardAdmin::class.java)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
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
        val loginBtn = findViewById<Button>(R.id.sendBtn)
        val signupTV = findViewById<TextView>(R.id.signupTV)

        forgotPasswordTV.setOnClickListener() {
            forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            Log.d("ForgotPassword1", "First")
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
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
                        lifecycleScope.launch {
                            val user = Firebase.auth.currentUser
                            var flag = false
                            val userRepo = userRepository.getUser(user!!.uid)
                            Log.d("test13", "User: $userRepo")
                            userRepo.onSuccess {
                                flag = it.isAdmin
                            }.onFailure {
                                flag = false
                            }
                            if (flag) {
                                Toast.makeText(this@LoginActivity, "Admin", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                // Sign in success, switch to home activity
                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
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