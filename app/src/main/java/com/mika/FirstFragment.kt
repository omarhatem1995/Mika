package com.mika

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mika.Constants.CLIENT_USER_TYPE
import com.mika.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var firebaseAuth : FirebaseAuth? = null
    private var firebaseDataBase : FirebaseDatabase?=null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()


        var email = firebaseAuth?.currentUser?.email
        var uID = firebaseAuth?.currentUser?.uid
        var displayName = firebaseAuth?.currentUser?.displayName

        firebaseDataBase = FirebaseDatabase.getInstance()
        binding.buttonFirst.setOnClickListener {
            if (uID != null && displayName != null && email != null) {
                    setUserDataInFirebase(uID,displayName,email)
            }
        }
    }


    private fun setUserDataInFirebase(uID : String, userName : String, email : String){
        val user = User(uuID = uID,username = userName , userType = CLIENT_USER_TYPE ,
            email = email , plateNumber = binding.plateNumber.text.toString() ,
            phoneNumber = binding.phoneNumber.text.toString(),
            vehicleType = binding.vehicleType.text.toString(),
            vehicleColor = binding.vehicleColor.text.toString())
        viewLifecycleOwner.lifecycleScope.launch {
            firebaseDataBase?.reference?.child(CLIENT_USER_TYPE)
                ?.child("Users")?.child("${user.uuID}")?.setValue(user)?.await()

            val bundle = Bundle()
            bundle.putString("userID",uID)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}