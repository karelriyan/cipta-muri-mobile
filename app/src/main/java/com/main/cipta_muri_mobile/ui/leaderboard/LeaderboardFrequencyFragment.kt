package com.main.cipta_muri_mobile.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.databinding.FragmentLeaderboardFrequencyBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeaderboardFrequencyFragment : Fragment() {

    private var _binding: FragmentLeaderboardFrequencyBinding? = null
    private val binding get() = _binding!!
    private val vm: RankingViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaderboardFrequencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())

        // load once; default limit 10
        vm.load(limit = 10)

        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { state ->
                val mapped = state.setor.mapIndexed { idx, item ->
                    LeaderboardItem((idx + 1).toString(), item.nama ?: "-", item.no_rekening ?: "-", item.total_setor)
                }

                val podium = mapped.take(3)
                if (podium.size >= 3) {
                    val first = podium[0]
                    val second = podium[1]
                    val third = podium[2]
                    binding.tvFirstName.text = first.name
                    binding.tvFirstPoints.text = first.points.toString()
                    binding.tvSecondName.text = second.name
                    binding.tvSecondPoints.text = second.points.toString()
                    binding.tvThirdName.text = third.name
                    binding.tvThirdPoints.text = third.points.toString()
                }

                val rest = if (mapped.size > 3) mapped.drop(3) else emptyList()
                binding.rvLeaderboard.adapter = LeaderboardAdapter(rest)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
