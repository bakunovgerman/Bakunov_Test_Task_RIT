package com.example.bakunov_test_task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText



class MainActivity : AppCompatActivity() {

    private lateinit var addressEditText: EditText
    private lateinit var watchNewsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    // метод инициализации view
    private fun initView() {
        addressEditText = findViewById(R.id.etAddress)
        watchNewsButton = findViewById(R.id.btnWatchNews)
    }

    // метод отправки адреса RSS
    fun watchNews(view: View) {
        if (!addressEditText.text.equals("")){
            val intent: Intent = Intent(this, NewsActivity::class.java)
            intent.putExtra("address", addressEditText.text.toString())
            startActivity(intent)
        }
    }
}