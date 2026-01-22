package com.example.gestaoderisco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestaoderisco.view.DashboardFragment
import com.example.gestaoderisco.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, DashboardFragment())
                .commitNow()
        }
    }
}