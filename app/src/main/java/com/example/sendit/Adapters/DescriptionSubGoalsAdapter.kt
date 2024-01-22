package com.example.sendit.Adapters

import SubGoal
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.sendit.ViewModels.DescriptionGoalViewModel
import com.example.sendit.databinding.DescrUnderGoalRawBinding

class DescriptionSubGoalsAdapter(private val lifecycleScope: LifecycleCoroutineScope, private val viewModel: DescriptionGoalViewModel,
                                 var subGoals: MutableList<SubGoal>):
    RecyclerView.Adapter<DescriptionSubGoalsAdapter.UnderGoalViewHolder>() {

    inner class UnderGoalViewHolder(binding: DescrUnderGoalRawBinding) : RecyclerView.ViewHolder(binding.root) {
        val chekBox = binding.cbDone
        val etSubGoal = binding.etSubGoal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionSubGoalsAdapter.UnderGoalViewHolder {
        val binding = DescrUnderGoalRawBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UnderGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DescriptionSubGoalsAdapter.UnderGoalViewHolder, position: Int) {
        val currentItem = subGoals[position]

        holder.etSubGoal.setText(currentItem.opisPC)
        updateItemState(holder, currentItem.zrealizowany)

    }

    private fun updateItemState(holder: UnderGoalViewHolder, isChecked: Boolean) {
        holder.chekBox.isChecked = isChecked
        if (isChecked) {
            holder.etSubGoal.paintFlags = holder.etSubGoal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.etSubGoal.setTextColor(Color.GRAY)
        } else {
            holder.etSubGoal.paintFlags = holder.etSubGoal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.etSubGoal.setTextColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return subGoals.size
    }

}