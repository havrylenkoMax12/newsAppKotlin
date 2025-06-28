package org.havrylenko.vrgsoftapp.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.FragmentCategoryBinding
import org.havrylenko.vrgsoftapp.databinding.FragmentDetailsBinding

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }
}