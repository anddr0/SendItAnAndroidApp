package com.example.sendit.ViewModels

import Goal
import SubGoal
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.sendit.HelperFunctions
import com.example.sendit.Repos.SupabaseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DescriptionGoalViewModel : ViewModel() {
    private val helpersFuncs = HelperFunctions()
    val sbRepo = SupabaseRepo()

    private val subGoalsLiveData = MutableLiveData<List<SubGoal>>()

    suspend fun getGoalById(idC: Int): Goal {
        return sbRepo.getGoalByGoalId(idC)
    }

    //-------------------------SUB_GOALS-------------------------
    fun getUserSubGoals(scope: CoroutineScope, idC: Int) {
        scope.launch {
            val subGoals = sbRepo.getSubGoalsByIdC(idC)
            subGoalsLiveData.postValue(subGoals)
        }
    }
    fun getSubGoalsLiveData(): LiveData<List<SubGoal>> {
        return subGoalsLiveData
    }
    //-------------------------ChatGPT-------------------------
    fun sendGptRequest(prompt: String, apiKey: String, onResponse: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build()
                    val mediaType = "application/json".toMediaType()
                    val requestBody = JSONObject()
                        .put("model", "gpt-3.5-turbo") // Указание модели
                        .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", prompt)))
                        .toString()
                        .toRequestBody(mediaType)
                    val request = Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions") // Обновленный URL
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer $apiKey")
                        .addHeader("Content-Type", "application/json")
                        .build()

                    client.newCall(request).execute()
                }
                val responseBody = response.body?.string() ?: ""
                Log.d("API Response", "Response body: $responseBody")

                val responseJson = JSONObject(responseBody)
                val choices = responseJson.optJSONArray("choices")
                val firstChoice = choices?.optJSONObject(0)
                val message = firstChoice?.optJSONObject("message")?.optString("content")
                onResponse(message ?: "Error responding")
            } catch (e: Exception) {
                e.printStackTrace()
                onResponse("Error: ${e.message}")
            }
        }

    }

}