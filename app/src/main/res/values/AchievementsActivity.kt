package com.example.project_gestoderisco.ui.gamification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_gestoderisco.data.repository.GamificationRepository
import com.example.project_gestoderisco.databinding.ActivityAchievementsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AchievementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    
    @Inject
    lateinit var repository: GamificationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        val badges = repository.getAllBadges()
        val adapter = BadgesAdapter(badges)
        
        binding.rvBadges.layoutManager = LinearLayoutManager(this)
        binding.rvBadges.adapter = adapter
    }
}