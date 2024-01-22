package com.example.sendit.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sendit.Adapters.GoalsAdapter
import com.example.sendit.Adapters.RealizedGoalsAdapter
import com.example.sendit.HelperFunctions
import com.example.sendit.R
import com.example.sendit.ViewModels.HomeViewModel
import com.example.sendit.databinding.FragmentGalleryBinding
import com.example.sendit.databinding.FragmentHomeBinding

class GalleryFragment : Fragment() {
    private lateinit var viewModel: GalleryViewModel
    private lateinit var adapter: RealizedGoalsAdapter

    private val helpersFuncs = HelperFunctions()


    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        adapter = RealizedGoalsAdapter(lifecycleScope, viewModel, mutableListOf())
        val recyclerView = view.findViewById<RecyclerView>(R.id.RecyclerView_gallery)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.getGoalsLiveData().observe(viewLifecycleOwner, Observer { goals ->
            if (goals.isNotEmpty()) {
                adapter.data = goals
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserGoals(lifecycleScope, helpersFuncs.getLoggedUserId(requireView()))
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}