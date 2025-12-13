package com.example.project_gestoderisco.dashboard.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_gestoderisco.dashboard.DashboardViewModel
import com.example.project_gestoderisco.dashboard.RankingAdapter
import com.example.project_gestoderisco.databinding.FragmentDashboardRankingBinding

class DashboardRankingFragment : Fragment() {

    private var _binding: FragmentDashboardRankingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var rankingAdapter: RankingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.ranking.observe(viewLifecycleOwner) {
            rankingAdapter.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        rankingAdapter = RankingAdapter()
        binding.rvRanking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rankingAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}