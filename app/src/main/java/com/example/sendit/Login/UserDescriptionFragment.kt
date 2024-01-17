package com.example.sendit.Login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.sendit.HelperFunctions
import com.example.sendit.NavMainActivity
import com.example.sendit.R
import com.example.sendit.Repos.SupabaseRepo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

private const val ARG_PARAM1 = "email"
private const val ARG_PARAM2 = "password"

class UserDescriptionFragment : Fragment() {

    private var email: String? = null
    private var password: String? = null
    private val helpersFuncs = HelperFunctions()
    private val sbRepo = SupabaseRepo()


    private lateinit var tvUsername: TextView
    private lateinit var etUsername: EditText
    private lateinit var tvDate: TextView
    private lateinit var yearPicker: NumberPicker
    private lateinit var monthPicker: NumberPicker
    private lateinit var dayPicker: NumberPicker
    private lateinit var tvDescription: TextView
    private lateinit var editTextDescription: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvUsernameWarn: TextView
    private lateinit var tvDescrWarn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString(ARG_PARAM1)
            password = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername = view.findViewById(R.id.tv_username)
        etUsername = view.findViewById(R.id.et_username)
        tvDate = view.findViewById(R.id.tv_date)
        yearPicker = view.findViewById(R.id.yearPicker)
        monthPicker = view.findViewById(R.id.monthPicker)
        dayPicker = view.findViewById(R.id.dayPicker)
        tvDescription = view.findViewById(R.id.tv_description)
        editTextDescription = view.findViewById(R.id.editTextTextMultiLine)
        btnSignUp = view.findViewById(R.id.bt_signup)
        tvUsernameWarn = view.findViewById(R.id.tv_warning_username)
        tvDescrWarn = view.findViewById(R.id.tv_warning_descr)

        datePickerSettings()

        btnSignUp.setOnClickListener { view, ->
            if (validate()) {
                sbRepo.signUp(lifecycleScope, helpersFuncs.trimStr(etUsername), createLocalDateTimeFromPickers(),
                    email!!, password!!, helpersFuncs.trimStr(editTextDescription)) {idU ->
                    helpersFuncs.saveUserLogging(view, idU)
                    navigateToSecondActivity()
                }
            }
        }

    }

    private fun createLocalDateTimeFromPickers(): LocalDateTime {
        val selectedYear = yearPicker.value
        val selectedMonth = monthPicker.value
        val selectedDay = dayPicker.value

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        val formattedMonth = if (selectedMonth < 10) "0$selectedMonth" else selectedMonth.toString()
        val formattedDay = if (selectedDay < 10) "0$selectedDay" else selectedDay.toString()

        val dateTimeString = "$selectedYear-$formattedMonth-$formattedDay 00:00"

        return LocalDateTime.parse(dateTimeString, formatter)
    }

    private fun datePickerSettings() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear
        yearPicker.value = currentYear

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = 1

        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = 1
    }

    private fun validate(): Boolean {
        val username = helpersFuncs.trimStr(etUsername).isNullOrBlank()
        val descryption = helpersFuncs.trimStr(editTextDescription).isNullOrBlank()
        if (username) {
            helpersFuncs.changeTVHeight(tvUsernameWarn)
            return false
        } else helpersFuncs.changeTVHeight(tvUsernameWarn, 1)
        if (descryption) {
            helpersFuncs.changeTVHeight(tvDescrWarn)
            return false
        } else helpersFuncs.changeTVHeight(tvDescrWarn, 1)
        return true
    }

    private fun navigateToSecondActivity() {
        val intent = Intent(activity, NavMainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }



    companion object {
        @JvmStatic
        fun newInstance(email: String, password: String) =
            UserDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, email)
                    putString(ARG_PARAM2, password)
                }
            }
    }
}
