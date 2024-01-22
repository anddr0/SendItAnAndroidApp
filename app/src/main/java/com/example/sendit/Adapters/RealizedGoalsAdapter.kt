package com.example.sendit.Adapters

import Goal
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.sendit.HelperFunctions
import com.example.sendit.R
import com.example.sendit.databinding.FinishedGoalRawBinding
import com.example.sendit.ui.gallery.GalleryViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RealizedGoalsAdapter(private val lifecycleScope: LifecycleCoroutineScope, private val viewModel: GalleryViewModel, var data: List<Goal>):
    RecyclerView.Adapter<RealizedGoalsAdapter.MyViewHolder>() {

    private val helpersFuncs = HelperFunctions()

    inner class MyViewHolder(binding: FinishedGoalRawBinding) : RecyclerView.ViewHolder(binding.root) {
        val chekBox = binding.checkBox
        val tvGoal = binding.tvGoal
        val tvDate = binding.tvDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealizedGoalsAdapter.MyViewHolder {
        val binding = FinishedGoalRawBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = data[position]

        holder.chekBox.isChecked = currentItem.zrealizowany
        holder.tvGoal.text = currentItem.opisC
//        holder.tvGoal.paintFlags = holder.tvGoal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
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

        holder.chekBox.setOnClickListener {
            currentItem.zrealizowany = holder.chekBox.isChecked
            viewModel.updateGoalZrealiz(lifecycleScope, currentItem)
            viewModel.getUserGoals(lifecycleScope, helpersFuncs.getLoggedUserId(holder.itemView))
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
}