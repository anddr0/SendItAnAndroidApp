package com.example.sendit.ui.gallery

import Goal
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sendit.Repos.SupabaseRepo
import kotlinx.coroutines.CoroutineScope

class GalleryViewModel : ViewModel() {

    val sbRepo = SupabaseRepo()
    private val goalsLiveData = MutableLiveData<List<Goal>>()

    //--------------------------Goals--------------------------
    fun getUserGoals(scope: CoroutineScope, idU: Int) {
        sbRepo.getFinishedGoalsByUserId(scope, idU) { goals ->
            goalsLiveData.postValue(goals)
        }
    }
    fun getGoalsLiveData(): LiveData<List<Goal>> {
        return goalsLiveData
    }
    fun updateGoalZrealiz(scope: CoroutineScope, goal: Goal) {
        sbRepo.updateUserGoal(scope, goal)
        sbRepo.deleteFinishedGoalByidC(scope, goal.idC)
    }
}