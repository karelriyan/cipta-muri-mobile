package com.main.cipta_muri_mobile.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.databinding.FragmentLeaderboardWeightBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeaderboardWeightFragment : Fragment() {

    private var _binding: FragmentLeaderboardWeightBinding? = null
    private val binding get() = _binding!!
    private val vm: RankingViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaderboardWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())

        // load once; default limit 10, last 3 months handled server-side if not provided
        vm.load(limit = 10)

        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { state ->
                // map API TopBeratItem to UI model
                val mapped = state.berat.mapIndexed { idx, item ->
                    val pts = item.total_berat.toDoubleOrNull()?.let { it.toInt() } ?: 0
                    LeaderboardItem((idx + 1).toString(), item.nama ?: "-", item.no_rekening ?: "-", pts)
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
