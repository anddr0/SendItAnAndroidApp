package com.example.sendit.ui

import Goal
import SubGoal
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sendit.Adapters.DescriptionSubGoalsAdapter
import com.example.sendit.Adapters.UnderGoalAdapter
import com.example.sendit.R
import com.example.sendit.ViewModels.DescriptionGoalViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DescriptionGoalFragment : Fragment() {
    private lateinit var viewModel: DescriptionGoalViewModel
    private lateinit var adapter: DescriptionSubGoalsAdapter
    private var idC: Int? = null
    private var currentGoal: Goal? = null
    private var subGoals = mutableListOf<SubGoal>()

    private lateinit var tvDescrGoal: TextView
    private lateinit var etGoal: EditText
    private lateinit var divider: View
    private lateinit var linearLayout: LinearLayout
    private lateinit var btDate: ImageButton
    private lateinit var etDate: EditText
    private lateinit var divider3: View
    private lateinit var imageView2: ImageView
    private lateinit var textView3: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var etAnalize: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idC = it.getInt("idC")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(DescriptionGoalViewModel::class.java)
        adapter = DescriptionSubGoalsAdapter(lifecycleScope, viewModel, mutableListOf())

        return inflater.inflate(R.layout.fragment_description_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        tvDescrGoal = view.findViewById(R.id.tv_descr_goal)
        etGoal = view.findViewById(R.id.et_goal)
        divider = view.findViewById(R.id.divider)
        linearLayout = view.findViewById(R.id.linearLayout)
        btDate = view.findViewById(R.id.bt_date)
        etDate = view.findViewById(R.id.et_date)
        divider3 = view.findViewById(R.id.divider3)
        imageView2 = view.findViewById(R.id.imageView2)
        textView3 = view.findViewById(R.id.textView3)
        recyclerView = view.findViewById(R.id.RecyclerView)
        etAnalize = view.findViewById(R.id.et_analize)



        viewModel.getSubGoalsLiveData().observe(viewLifecycleOwner, Observer { subGoals ->
            this.subGoals.clear()
            this.subGoals.addAll(subGoals)
            adapter.subGoals = subGoals.toMutableList()
            adapter.notifyDataSetChanged()
        })
        lifecycleScope.launch {
            try {
                currentGoal = viewModel.getGoalById(idC!!)
                viewModel.getUserSubGoals(lifecycleScope, idC!!)
            } catch (_: Exception) {
                Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_SHORT).show()
            }
            currentGoal.let {
                etGoal.setText(it!!.opisC)
                if (it.termin != null) {
                    val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
                    val formattedDate = it.termin.format(formatter)
                    etDate.setText(formattedDate)
                }
                textView3.text = if (it.cyklicznosc) getString(R.string.repeatable) else getString(R.string.not_repeatable)
            }
            val prompt = "${getString(R.string.main_prompt)} ${getString(R.string.main_prompt2)} ${currentGoal!!.opisC} deadline is: ${currentGoal!!.termin} cyclicality: ${currentGoal!!.cyklicznosc}"
            viewModel.sendGptRequest(prompt, "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") { response ->
                etAnalize.setText(response)
            }
        }

    }

}