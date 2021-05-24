package com.example.bluetoothtestapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetoothtestapplication.databinding.ActivityRxBluetoothBinding
import com.github.ivbaranov.rxbluetooth.RxBluetooth
import com.github.ivbaranov.rxbluetooth.predicates.BtPredicate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers


class RxBluetoothActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRxBluetoothBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)




        val rxBluetooth = RxBluetooth(this)
        val disposable = rxBluetooth.observeDevices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe({ device ->
                Log.d(
                    "KHJ",
                    "device: ${device.name} ${device.address} ${device.bondState} ${device.type} ${device.uuids}"
                )
            }) { throwable -> Log.d("KHJ", "throwable: $throwable") }

        val devices = rxBluetooth.observeDevices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ device ->
                Log.d(
                    "KHJ",
                    "device: ${device.name} ${device.address} ${device.bondState} ${device.type} ${device.uuids}"
                )
            }) { throwable -> Log.d("KHJ", "throwable: $throwable") }


        val discovery = rxBluetooth.observeDiscovery()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .filter(BtPredicate.`in`(BluetoothAdapter.ACTION_DISCOVERY_STARTED,BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
            .subscribe({
                Log.d("KHJ", "$it")
            }) { throwable ->
                Log.d("KHJ", "throwable: $throwable")
            }
        val state = rxBluetooth.observeBluetoothState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .filter(BtPredicate.`in`(BluetoothAdapter.STATE_ON))
            .subscribe({
                Log.d("KHJ", "$it")
            }){throwable ->
                Log.d("KHJ", "throwable: $throwable")
            }
    }
}