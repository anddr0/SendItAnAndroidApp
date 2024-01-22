package com.example.sendit.ViewModels

import Goal
import NewGoal
import NewSubGoal
import SubGoal
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sendit.HelperFunctions
import com.example.sendit.Repos.SupabaseRepo
import finishedSubGoal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import newFinishedSubGoal
import java.time.LocalDateTime

class AddGoalViewModel : ViewModel() {

    private val helpersFuncs = HelperFunctions()
    val sbRepo = SupabaseRepo()

    //-------------------------GOALS-------------------------
    suspend fun addNewGoal(scope: CoroutineScope, view: View, opisC: String, cykicznosc: Boolean, termin: LocalDateTime?): Int {
        val newGoal = NewGoal(helpersFuncs.getLoggedUserId(view), opisC, LocalDateTime.now(), cykicznosc, termin, false)
        return sbRepo.addNewGoal(newGoal)
    }
    fun updateGoal(scope: CoroutineScope, view: View, idC: Int, opisC: String, cykicznosc: Boolean, termin: LocalDateTime?): Deferred<Any> {
        val goal = Goal(idC, helpersFuncs.getLoggedUserId(view), opisC, LocalDateTime.now(), cykicznosc, termin, false)
        return sbRepo.updateUserGoal(scope, goal)
    }
    suspend fun getGoalById(idC: Int): Goal {
        return sbRepo.getGoalByGoalId(idC)
    }

    //-------------------------SUB_GOALS-------------------------

    private val subGoalsLiveData = MutableLiveData<List<SubGoal>>()

    fun getUserSubGoals(scope: CoroutineScope, idC: Int) {
        scope.launch {
            val subGoals = sbRepo.getSubGoalsByIdC(idC)
            subGoalsLiveData.postValue(subGoals)
        }
    }

    fun getSubGoalsLiveData(): LiveData<List<SubGoal>> {
        return subGoalsLiveData
    }

    suspend fun addNewSubGoals(scope: CoroutineScope, subGoals: MutableList<SubGoal>, idC: Int) {
        for (subGoal in subGoals) {
            if (!subGoal.opisPC.isNullOrBlank()) {
                    val getIdP = sbRepo.upsertNewSubGoal(scope, NewSubGoal(idC, subGoal.opisPC, subGoal.zrealizowany), subGoal.idP)
                    if (subGoal.zrealizowany) {
                        sbRepo.upsertFinishedSubGoal(scope, newFinishedSubGoal(getIdP, LocalDateTime.now()))
                        Log.d("---Czy zrealizowany---", "$getIdP")
                    }
            }
        }
    }
    fun subGoalDelete(scope: CoroutineScope, idP: Int) {
        sbRepo.subGoalDelete(scope, idP)
    }

}