package com.example.sendit.Repos

import NewUser
import User
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SupabaseRepo {
    private var client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://bagmctvkcdodloqglsfr.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJhZ21jdHZrY2RvZGxvcWdsc2ZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTkyMTAyNTQsImV4cCI6MjAxNDc4NjI1NH0.aoyZrHdlYJ2Du9nuut94BsmAacjFIJ0xJD-3h0Q35v0")
    {
        defaultSerializer = KotlinXSerializer()
        install(Postgrest)
    }

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
        }
        scope.launch{
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
            val response = client.from("Uzytkownik").select(Columns.ALL) {filter { eq("idU", idU) }}.decodeList<User>()[0]
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
}