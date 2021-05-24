package com.example.bluetoothtestapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bluetoothtestapplication.databinding.ActivityBleBinding
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.disposables.Disposable


class BleActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBleBinding.inflate(layoutInflater) }

    companion object {
        const val LOCATION_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.bleStartButton.setOnClickListener {
            start()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }

    fun start() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_CODE
        )
        val rxBleClient = RxBleClient.create(this)
        val macAddress = "98:D3:31:F7:98:3A"
/*
        val device = rxBleClient.getBleDevice(macAddress)
        val disposable: Disposable = device.establishConnection(false) // <-- autoConnect flag
            .subscribe(
                { rxBleConnection -> Log.d("KHJ","connection: ${rxBleConnection}")}
            ) { throwable -> Log.e("KHJ","throwable $throwable")}
*/
        // When done... dispose and forget about connection teardown :)
//        disposable.dispose();
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        val scanFilter = ScanFilter.Builder()
//            .setDeviceAddress("B4:99:4C:34:DC:8B")
            // add custom filters if needed
            .build()
        val scanSubscription: Disposable = rxBleClient.scanBleDevices(scanSettings,scanFilter)
            .subscribe(
                { scanResult ->
//                    if (scanResult.bleDevice.macAddress != null)
                    Log.d("KHJ", "scanResult: ${scanResult.bleDevice.macAddress} ${scanResult.bleDevice.name}")
                }
            ) { throwable -> Log.d("KHJ", "throwable $throwable") }

// When done, just dispose.
//        scanSubscription.dispose()

/*
        val flowDisposable = rxBleClient.observeStateChanges()
            .switchMap<Any> { state: RxBleClient.State? ->
                when (state) {
                    RxBleClient.State.READY ->                 // everything should work
                    {
                        Log.d("KHJ", "RxBleClient READY")
                        return@switchMap rxBleClient.scanBleDevices()
                    }
                    RxBleClient.State.BLUETOOTH_NOT_AVAILABLE, RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED, RxBleClient.State.BLUETOOTH_NOT_ENABLED, RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> return@switchMap Observable.empty()
                    else -> {
                        Log.d("KHJ", "Observable.empty()")
                        return@switchMap Observable.empty()
                    }
                }
            }
            .subscribe(
                { scanResult: Any? ->
                    Log.d("KHJ", "scanResult: ${(scanResult as ScanResult).device.name}")
                }
            ) { throwable -> Log.d("KHJ", "throwable $throwable") }
*/

// When done, just dispose.
//        flowDisposable.dispose()
    }
}