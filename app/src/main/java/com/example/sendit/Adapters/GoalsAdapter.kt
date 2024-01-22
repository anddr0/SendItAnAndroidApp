package com.example.sendit.Adapters

import Goal
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.sendit.ViewModels.HomeViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.sendit.HelperFunctions
import com.example.sendit.R
import com.example.sendit.databinding.GoalRawBinding
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.io.Serializable
import java.time.format.DateTimeFormatter

class GoalsAdapter(private val lifecycleScope: LifecycleCoroutineScope, private val viewModel: HomeViewModel, var data: List<Goal>):
    RecyclerView.Adapter<GoalsAdapter.MyViewHolder>() {

    private val helpersFuncs = HelperFunctions()


    inner class MyViewHolder(binding: GoalRawBinding) : RecyclerView.ViewHolder(binding.root) {
        val chekBox = binding.checkBox
        val tvGoal = binding.tvGoal
        val tvDate = binding.tvDate
        val ibtEdit = binding.ibtEdit
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = GoalRawBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = data[position]

        holder.chekBox.isChecked = currentItem.cyklicznosc
        holder.tvGoal.text = currentItem.opisC
        if (currentItem.termin != null) {
            val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
            val formattedDate = currentItem.termin.format(formatter)
            holder.tvDate.text = formattedDate
            if (currentItem.termin.isAfter(LocalDateTime.now())) {
                holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.md_theme_light_primaryContainer))
            } else {
                holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.required_value))
            }
            helpersFuncs.changeTVHeight(holder.tvDate)
        }
        holder.ibtEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("idC", currentItem.idC)
            it.findNavController().navigate(
                R.id.action_nav_home_to_addGoalFragment, bundle)
        }
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete your goal?")
                .setPositiveButton("Yes") { dialog, which ->
                    viewModel.deleteGoal(lifecycleScope, currentItem.idC)
                    viewModel.getUserGoals(lifecycleScope, helpersFuncs.getLoggedUserId(holder.itemView))
                    notifyDataSetChanged()
                    Snackbar.make(holder.itemView,
                        holder.itemView.context.getString(R.string.goal_deleted), Toast.LENGTH_SHORT)
                        .show()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
            true
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

}