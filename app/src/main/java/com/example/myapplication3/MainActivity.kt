package com.example.myapplication3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSourceAmount: EditText
    private lateinit var editTextTargetAmount: EditText
    private lateinit var spinnerSourceCurrency: Spinner
    private lateinit var spinnerTargetCurrency: Spinner

    private var isSourceSelected = true // Xác định EditText nào là nguồn
    private var isUpdating = false // Cờ để tránh cập nhật chồng chéo

    private val currencyList = listOf("USD", "EUR", "JPY", "VND") // danh sách các đồng tiền

    // Tỷ giá cố định
    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "JPY" to 110.0,
        "VND" to 25000.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Liên kết UI với code
        editTextSourceAmount = findViewById(R.id.editTextSourceAmount)
        editTextTargetAmount = findViewById(R.id.editTextTargetAmount)
        spinnerSourceCurrency = findViewById(R.id.spinnerSourceCurrency)
        spinnerTargetCurrency = findViewById(R.id.spinnerTargetCurrency)

        // Cấu hình spinner với danh sách đồng tiền
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSourceCurrency.adapter = adapter
        spinnerTargetCurrency.adapter = adapter

        // Lắng nghe sự thay đổi trong EditText và Spinner
        setupListeners()
    }

    private fun setupListeners() {
        // Lắng nghe sự thay đổi của EditText nguồn
        editTextSourceAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isSourceSelected && !isUpdating) { // Chỉ cập nhật khi là nguồn và không đang cập nhật
                    updateConversion()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Lắng nghe sự thay đổi của EditText đích
        editTextTargetAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isSourceSelected && !isUpdating) { // Chỉ cập nhật khi là nguồn đích và không đang cập nhật
                    updateConversion()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Lắng nghe sự thay đổi của spinner nguồn
        spinnerSourceCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isSourceSelected && !isUpdating) {
                    updateConversion()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Lắng nghe sự thay đổi của spinner đích
        spinnerTargetCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isSourceSelected && !isUpdating) {
                    updateConversion()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Xử lý sự kiện khi người dùng chọn EditText nguồn
        editTextSourceAmount.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceSelected = true
                updateConversion()
            }
        }

        // Xử lý sự kiện khi người dùng chọn EditText đích
        editTextTargetAmount.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceSelected = false
                updateConversion()
            }
        }
    }

    private fun updateConversion() {
        // Đặt cờ isUpdating để tránh cập nhật chồng chéo
        isUpdating = true

        // Lấy số tiền từ EditText
        val sourceAmount = if (isSourceSelected) {
            editTextSourceAmount.text.toString().toDoubleOrNull() ?: 0.0
        } else {
            editTextTargetAmount.text.toString().toDoubleOrNull() ?: 0.0
        }

        // Lấy đồng tiền từ Spinner
        val sourceCurrency = if (isSourceSelected) {
            spinnerSourceCurrency.selectedItem.toString()
        } else {
            spinnerTargetCurrency.selectedItem.toString()
        }

        val targetCurrency = if (isSourceSelected) {
            spinnerTargetCurrency.selectedItem.toString()
        } else {
            spinnerSourceCurrency.selectedItem.toString()
        }

        // Lấy tỷ giá cho đồng tiền nguồn và đồng tiền đích
        val sourceRate = exchangeRates[sourceCurrency] ?: return
        val targetRate = exchangeRates[targetCurrency] ?: return

        // Tính toán số tiền sau khi chuyển đổi
        val targetAmount = if (isSourceSelected) {
            sourceAmount * (targetRate / sourceRate)
        } else {
            sourceAmount * (targetRate / sourceRate)
        }

        // Hiển thị kết quả vào EditText phù hợp
        if (isSourceSelected) {
            editTextTargetAmount.setText("%.2f".format(targetAmount))
        } else {
            editTextSourceAmount.setText("%.2f".format(targetAmount))
        }

        // Hủy cờ isUpdating sau khi cập nhật xong
        isUpdating = false
    }
}
