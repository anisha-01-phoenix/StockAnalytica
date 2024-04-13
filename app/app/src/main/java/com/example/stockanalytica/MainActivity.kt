package com.example.stockanalytica

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.stockanalytica.databinding.ActivityMainBinding
import com.example.stockanalytica.fragments.HomeFragment
import com.example.stockanalytica.fragments.PortfolioOptimisationFragment
import com.example.stockanalytica.fragments.ProfileFragment
import com.example.stockanalytica.fragments.StockAnalysisFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.stocks -> replaceFragment(StockAnalysisFragment())
                R.id.portfolio -> replaceFragment(PortfolioOptimisationFragment())
                R.id.profile -> replaceFragment(ProfileFragment())

                else -> {
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                //logout
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}