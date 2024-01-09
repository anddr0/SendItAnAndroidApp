package com.example.sendit.Repos

import EmailWrapper
import LoggedUserWrapper
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

class SupabaseRepo {
    private val supabaseUrl = "https://bagmctvkcdodloqglsfr.supabase.co"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJhZ21jdHZrY2RvZGxvcWdsc2ZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTkyMTAyNTQsImV4cCI6MjAxNDc4NjI1NH0.aoyZrHdlYJ2Du9nuut94BsmAacjFIJ0xJD-3h0Q35v0"
    private var client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://bagmctvkcdodloqglsfr.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJhZ21jdHZrY2RvZGxvcWdsc2ZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTkyMTAyNTQsImV4cCI6MjAxNDc4NjI1NH0.aoyZrHdlYJ2Du9nuut94BsmAacjFIJ0xJD-3h0Q35v0")
    {
        defaultSerializer = KotlinXSerializer()
        install(Postgrest)
    }

    fun getUsers(scope: CoroutineScope) {
        scope.launch {
            val response = client.from("Uzytkownik").select().decodeList<User>()

            Log.d("FirstUserUsername", response.toString())
        }
    }

    fun insertUser(scope: CoroutineScope, user: NewUser) {
        scope.launch {
            client.from("Uzytkownik").insert(user)
        }
    }

    fun isUserExists(scope: CoroutineScope, email: String, pass: String?=null, callback: (Boolean) -> Unit) {
        scope.launch {
            if (pass.isNullOrBlank()) {
                val response = client.from("Uzytkownik")
                    .select(Columns.list("email")) { filter { eq("email", email) } }
                    .decodeList<EmailWrapper>()
                val exists = response.any { it.email == email }
                callback(exists)
            }
            else {
                val response = client.from("Uzytkownik")
                    .select(Columns.list("email", "password")) { filter { eq("email", email) } }
                    .decodeList<LoggedUserWrapper>()
                val exists = response.any {it.email == email && it.password == pass}
                callback(exists)
            }
        }
    }
}