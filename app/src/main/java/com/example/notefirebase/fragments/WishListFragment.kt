package com.example.notefirebase.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentWishListBinding

class WishListFragment : Fragment() {

    private lateinit var binding: FragmentWishListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishListBinding.inflate(inflater, container, false)
        showWishes()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateWish.setOnClickListener {
            val createWish = CreateWishFragment(this)
            createWish.show(
                requireActivity().supportFragmentManager,
                "CreateWishFragment"
            )
        }

        binding.back.setOnClickListener{
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, MainFragment())
                ?.commit()
        }
    }

    fun showWishes() {

        binding.rcWishList.layoutManager = LinearLayoutManager(requireContext())

    }

    companion object {
        fun newInstance() = WishListFragment()
    }
}