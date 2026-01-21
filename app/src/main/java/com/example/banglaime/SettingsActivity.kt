package com.example.banglaime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 50, 50, 50)
        
        val btnEnable = Button(this)
        btnEnable.text = "1. Enable Keyboard"
        btnEnable.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        
        val btnSelect = Button(this)
        btnSelect.text = "2. Select Keyboard"
        btnSelect.setOnClickListener {
            val imeManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.showInputMethodPicker()
        }
        
        layout.addView(btnEnable)
        layout.addView(btnSelect)
        setContentView(layout)
    }
}