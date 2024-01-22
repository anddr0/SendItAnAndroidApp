package com.example.sendit.ViewModels

import Goal
import NewRealizedGoal
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sendit.Repos.SupabaseRepo
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {

    val sbRepo = SupabaseRepo()
    private val goalsLiveData = MutableLiveData<List<Goal>>()

    //--------------------------Goals--------------------------
    fun getUserGoals(scope: CoroutineScope, idU: Int) {
        sbRepo.getGoalsByUserId(scope, idU) { goals ->
            goalsLiveData.postValue(goals)
        }
    }
    fun getGoalsLiveData(): LiveData<List<Goal>> {
        return goalsLiveData
    }
    fun updateGoalZrealiz(scope: CoroutineScope, goal: Goal) {
        sbRepo.updateUserGoal(scope, goal)
        sbRepo.addFinishedGoal(scope, NewRealizedGoal(goal.idC, LocalDateTime.now(), ""))
    }

    fun deleteGoal(scope: CoroutineScope, idC: Int) { sbRepo.deleteUserGoal(scope, idC) }

    //--------------------------Add to --------------------------
}