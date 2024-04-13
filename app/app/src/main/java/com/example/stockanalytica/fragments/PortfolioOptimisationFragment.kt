package com.example.stockanalytica.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.stockanalytica.R
import com.example.stockanalytica.databinding.FragmentPortfolioOptimisationBinding
import java.util.Calendar

class PortfolioOptimisationFragment : Fragment() {

    private var _binding: FragmentPortfolioOptimisationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioOptimisationBinding.inflate(inflater, container, false)
        val view = binding.root
        setupSpinners()
        setupDatePickers()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDatePickers() {
        binding.startDateCalendarButton.setOnClickListener {
            showDatePickerForStartDate()
        }

        binding.endDateCalendarButton.setOnClickListener {
            showDatePickerForEndDate()
        }
    }

    private fun showDatePickerForStartDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                binding.startDateEditText.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun showDatePickerForEndDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                binding.endDateEditText.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun setupSpinners() {
        val companyAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.company_options,
            android.R.layout.simple_spinner_item
        )
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.companySpinner.adapter = companyAdapter
        binding.companySpinner.setSelection(0)

        binding.companySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCompany = parent?.getItemAtPosition(position).toString()
                    binding.companyName.text = selectedCompany
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    binding.companyName.text = "GOOGL"
                }
            }

        val methodAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.method_options,
            android.R.layout.simple_spinner_item
        )
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.methodSpinner.adapter = methodAdapter
        binding.methodSpinner.setSelection(0) // Set default selection to the first option

        binding.methodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedMethod = parent?.getItemAtPosition(position).toString()
                binding.methodName.text = selectedMethod
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.methodName.text = "Mean Variance Method"
            }
        }
    }
}
