package com.mika

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mika.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var firebaseDataBase : FirebaseDatabase?=null
    private var firebaseAuth : FirebaseAuth? = null
    private var selectedDates:Pair<String,String>?= null
//    val activity :MainActivity = requireActivity() as MainActivity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.title = getString(R.string.your_dates)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    companion object {
        var instance : SecondFragment? = null
        fun getInstanse():SecondFragment
        {
            if (instance == null)
            {
                instance = SecondFragment()
            }
            return instance as SecondFragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

/*
        activity.setSupportActionBar(_binding?.toolBar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true);
        _binding?.navigationDrawer?.setupWithNavController(findNavController())
        _binding?.navigationDrawer?.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(requireActivity(), _binding?.drawerLayout, R.string.nav_open, R.string.nav_close);
        _binding?.drawerLayout?.addDrawerListener(toggle)
        _binding?.navigationDrawer?.bringToFront()
        toggle.syncState()
*/





        var userID = arguments?.getString("userID")
        firebaseDataBase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val calendar:Calendar = Calendar.getInstance()
        val current =
            calendar.get(Calendar.MONTH)

        _binding?.firstDay?.setOnClickListener {
            showDateRangePicker("First Day")
        }
        _binding?.secondDay?.setOnClickListener {
            showDateRangePicker("Second Day")
        }
        _binding?.thirdDay?.setOnClickListener {
            showDateRangePicker("Third Day")
        }
        _binding?.fourthDay?.setOnClickListener {
            showDateRangePicker("Fourth Day")
        }

        _binding?.submitDates?.setOnClickListener {
            if(selectedDates2.size < 4)
                Toast.makeText(requireContext(),getString(R.string.please_select_your_4_dates),Toast.LENGTH_LONG).show()
            else
                viewLifecycleOwner.lifecycleScope.launch { insertDatesInFirebase() }

        }
        retrieveFirebase()
    }

    private suspend fun insertDatesInFirebase() {
        var currentMonth = selectedDates2.get(0)
        currentMonth = currentMonth.substringAfter("-")
        currentMonth = currentMonth.substringBefore("-")
        var datesMap = HashMap<String, String>()
        datesMap.put("First", "${selectedDates2.get(0)}");
        datesMap.put("Second", "${selectedDates2.get(1)}");
        datesMap.put("Third", "${selectedDates2.get(2)}");
        datesMap.put("Fourth", "${selectedDates2.get(3)}");
        firebaseDataBase?.reference?.child("Dates")?.child("${firebaseAuth?.currentUser?.uid}")
            ?.child(currentMonth)?.setValue(datesMap)?.await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun retrieveFirebase(){
        val currentMonth = LocalDate.now().month.value
        viewLifecycleOwner.lifecycleScope.launch {
            firebaseDataBase?.reference?.child("Dates")?.child("${firebaseAuth?.currentUser?.uid}")
                ?.child(currentMonth.toString())?.valueEventFlow()?.collect {
                    when(it){
                        is Response.Success -> {
                            if (it.data.value != null) {
                                _binding?.submitDates?.visibility = View.GONE
                                _binding?.firstDayTextview?.text =
                                    it.data.child("First").value.toString()
                                _binding?.firstDay?.isEnabled = false
                                _binding?.secondDayTextview?.text =
                                    it.data.child("Second").value.toString()
                                _binding?.secondDay?.isEnabled = false
                                _binding?.thirdDayTextview?.text =
                                    it.data.child("Third").value.toString()
                                _binding?.thirdDay?.isEnabled = false
                                _binding?.fourthDayTextview?.text =
                                    it.data.child("Fourth").value.toString()
                                _binding?.fourthDay?.isEnabled = false
                            }else{
                                _binding?.submitDates?.visibility = View.VISIBLE
                            }
                        }
                        is Response.Error -> {
                            _binding?.submitDates?.visibility = View.VISIBLE

                        }
                        is Response.Loading -> {
                            _binding?.submitDates?.visibility = View.GONE

                        }
                    }
                }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildDatePicker(title:String): MaterialDatePicker< Long> {

        val calendarConstraintBuilder = CalendarConstraints.Builder()
       // val dateValidator = RangeDateValidator(7)
        calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        val month = LocalDate.now().month.value
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
            .setTitleText("$title")
            .setCalendarConstraints(calendarConstraintBuilder.build())
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

    private fun getSelectedDates(dates: Long): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val netDate1 = Date(dates)
        return sdf.format(netDate1)
    }
    var selectedDates2 = ArrayList<String>(4)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDateRangePicker(title:String) {
        val materialDatePicker = buildDatePicker(title)
        materialDatePicker.addOnPositiveButtonClickListener {
            val date = getSelectedDates(it)
            if (checkSelectedDay(date.toString())) {
                if (title == "First Day") {
                    selectedDates2.add(0, date.toString())
                    _binding?.firstDayTextview?.text = date.toString()
                } else if (title == "Second Day") {
                    selectedDates2.add(1, date.toString())
                    _binding?.secondDayTextview?.text = date.toString()
                } else if (title == "Third Day") {
                    selectedDates2.add(2, date.toString())
                    _binding?.thirdDayTextview?.text = date.toString()
                } else if (title == "Fourth Day") {
                    selectedDates2.add(3, date.toString())
                    _binding?.fourthDayTextview?.text = date.toString()
                }
            }else {
                Toast.makeText(requireContext(),"This date is already selected",Toast.LENGTH_LONG).show()
            }
            // selectedDates = getSelectedDates(it)
//            if (it.second-it.first > 4)
//            {
//                Toast.makeText(requireContext(),"You Are Allowed 4 days per month only",Toast.LENGTH_LONG).show()
//                findNavController().navigate(R.id.SecondFragment)
//            }


            //navigateToEmployeesVisits(selectedDates!!.first, selectedDates!!.second)

        }


        materialDatePicker.show(requireActivity().supportFragmentManager, "tag")
    }

    fun checkSelectedDay(date : String) : Boolean{
        return !(selectedDates2.contains(date))
    }

/*    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.nav_account -> {
                Toast.makeText(requireContext(),"HELLO",Toast.LENGTH_LONG).show()
//                _binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }
            R.id.nav_settings -> {

            }
            R.id.nav_logout -> {

            }
           com.google.android.material.R.id.home->{

           }

        }
        return true
    }*/



}