package com.example.bluetoothtestapplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.zakaprov.rxbluetoothadapter.RxBluetoothAdapter

class RxBluetoothAdapterViewModel(application: Application) : AndroidViewModel(application) {
    val data = MutableLiveData<String>("null")
    private val adapter by lazy { RxBluetoothAdapter(application) }

    fun bluetoothOn() {
    }

    fun bluetoothOff() {

    }

    fun scan() {
        val disposable = adapter.startDeviceScan()
            .subscribe({ device ->
                // Process next discovered device
                Log.d("KHJ","device: ${device.address} ${device.name} ${device.type} ${device.bondState}")

            }, { error ->
                Log.e("KHJ","error: $error")
                // Handle error
            }, {
                // Scan complete
            }
            )
    }

    fun pair() {

    }

    fun connect() {

    }

    fun disconnect() {

    }
}