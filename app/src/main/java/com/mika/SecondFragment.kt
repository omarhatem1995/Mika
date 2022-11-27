package com.mika

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.mika.databinding.FragmentSecondBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var firebaseDataBase : FirebaseDatabase?=null
    private var selectedDates:Pair<String,String>?= null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userID = arguments?.getString("userID")
        firebaseDataBase = FirebaseDatabase.getInstance()
       val calendar:Calendar = Calendar.getInstance()
        val current =
            calendar.get(Calendar.MONTH)




        showDateRangePicker()


    }

    private fun firebaseResponse(response :  Response<DataSnapshot>?, userID : String) : Flow<User> {
        Log.d("getUserID" , "$userID")
        val res = response as Response.Success
        Log.d("getUserID" , "$res")
        return flow{
            res.data.child(userID).getValue(User::class.java)?.let {
                Log.d("getUserID", "y3m $it")
                emit(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildDatePicker(): MaterialDatePicker<Pair<Long, Long>> {

        val calendarConstraintBuilder = CalendarConstraints.Builder()
       // val dateValidator = RangeDateValidator(7)
        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        val month = LocalDate.now().month.value
        val day = Calendar.SATURDAY
        calendarConstraintBuilder.setFirstDayOfWeek(day)

        // now set the starting bound from current month to
        // previous MARCH
    calendar.set(Calendar.MONTH, month);
        val start = calendar.timeInMillis;

        // now set the ending bound from current month to
       // DECEMBER
      calendar.set(Calendar.MONTH, month);
       val end = calendar.timeInMillis;


        calendarConstraintBuilder.setStart(start);
      calendarConstraintBuilder.setEnd(end);

        return   MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .setCalendarConstraints(calendarConstraintBuilder.build())
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()
    }


    private fun getSelectedDates(dates: Pair<Long, Long>): Pair<String, String>? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val netDate1 = Date(dates.first)
        val netDate2 = Date(dates.second)
        val firstDate = sdf.format(netDate1)
        val secondDate = sdf.format(netDate2)
        return Pair(firstDate, secondDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDateRangePicker() {
        val dateRangePicker = buildDatePicker()
        dateRangePicker.addOnPositiveButtonClickListener {

            selectedDates = getSelectedDates(it)
            if (it.second-it.first > 4)
            {
                Toast.makeText(requireContext(),"You Are Allowed 4 days per month only",Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.SecondFragment)
            }


            //navigateToEmployeesVisits(selectedDates!!.first, selectedDates!!.second)

        }

        dateRangePicker.show(requireActivity().supportFragmentManager, "tag")
    }
}