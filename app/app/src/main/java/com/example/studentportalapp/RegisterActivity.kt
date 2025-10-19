package com.example.studentportalapp
import AppDatabase
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentportalapp.databinding.ActivityRegisterBinding
import User

import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = AppDatabase.getDatabase(applicationContext)


        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }


        binding.btnRegister.setOnClickListener {
            val email = binding.etRegEmail.text.toString().trim()
            val password = binding.etRegPassword.text.toString().trim()
            val courseCode = binding.etRegCourseCode.text.toString().trim().ifEmpty { null }


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            lifecycleScope.launch {
                val existing = db.userDao().getByEmail(email)
                runOnUiThread {
                    if (existing != null) {
                        Toast.makeText(this@RegisterActivity, "Email đã được đăng ký.", Toast.LENGTH_SHORT).show()
                    } else {
                        lifecycleScope.launch {
                            val newUser = User(email = email, password = password, courseCode = courseCode)
                            db.userDao().insert(newUser)
                            runOnUiThread {
                                Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}