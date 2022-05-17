package com.alexandergorin.foosball.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VIEW_BINDING : ViewBinding> : Fragment() {

    protected open var viewBinding: VIEW_BINDING? = null

    protected fun requireViewBinding(): VIEW_BINDING = requireNotNull(viewBinding)

    protected abstract fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VIEW_BINDING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createViewBinding(inflater, container).also {
            viewBinding = it
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}