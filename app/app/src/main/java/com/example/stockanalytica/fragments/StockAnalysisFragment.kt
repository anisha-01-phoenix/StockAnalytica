package com.example.stockanalytica.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.stockanalytica.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class StockAnalysisFragment : Fragment() {
    private val FILE_PICK_REQUEST_CODE = 123
    private val STORAGE_PERMISSION_CODE = 456
    private lateinit var stockdata: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stock_analysis, container, false)
        val upload: FloatingActionButton = view.findViewById(R.id.uploadButton)
        stockdata = view.findViewById(R.id.uploaded_data_text)

        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)
        val adapter = MyPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)?.text = "EDA"
        tabLayout.getTabAt(1)?.text = "Stock Prediction"

        upload.setOnClickListener {
            openFileChooser()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission()
            }
        }

        return view
    }

    inner class MyPagerAdapter(fm: androidx.fragment.app.FragmentManager) :
        androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> EDAScrollFragment()
                1 -> PredictionScrollFragment()
                else -> throw IllegalStateException("Unexpected position $position")

            }
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform your file access operations
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/csv"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    val inputStream: InputStream? = context?.contentResolver?.openInputStream(uri)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?
                    val stringBuilder = StringBuilder()
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                        stringBuilder.append('\n')
                    }
                    val fileData = stringBuilder.toString()
                    stockdata.text = fileData
                    Toast.makeText(
                        context,
                        "File uploaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error reading File!", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}