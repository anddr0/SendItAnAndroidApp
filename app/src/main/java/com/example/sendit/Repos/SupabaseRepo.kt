package com.example.sendit.Repos

import Goal
import NewGoal
import NewRealizedGoal
import NewSubGoal
import NewUser
import RealizedGoal
import SubGoal
import User
import android.util.Log
import finishedSubGoal
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import newFinishedSubGoal
import java.time.LocalDateTime

class SupabaseRepo {
    private var client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://bagmctvkcdodloqglsfr.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJhZ21jdHZrY2RvZGxvcWdsc2ZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTkyMTAyNTQsImV4cCI6MjAxNDc4NjI1NH0.aoyZrHdlYJ2Du9nuut94BsmAacjFIJ0xJD-3h0Q35v0")
    {
        defaultSerializer = KotlinXSerializer()
        install(Postgrest)
    }

    //-------------------------USERS-------------------------

    fun signIn(scope: CoroutineScope, email: String, pass: String, callback: (Boolean, Int) -> Unit) {
        scope.launch {
            val response = client.from("Uzytkownik")
                .select() { filter { eq("email", email) } }
                .decodeList<User>()
            val exists = response.any {it.email == email && it.password == pass}
            callback(exists, if (exists) response[0].idU else -1)
        }
    }

    fun signUp(scope: CoroutineScope, username: String, birthDate: LocalDateTime,
               email: String, pass: String, description: String, callback: (Int) -> Unit) {
        scope.launch {
            val ins = client.from("Uzytkownik").insert(
                NewUser(username, birthDate, email, pass, description))
            val response = client.from("Uzytkownik").select(Columns.ALL) {filter { eq("email", email) }}.decodeList<User>()[0]
            callback(response.idU)
        }
    }

    fun insertUser(scope: CoroutineScope, user: NewUser) {
        scope.launch {
            client.from("Uzytkownik").insert(user)
        }
    }

    fun getUsers(scope: CoroutineScope) {
        scope.launch {
            val response = client.from("Uzytkownik").select().decodeList<User>()
        }
    }
    fun getUserById(scope: CoroutineScope, idU: Int, callback: (User) -> Unit) {
        scope.launch {
            val response = client.from("Uzytkownik").select(Columns.ALL) {filter { eq("idU", idU) }}.decodeSingle<User>()
            callback(response)
        }
    }

    fun getIdByEmail(scope: CoroutineScope, email: String, callback: (Int) -> Unit) {
        scope.launch {
            val response =
                client.from("Uzytkownik").select(Columns.ALL) { filter { eq("email", email) } }
                    .decodeList<User>()[0]
            callback(response.idU)
        }
    }

    fun ifUserExists(scope: CoroutineScope, email: String, callback: (Boolean) -> Unit) {
        scope.launch {
            val response = client.from("Uzytkownik")
                .select() { filter { eq("email", email) } }
                .decodeList<User>()
            val exists = response.any { it.email == email }
            callback(exists)
        }
    }

    //-------------------------GOALS-------------------------

    fun getGoalsByUserId(scope: CoroutineScope, idU: Int, callback: (List<Goal>) -> Unit) {
        scope.launch { callback(client.from("Cele").select() {
            order(column = "dataUt", order = Order.ASCENDING)
            filter {
                eq("idU", idU)
                eq("zrealizowany", false)
        }}.decodeList<Goal>()) }
    }
    suspend fun getGoalByGoalId(idC: Int): Goal {
        return client.from("Cele").select() {filter { eq("idC", idC) }}.decodeSingle<Goal>()
    }
    suspend fun addNewGoal(newGoal: NewGoal): Int {
        val response = client.from("Cele").insert(newGoal) { select() }.decodeSingle<Goal>()
        return response.idC
    }
    fun updateUserGoal(scope: CoroutineScope, goal: Goal): Deferred<Any> {
        return scope.async { client.from("Cele").update(goal) {filter { eq("idC", goal.idC) }} }
    }
    fun deleteUserGoal(scope: CoroutineScope, idC:Int) {
        scope.launch { client.from("Cele").delete() {filter { eq("idC", idC) }} }
    }
    //-------------------------FINISHED GOALS-------------------------

    fun getFinishedGoalsByUserId(scope: CoroutineScope, idU: Int, callback: (List<Goal>) -> Unit) {
        scope.launch { callback(client.from("Cele").select() {
            order(column = "dataUt", order = Order.ASCENDING)
            filter {
                eq("idU", idU)
                eq("zrealizowany", true)
        }}.decodeList<Goal>()) }
    }
    fun addFinishedGoal(scope: CoroutineScope, finishedGoal: NewRealizedGoal) {
        scope.launch { client.from("ZrealizowaneCele").insert(finishedGoal) }
    }
    fun deleteFinishedGoalByidC(scope: CoroutineScope, idC: Int) {
        scope.launch { client.from("ZrealizowaneCele").delete()
        {filter { eq("idC", idC) }} }
    }
    //-------------------------SUB_GOALS-------------------------
    suspend fun upsertNewSubGoal(scope: CoroutineScope, newSubGoal: NewSubGoal, idP: Int?): Int {
        var getIdP: Int
        if (idP != null) {
            getIdP = client.from("Podcele").update(newSubGoal) {
                select()
                filter{ eq("idP", idP)}
            }.decodeSingle<SubGoal>().idP!!
        } else {
            getIdP = client.from("Podcele").insert(newSubGoal) { select() }.decodeSingle<SubGoal>().idP!!
        }
        return getIdP
    }
    fun subGoalDelete(scope: CoroutineScope, idP: Int) {
        scope.launch { client.from("Podcele").delete() {filter { eq("idP", idP) }} }
    }
    suspend fun getSubGoalsByIdC(idC: Int): MutableList<SubGoal> {
        return client.from("Podcele").select() {filter { eq("idC", idC) }}.decodeList<SubGoal>().toMutableList()
    }

    //-------------------------FINISHED_GOALS-------------------------
    suspend fun upsertFinishedSubGoal(scope: CoroutineScope, newFinishedSubgoal: newFinishedSubGoal) {
        val fSubGoal = client.from("ZrealizowanePodcele").select()
        { filter { eq("idP", newFinishedSubgoal.idP) } }.decodeSingleOrNull<finishedSubGoal>()
        Log.d("___ja zaibavsa___", fSubGoal.toString())

        if (fSubGoal != null) {
            client.from("ZrealizowanePodcele").update(newFinishedSubgoal) {filter { eq("idP", fSubGoal.idP) }}
            Log.d("---Czy update---", "Update $fSubGoal")
        }
        else {
            Log.d("---Czy insert---", "Insert")
            client.from("ZrealizowanePodcele").insert(newFinishedSubgoal)
        }
    }
}