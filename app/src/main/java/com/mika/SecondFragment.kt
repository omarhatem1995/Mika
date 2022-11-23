package com.mika

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Pair
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mika.databinding.FragmentSecondBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var firebaseDataBase : FirebaseDatabase?=null

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

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userID = arguments?.getString("userID")
        firebaseDataBase = FirebaseDatabase.getInstance()
       val calendar:Calendar = Calendar.getInstance()
        val current =
            calendar.get(Calendar.MONTH)

        val day = Calendar.SATURDAY



        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setCalendarConstraints(CalendarConstraints.Builder()
                    .setStart(current.toLong())
                    .setEnd(current.toLong())
                    .setOpenAt(current.toLong())
                    .setFirstDayOfWeek(day).build()
                )
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()

        dateRangePicker.show(childFragmentManager,"")


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
}