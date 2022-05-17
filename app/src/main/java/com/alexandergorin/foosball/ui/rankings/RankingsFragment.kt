package com.alexandergorin.foosball.ui.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexandergorin.foosball.R
import com.alexandergorin.foosball.core.base.BaseFragment
import com.alexandergorin.foosball.databinding.RankingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingsFragment : BaseFragment<RankingsFragmentBinding>() {

    private val viewModel: RankingsViewModel by viewModels()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RankingsFragmentBinding {
        return RankingsFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = requireViewBinding()
        requireActivity().title = getString(R.string.rankings)


        binding.matchesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.loadRankings()

        viewModel.rankingsViewState.observe(viewLifecycleOwner, ::renderState)
    }

    private fun renderState(viewState: RankingsViewState) {
        val binding = requireViewBinding()
        when (viewState) {
            is RankingsViewState.Error -> {
                binding.progressBar.isVisible = false
                binding.matchesRecyclerView.isVisible = false
            }
            is RankingsViewState.Loaded -> {
                binding.progressBar.isVisible = false
                binding.matchesRecyclerView.isVisible = true
                binding.matchesRecyclerView.adapter =
                    RankingsRecyclerViewAdapter(viewState.rankings)
            }
            RankingsViewState.Loading -> {
                binding.progressBar.isVisible = true
                binding.matchesRecyclerView.isVisible = false
            }
        }
    }
}