package com.example.stockanalytica

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stockanalytica.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tologin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding.signup.setOnClickListener {
            val username = binding.signupUser.text.toString().trim()
            val email = binding.signupEmail.text.toString().trim()
            val password = binding.signupPass.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkUsernameUnique(username, email, password)
        }
    }

    private fun checkUsernameUnique(username: String, email: String, password: String) {
        val usernameRef = database.getReference("usernames").child(username)
        usernameRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                Toast.makeText(this, "Username is already taken.", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(username, email, password)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to check username uniqueness.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid
                userId?.let {
                    val userMap = mapOf("username" to username, "email" to email)
                    database.getReference("Users").child(it).setValue(userMap)
                    database.getReference("usernames").child(username).setValue(it)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                Toast.makeText(this, "Failed to save username.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                }
            } else {
                Toast.makeText(
                    this,
                    "Authentication failed: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}