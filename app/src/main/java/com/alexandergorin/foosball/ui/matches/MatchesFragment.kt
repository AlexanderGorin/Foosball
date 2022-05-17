package com.alexandergorin.foosball.ui.matches

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexandergorin.foosball.R
import com.alexandergorin.foosball.core.base.BaseFragment
import com.alexandergorin.foosball.databinding.MatchesFragmentBinding
import com.alexandergorin.foosball.ui.edit.EditMatchFragmentType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesFragment : BaseFragment<MatchesFragmentBinding>() {

    private val viewModel: MatchesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MatchesFragmentBinding {
        return MatchesFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = requireViewBinding()
        val navController = findNavController()
        requireActivity().title = getString(R.string.app_name)

        binding.matchesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.addMatchButton.setOnClickListener {
            navController.navigate(
                MatchesFragmentDirections.actionNavigationMatchesToEditMatchFragment(
                    EditMatchFragmentType.Add
                )
            )
        }

        viewModel.loadMatches()

        viewModel.matchesViewState.observe(viewLifecycleOwner, ::renderState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.rankings_item -> {
                findNavController().navigate(R.id.action_navigation_matches_to_rankingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderState(viewState: MatchesViewState) {
        val binding = requireViewBinding()
        val navController = findNavController()
        when (viewState) {
            is MatchesViewState.Error -> {
                binding.progressBar.isVisible = false
                binding.matchesRecyclerView.isVisible = false
            }
            is MatchesViewState.Loaded -> {
                binding.progressBar.isVisible = false
                binding.matchesRecyclerView.isVisible = true
                binding.matchesRecyclerView.adapter =
                    MatchesRecyclerViewAdapter(viewState.matches) { matchId ->
                        navController.navigate(
                            MatchesFragmentDirections.actionNavigationMatchesToEditMatchFragment(
                                EditMatchFragmentType.Edit(matchId)
                            )
                        )
                    }
            }
            MatchesViewState.Loading -> {
                binding.progressBar.isVisible = true
                binding.matchesRecyclerView.isVisible = false
            }
        }
    }

}