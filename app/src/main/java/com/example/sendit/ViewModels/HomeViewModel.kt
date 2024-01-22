package com.example.sendit.ViewModels

import Goal
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sendit.Repos.SupabaseRepo
import kotlinx.coroutines.CoroutineScope

class HomeViewModel : ViewModel() {

    val sbRepo = SupabaseRepo()
    private val goalsLiveData = MutableLiveData<List<Goal>>()

    //--------------------------Get Goals--------------------------
    fun getUserGoals(scope: CoroutineScope, idU: Int) {
        sbRepo.getGoalsByUserId(scope, idU) { goals ->
            goalsLiveData.postValue(goals)
        }
    }
    fun getGoalsLiveData(): LiveData<List<Goal>> {
        return goalsLiveData
    }

    //--------------------------Delete Goals--------------------------
    fun deleteGoal(scope: CoroutineScope, idC: Int) { sbRepo.deleteUserGoal(scope, idC) }
}