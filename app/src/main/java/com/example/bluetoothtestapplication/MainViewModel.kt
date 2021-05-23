package com.example.bluetoothtestapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val data = MutableLiveData<String>(null)
}