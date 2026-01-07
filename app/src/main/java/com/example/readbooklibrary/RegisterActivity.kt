package com.example.readbooklibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.readbooklibrary.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerBtn.setOnClickListener {
            registerUser()
        }

        binding.goToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {

        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()
        val password2 = binding.passwordAgainEt.text.toString().trim()


        if (email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != password2) {
            Toast.makeText(this, "Şifreler uyuşmuyor", Toast.LENGTH_SHORT).show()
            return
        }


        if (password.length < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalıdır", Toast.LENGTH_SHORT).show()
            return
        }


        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->

                val error = (exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode

                when (error) {
                    "ERROR_INVALID_EMAIL" ->
                        Toast.makeText(this, "Geçersiz email formatı", Toast.LENGTH_SHORT).show()

                    "ERROR_EMAIL_ALREADY_IN_USE" ->
                        Toast.makeText(this, "Bu email zaten kayıtlı!", Toast.LENGTH_SHORT).show()

                    "ERROR_WEAK_PASSWORD" ->
                        Toast.makeText(this, "Şifre çok zayıf (en az 6 karakter)", Toast.LENGTH_SHORT).show()

                    else ->
                        Toast.makeText(this, "Hata: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
