package com.mika

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val languageLiveDate : MutableLiveData<String> = MutableLiveData(null)
}