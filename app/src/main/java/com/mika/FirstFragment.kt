package com.mika

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.mika.Constants.CLIENT_USER_TYPE
import com.mika.databinding.FragmentFirstBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
        checkUserExistence(uID)

        binding.buttonFirst.setOnClickListener {
            if (uID != null && displayName != null && email != null) {
                    setUserDataInFirebase(uID,displayName,email)
            }
        }
    }

    private fun checkUserExistence(userID:String?) {
        viewLifecycleOwner.lifecycleScope.launch {

            val response = firebaseDataBase?.reference?.child("Users")?.singleValueEvent()

            if(response is Response.Success){
//               val user =  response.data.child("$userID").value
                if (userID != null) {
                    firebaseResponse(response,userID).flowWithLifecycle(viewLifecycleOwner.lifecycle
                        , Lifecycle.State.STARTED).collect{
                        Log.d("getUserID", "da5al gwa el Collect ${it.userType}")
                        navigateToSecondClientHomeFragment()
                    }
                }else{
                    Toast.makeText(context,"USER ID NULL", Toast.LENGTH_LONG).show()

                }
            }else{
                Toast.makeText(context,"USER ID NULL", Toast.LENGTH_LONG).show()

            }
        }

    }

    private fun navigateToSecondClientHomeFragment() {
//        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    private fun firebaseResponse(response :  Response<DataSnapshot>?, userID : String) : Flow<User> {
        Log.d("getUserID" , "$userID")
        val res = response as Response.Success
        Log.d("getUserID" , "$res")
        return flow{
            res.data.child(CLIENT_USER_TYPE).child(userID).getValue(User::class.java)?.let {
                Log.d("getUserID", "y3m $it")
                emit(it) }
        }
    }


    private fun setUserDataInFirebase(uID : String, userName : String, email : String){
        val user = User(uuID = uID,userName = userName , userType = CLIENT_USER_TYPE ,
            email = email , plateNumber = binding.plateNumber.text.toString() ,
            phoneNumber = binding.phoneNumber.text.toString(),
            vehicleType = binding.vehicleType.text.toString(),
            vehicleColor = binding.vehicleColor.text.toString())
        viewLifecycleOwner.lifecycleScope.launch {
            firebaseDataBase?.reference?.child("Users")?.child(CLIENT_USER_TYPE)
                ?.child("${user.uuID}")?.setValue(user)?.await()

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