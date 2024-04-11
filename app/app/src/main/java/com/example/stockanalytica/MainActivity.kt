package com.example.stockanalytica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.stockanalytica.databinding.ActivityMainBinding
import com.example.stockanalytica.fragments.PortfolioOptimisationFragment
import com.example.stockanalytica.fragments.ProfileFragment
import com.example.stockanalytica.fragments.StockAnalysisFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(StockAnalysisFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.stocks -> replaceFragment(StockAnalysisFragment())
                R.id.portfolio -> replaceFragment(PortfolioOptimisationFragment())
                R.id.profile -> replaceFragment(ProfileFragment())

                else -> {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()


    }
}