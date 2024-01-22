package com.example.sendit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sendit.Adapters.GoalsAdapter
import com.example.sendit.HelperFunctions
import com.example.sendit.R
import com.example.sendit.ViewModels.HomeViewModel
import com.example.sendit.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val helpersFuncs = HelperFunctions()

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: GoalsAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        adapter = GoalsAdapter(lifecycleScope, homeViewModel, mutableListOf())

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        homeViewModel.getGoalsLiveData().observe(viewLifecycleOwner, Observer { goals ->
            if (goals.isNotEmpty()) {
                adapter.data = goals
                adapter.notifyDataSetChanged()
//                Toast.makeText(requireContext(), "Goals loaded", Toast.LENGTH_SHORT).show()
            }
        })
        fab.setOnClickListener {
            view.findNavController().navigate(R.id.action_nav_home_to_addGoalFragment)
        }
    }


    override fun onResume() {
        super.onResume()
        homeViewModel.getUserGoals(lifecycleScope, helpersFuncs.getLoggedUserId(requireView()))
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}