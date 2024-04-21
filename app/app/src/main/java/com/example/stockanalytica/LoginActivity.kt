package com.example.stockanalytica

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stockanalytica.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tosignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()
        binding.login.setOnClickListener {
            val username = binding.editText.text.toString().trim()
            val password = binding.editText2.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            loginUser(username, password)
        }
    }

    private fun loginUser(username: String, password: String) {
        // Retrieve email from the database using the username
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        databaseRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val email = snapshot.children.first().child("email").getValue(String::class.java)
                        email?.let {
                            auth.signInWithEmailAndPassword(it, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Login successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Login failed: ${task.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } ?: Toast.makeText(
                            this@LoginActivity,
                            "Failed to retrieve email",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Username not found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Database error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}