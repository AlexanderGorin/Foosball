package com.alexandergorin.foosball.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alexandergorin.foosball.core.base.BaseFragment
import com.alexandergorin.foosball.databinding.EditMatchFragmentBinding
import com.google.android.material.snackbar.Snackbar
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

        viewModel.loadAppBarTitle(type)
        viewModel.loadMatch(type)

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

        viewModel.appBarTitle.observe(viewLifecycleOwner) { title ->
            requireActivity().title = title
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { message ->
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }

        binding.saveButton.setOnClickListener {
            viewModel.saveMatch(type)
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
