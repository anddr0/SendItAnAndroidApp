package com.example.sendit.ui.home


import Goal
import NewSubGoal
import SubGoal
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.sendit.HelperFunctions
import com.example.sendit.R
import com.example.sendit.Repos.SupabaseRepo
import com.example.sendit.ViewModels.AddGoalViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sendit.Adapters.UnderGoalAdapter
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


class AddGoalFragment : Fragment() {
    private var idC: Int? = null
    private var currentGoal: Goal? = null
    private var editMode: Boolean = false

    private lateinit var adapter: UnderGoalAdapter
    private lateinit var viewModel: AddGoalViewModel
    private val helpersFuncs = HelperFunctions()
    val sbRepo = SupabaseRepo()

    private lateinit var tvDescrGoal: TextView
    private lateinit var etGoal: EditText
    private lateinit var tvRepeatable: TextView
    private lateinit var swRepeat: Switch
    private lateinit var tvDeadline: TextView
    private lateinit var etDate: EditText
    private lateinit var btDate: ImageButton
    private lateinit var fabDone: FloatingActionButton
    private lateinit var btAddSubTask: Button

    private var deadline: LocalDateTime? = null
    private var oldSubGoals = mutableListOf<SubGoal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idC = it.getInt("idC")
        }
        if (idC != null) {
            editMode = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[AddGoalViewModel::class.java]
        adapter = UnderGoalAdapter(lifecycleScope, viewModel, mutableListOf())

        return inflater.inflate(R.layout.fragment_add_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        tvDescrGoal = view.findViewById(R.id.tv_descr_goal)
        etGoal = view.findViewById(R.id.et_goal)
        swRepeat = view.findViewById(R.id.sw_repeat)
        etDate = view.findViewById(R.id.et_date)
        btDate = view.findViewById(R.id.bt_date)
        fabDone = view.findViewById(R.id.fab_done)
        btAddSubTask = view.findViewById(R.id.button_add_subtask)

        //-----------------------DateTimePick-----------------------
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                deadline = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)

                val selectedDateTime = String.format("%02d-%02d-%04d %02d:%02d", dayOfMonth, month + 1, year, hourOfDay, minute)
                etDate.setText(selectedDateTime)
            }

            val timePickerDialog = TimePickerDialog(requireContext(), timePickerListener, 23, 59, true)
            timePickerDialog.show()
        }

        fun openDatePicker() {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        //-----------------------Listeners----------------------
        btDate.setOnClickListener { openDatePicker() }
        etDate.setOnClickListener { openDatePicker() }

        btAddSubTask.setOnClickListener {
            val newSubGoal = if (editMode) SubGoal(null, currentGoal!!.idC, null, false)
            else SubGoal(null, null, null, false)
            oldSubGoals.add(newSubGoal)
            adapter.subGoals = oldSubGoals
            adapter.notifyDataSetChanged()
        }

        fabDone.setOnClickListener {

            if (helpersFuncs.trimStr(etGoal).isBlank()) {
                Snackbar.make(view, getString(R.string.descr_missing), Snackbar.LENGTH_SHORT).show()
            }
            else {
                lifecycleScope.launch {
                    if (editMode) {
                        if (oldSubGoals.isNotEmpty()) {
                            viewModel.addNewSubGoals(lifecycleScope, adapter.subGoals, currentGoal!!.idC)
                        }
                        viewModel.updateGoal(lifecycleScope, view, currentGoal!!.idC,
                            helpersFuncs.trimStr(etGoal), swRepeat.isChecked, deadline)
                        view.findNavController().popBackStack()
                    } else {
                        if (oldSubGoals.isNotEmpty()) {
                            idC = viewModel.addNewGoal(lifecycleScope, view,
                                helpersFuncs.trimStr(etGoal), swRepeat.isChecked, deadline)

                            viewModel.addNewSubGoals(lifecycleScope, adapter.subGoals, idC!!)
                        }
                        else {
                            viewModel.addNewGoal(lifecycleScope, view, helpersFuncs.trimStr(etGoal), swRepeat.isChecked, deadline)
                        }
                        view.findNavController().popBackStack()
                    }
                }
            }
        }

        if (editMode) {
            //-----------------------Editing some goal----------------------
            viewModel.getSubGoalsLiveData().observe(viewLifecycleOwner, Observer { subGoals ->
                this.oldSubGoals.clear()
                this.oldSubGoals.addAll(subGoals)
                adapter.subGoals = oldSubGoals
                adapter.notifyDataSetChanged()
            })
            viewModel.getUserSubGoals(lifecycleScope, idC!!)
            lifecycleScope.launch {
                try {
                    currentGoal = viewModel.getGoalById(idC!!)
                    viewModel.getUserSubGoals(lifecycleScope, idC!!)
                } catch (_: Exception) { }
                currentGoal?.let { goal ->
                    etGoal.setText(goal.opisC)
                    swRepeat.isChecked = goal.cyklicznosc
                    deadline = goal.termin
                    deadline?.let {
                        etDate.setText(formatLocalDateTime(it))
                    }
                }
            }

        }
    }

    fun formatLocalDateTime(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return localDateTime.format(formatter)
    }
//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            AddGoalFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}
