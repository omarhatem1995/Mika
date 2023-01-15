package com.mika

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mika.databinding.FragmentAdminBinding
import com.mika.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDataBase: FirebaseDatabase? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseDate.setOnClickListener {
            showDateRangePicker()
        }
    }
    var arrayListOfDates = ArrayList<String>()

    private fun showDateRangePicker() {
        val materialDatePicker = buildDatePicker()
        materialDatePicker.addOnPositiveButtonClickListener {
            val date = getSelectedDates(it)
            val currentMonth = date?.split("-")?.get(0)

            binding.selectedDate.text = date
            viewLifecycleOwner.lifecycleScope.launch {
                firebaseDataBase?.reference?.child("Dates")?.valueEventFlow()?.collect {
                    when (it) {
                        is Response.Success -> {
                            if(it.data.value == currentMonth)
                                arrayListOfDates.add(it.data.value.toString())
                            Log.d("getDates" , "${arrayListOfDates.size}")
                        }
                            else -> {

                                Log.d("getDates" , "else")
                        }
                    }
                }
            }
        }


        materialDatePicker.show(requireActivity().supportFragmentManager, "tag")
    }
    private fun getSelectedDates(dates: Long): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val netDate1 = Date(dates)
        return sdf.format(netDate1)
    }
    @SuppressLint("RestrictedApi")
    private fun buildDatePicker(): MaterialDatePicker<Long> {

        val calendarConstraintBuilder = CalendarConstraints.Builder()
        // val dateValidator = RangeDateValidator(7)
        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        val day = Calendar.SATURDAY
        calendarConstraintBuilder.setFirstDayOfWeek(day)

        // now set the starting bound from current month to
        // previous MARCH
        //calendar.set(Calendar.MONTH, month);
        val start = calendar.timeInMillis;
        // now set the ending bound from current month to
        // DECEMBER
        //  calendar.set(Calendar.MONTH, month);
        val end = calendar.timeInMillis;

        calendarConstraintBuilder.setStart(start);
        calendarConstraintBuilder.setEnd(end);


        return   MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date")
            .setCalendarConstraints(calendarConstraintBuilder.build())
            .build()
    }
}