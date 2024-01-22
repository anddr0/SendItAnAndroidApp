package com.example.sendit.Adapters

import Goal
import NewSubGoal
import SubGoal
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.sendit.R
import com.example.sendit.ViewModels.AddGoalViewModel
import com.example.sendit.databinding.UnderGoalRawBinding

class UnderGoalAdapter(private val lifecycleScope: LifecycleCoroutineScope, private val viewModel: AddGoalViewModel,
                       var subGoals: MutableList<SubGoal>):
    RecyclerView.Adapter<UnderGoalAdapter.UnderGoalViewHolder>() {

        inner class UnderGoalViewHolder(binding: UnderGoalRawBinding) : RecyclerView.ViewHolder(binding.root) {
            val chekBox = binding.cbDone
            val etSubGoal = binding.etSubGoal
            val ibtDelete = binding.ibtDelete
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnderGoalViewHolder {
        val binding = UnderGoalRawBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UnderGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnderGoalViewHolder, position: Int) {
        val currentItem = subGoals[position]

        holder.etSubGoal.setText(currentItem.opisPC)
        updateItemState(holder, currentItem.zrealizowany)

        holder.chekBox.setOnClickListener {
            val isChecked = holder.chekBox.isChecked
            currentItem.zrealizowany = isChecked
            updateItemState(holder, isChecked)
        }

        (holder.etSubGoal.tag as? TextWatcher)?.let {
            holder.etSubGoal.removeTextChangedListener(it)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentItem.opisPC = s.toString()
            }
        }
        holder.etSubGoal.addTextChangedListener(textWatcher)
        holder.etSubGoal.tag = textWatcher

        holder.ibtDelete.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val itemToRemove = subGoals[currentPosition]
                subGoals.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
                notifyItemRangeChanged(currentPosition, subGoals.size)

                if (itemToRemove.idP != null) {
                    viewModel.subGoalDelete(lifecycleScope, itemToRemove.idP!!)
                    viewModel.getUserSubGoals(lifecycleScope, itemToRemove.idC!!)
                }
            }
        }
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

    override fun onViewRecycled(holder: UnderGoalViewHolder) {
        super.onViewRecycled(holder)
        (holder.etSubGoal.tag as? TextWatcher)?.let {
            holder.etSubGoal.removeTextChangedListener(it)
        }
    }
}