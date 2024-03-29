package com.mika

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mika.databinding.FragmentProviderBinding
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class ProviderFragment : Fragment(), ProviderServicesAdapter.CallClient {

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDataBase: FirebaseDatabase? = null
    private var adapter: ProviderServicesAdapter? = null
    private var binding: FragmentProviderBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDataBase = FirebaseDatabase.getInstance()

        binding = FragmentProviderBinding.inflate(inflater, container, false)

        return binding!!.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        getUsersListOfDates()
    }

    fun getUsersListOfDates(){
        val todaysDate = Date()
        adapter = ProviderServicesAdapter()
        adapter!!.callClient = this
        binding?.usersList?.adapter = adapter

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val sdfForDate = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

        var currentDate = sdf.format(todaysDate)

        binding?.currentDateTextView?.text = sdfForDate.format(todaysDate)

        Log.d("currentDate", "$currentDate ")
        var usersList = mutableListOf<User>()
        viewLifecycleOwner.lifecycleScope.launch {
            // Inflate the layout for this fragment
            firebaseDataBase?.reference?.child("Users")?.child("Provider")
                ?.child("${firebaseAuth?.currentUser?.uid}")
                ?.child("Visits")?.child(currentDate)?.valueEventFlow()?.collect {
                    when (it) {
                        is Response.Success -> {
                            if (it.data.value != null) {
                                Log.d("currentDateFotmat", "${it.data.value}")
                                it.data.children.forEach {
                                    firebaseDataBase?.reference?.child("Users")?.child("Client")
                                        ?.child("${it.getValue(String::class.java)}")
                                        ?.valueEventFlow()?.collect { response ->
                                            when (response) {
                                                is Response.Success -> {
                                                    if (response.data.value != null) {
                                                        Log.d(
                                                            "currentDateFotmat",
                                                            "${response.data.value}"
                                                        )


                                                        usersList.add(User().apply {
                                                            userName =
                                                                response.data.child("userName").value.toString()
                                                            phoneNumber =
                                                                response.data.child("phoneNumber").value.toString()
                                                            plateNumber =
                                                                response.data.child("plateNumber").value.toString()
                                                            vehicleType =
                                                                response.data.child("vehicleType").value.toString()
                                                            vehicleColor =
                                                                response.data.child("vehicleColor").value.toString()
                                                        })
                                                        Log.d(
                                                            "currentDateFotmat",
                                                            "${usersList.size}"
                                                        )

                                                    } else {
                                                        Log.d(
                                                            "currentDateFotmat",
                                                            "${response.data.value}"
                                                        )

                                                    }
                                                    adapter!!.submitList(usersList)
                                                }
                                                else -> {
                                                }
                                            }
                                        }
                                }
                                Log.d("currentDateFotmat", "${usersList.size}")


                            } else {
                                Log.d("currentDateFotmat", "${it.data.value}")

                            }
                        }
                        else -> {
                            Log.d("getDataUser", " is Else")
                        }

                    }
                }
        }

    }
    override fun call(phone: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phone")

        activity?.startActivity(callIntent)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}