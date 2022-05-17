package com.alexandergorin.foosball.ui.edit

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alexandergorin.foosball.R
import com.alexandergorin.foosball.core.base.BaseFragment
import com.alexandergorin.foosball.databinding.EditMatchFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMatchFragment : BaseFragment<EditMatchFragmentBinding>() {

    private val args: EditMatchFragmentArgs by navArgs()
    private val viewModel: EditMatchViewModel by viewModels()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EditMatchFragmentBinding {
        return EditMatchFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = requireViewBinding()
        val navController = findNavController()

        val type = args.type

        val title = when (type) {
            EditMatchFragmentType.Add -> getString(R.string.add_your_match)
            is EditMatchFragmentType.Edit -> getString(R.string.edit_your_match)
        }
        requireActivity().title = title

        if (type is EditMatchFragmentType.Edit) {
            viewModel.loadMatch(type.matchId)
        }

        viewModel.matchViewState.observe(viewLifecycleOwner) { match ->
            binding.firstPersonNameEditText.setText(match.firstPersonName)
            binding.firstScoreEditText.setText(match.firstPersonScore)
            binding.secondPersonNameEditText.setText(match.secondPersonName)
            binding.secondScoreEditText.setText(match.secondPersonScore)
        }

        viewModel.saveButtonEnabledState.observe(viewLifecycleOwner) { isEnabled ->
            binding.saveButton.isEnabled = isEnabled
        }

        viewModel.navigateBackEvent.observe(viewLifecycleOwner) {
            navController.navigateUp()
        }

        binding.saveButton.setOnClickListener {
            when (type) {
                EditMatchFragmentType.Add -> viewModel.addMatch()
                is EditMatchFragmentType.Edit -> viewModel.saveMatch(type.matchId)
            }
        }

        binding.firstPersonNameEditText.doAfterTextChanged {
            viewModel.onFirstPersonNameChange(it.toString())
        }
        binding.firstScoreEditText.doAfterTextChanged {
            viewModel.onFirstPersonScoreChange(it.toString())
        }
        binding.secondPersonNameEditText.doAfterTextChanged {
            viewModel.onSecondPersonNameChange(it.toString())
        }
        binding.secondScoreEditText.doAfterTextChanged {
            viewModel.onSecondPersonScoreChange(it.toString())
        }
    }

}
