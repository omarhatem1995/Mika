package com.mika

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.mika.Constants.ADMIN_USER_TYPE
import com.mika.Constants.CLIENT_USER_TYPE
import com.mika.Constants.PROVIDER_USER_TYPE
import com.mika.databinding.FragmentFirstBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.mika.R


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDataBase: FirebaseDatabase? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        requireActivity().actionBar?.hide()

        var email = firebaseAuth?.currentUser?.email
        var uID = firebaseAuth?.currentUser?.uid
        var displayName = firebaseAuth?.currentUser?.displayName

        firebaseDataBase = FirebaseDatabase.getInstance()
        val edit = arguments?.getBoolean("edit", false)

        if (arguments != null && edit != null) {
            checkUserExistence(uID, true)

        } else {
            checkUserExistence(uID, false)
        }

        binding.buttonFirst.setOnClickListener {
            if (uID != null && displayName != null && email != null) {
                setUserDataInFirebase(uID, displayName, email)
            }
        }
    }

    private fun checkUserExistence(userID: String?, edit: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {

            _binding?.progressFirst?.visibility = View.VISIBLE
//            checkUserType(userID)
            if (edit) {
                showEditTextsWhenDataNotAvailable()
                val response =
                    firebaseDataBase?.reference?.child("Users")?.child("Client")?.child("$userID")
                        ?.valueEventFlow()?.collect {
                            when (it) {
                                is Response.Success -> {
                                    if (it.data.value != null) {
                                        _binding?.phoneNumber?.setText(it.data.child("phoneNumber").value.toString())
                                        _binding?.plateNumber?.setText(it.data.child("plateNumber").value.toString())
                                        _binding?.vehicleType?.setText(it.data.child("vehicleType").value.toString())
                                        _binding?.vehicleColor?.setText(it.data.child("vehicleColor").value.toString())

                                    }
                                }
                                else -> {

                                }
                            }
                        }
            }else {
                val response = firebaseDataBase?.reference?.child("Types")?.singleValueEvent()

                if (response is Response.Success) {
//               val user =  response.data.child("$userID").value
                    if (userID != null) {
                        firebaseResponse(response, userID).flowWithLifecycle(
                            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect {
                            if (it != null) {
                                when (it.type) {
                                    CLIENT_USER_TYPE -> {
                                        navigateToSecondClientHomeFragment()
                                    }
                                    ADMIN_USER_TYPE -> {
                                        navigateToAdminFragment()
                                    }
                                    else -> {
                                        navigateToProviderFragment()
                                    }
                                }
                            } else {
                                // Toast.makeText(context,"USER Not Found", Toast.LENGTH_LONG).show()
                                showEditTextsWhenDataNotAvailable()
                            }
                        }
                    } else {
                        _binding?.progressFirst?.visibility = View.GONE
                        Toast.makeText(context, "USER ID NULL", Toast.LENGTH_LONG).show()
                    }
                } else {
                    showEditTextsWhenDataNotAvailable()
                    //Toast.makeText(context,"USER Not Found", Toast.LENGTH_LONG).show()

                }
            }
        }

    }

    private fun navigateToProviderFragment() {

        findNavController().navigate(R.id.action_FirstFragment_to_providerFragment)
    }
    private fun navigateToAdminFragment() {

        findNavController().navigate(R.id.action_FirstFragment_to_adminFragment)
    }

    private fun checkUserType(userID: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val response =
                firebaseDataBase?.reference?.child("Types")?.child("$userID")?.valueEventFlow()
                    ?.flowWithLifecycle(viewLifecycleOwner.lifecycle)?.collect {
                        when (it) {
                            is Response.Success -> {
                                if (it.data.value != null) {
                                    if (it.data.child("type").value.toString() == PROVIDER_USER_TYPE) findNavController().navigate(
                                        R.id.action_FirstFragment_to_providerFragment
                                    )
                                }
                            }
                            else -> {

                            }
                        }
                    }
        }

    }


    private fun navigateToSecondClientHomeFragment() {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)

    }

    private fun firebaseResponse(
        response: Response<DataSnapshot>?, userID: String
    ): Flow<UserType?> {
        val res = response as Response.Success

        return flow {

            emit(res.data.child(userID).getValue(UserType::class.java))
        }
    }


    private fun setUserDataInFirebase(uID: String, userName: String, email: String) {
        if (_binding?.plateNumber?.text.toString().trim()
                .isNotEmpty() && _binding?.phoneNumber?.text.toString().trim()
                .isNotEmpty() && _binding?.vehicleType?.text.toString().trim()
                .isNotEmpty() && _binding?.vehicleColor?.text.toString().trim().isNotEmpty()
        ) {
            val user = User(
                uuID = uID,
                userName = userName,
                userType = CLIENT_USER_TYPE,
                email = email,
                plateNumber = binding.plateNumber.text.toString(),
                phoneNumber = binding.phoneNumber.text.toString(),
                vehicleType = binding.vehicleType.text.toString(),
                vehicleColor = binding.vehicleColor.text.toString()
            )
            viewLifecycleOwner.lifecycleScope.launch {
                firebaseDataBase?.reference?.child("Users")?.child(CLIENT_USER_TYPE)
                    ?.child("${user.uuID}")?.setValue(user)?.await()

                val type = UserType(CLIENT_USER_TYPE)
                firebaseDataBase?.reference?.child("Types")?.child(user.uuID!!)?.setValue(type)
                    ?.await()

                val bundle = Bundle()
                bundle.putString("userID", uID)
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
            }
        } else {
            Toast.makeText(requireContext(), "Please Check the empty field", Toast.LENGTH_LONG)
                .show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showEditTextsWhenDataNotAvailable() {
        _binding?.progressFirst?.visibility = View.GONE
        _binding?.phoneNumberTextInputLayout?.visibility = View.VISIBLE
        _binding?.plateNumberTextInputLayout?.visibility = View.VISIBLE
        _binding?.vehicleColorTextInputLayout?.visibility = View.VISIBLE
        _binding?.vehicleTypeTextInputLayout?.visibility = View.VISIBLE
        _binding?.buttonFirst?.visibility = View.VISIBLE
    }
}
