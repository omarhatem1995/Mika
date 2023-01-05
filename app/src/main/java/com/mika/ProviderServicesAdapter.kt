package com.mika

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mika.databinding.ProviderItemBinding

class ProviderServicesAdapter: ListAdapter<User, ProviderServicesAdapter.ViewHolder>(DiffUtil) {

    private var itemBinding:ProviderItemBinding?=null


    inner class ViewHolder(private val binding: ProviderItemBinding):
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
    {
            fun bind(user:User)
            {

                binding.carColorText.text = user.vehicleColor
                binding.carNumberText.text = user.plateNumber
                binding.carTypeText.text = user.vehicleType
                binding.userNameText.text = user.userName
                Log.d("getUserData" , " ${user.vehicleType}")
             /*   binding.callUserImage.setOnClickListener {
                    user.phoneNumber?.let { it1 -> callClient?.call(it1) }
                }*/
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       itemBinding = ProviderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(itemBinding!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    object DiffUtil: androidx.recyclerview.widget.DiffUtil.ItemCallback<User>()
    {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
           return oldItem.phoneNumber == newItem.phoneNumber
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
           return oldItem==newItem
        }

    }

    var callClient:CallClient?=null

    interface CallClient{

        fun call(phone:String)
    }


}