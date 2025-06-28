package org.havrylenko.vrgsoftapp.ui.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.havrylenko.vrgsoftapp.R
import org.havrylenko.vrgsoftapp.databinding.FragmentDetailsBinding
import org.havrylenko.vrgsoftapp.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }
}