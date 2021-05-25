package com.example.bluetoothtestapplication

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.bluetoothtestapplication.databinding.ActivityRxBluetoothAdapterBinding
import com.example.bluetoothtestapplication.databinding.ActivityRxBluetoothBinding

class RxBluetoothAdapterActivity : AppCompatActivity() {
    val viewModel: RxBluetoothAdapterViewModel by viewModels()
    private val binding by lazy { ActivityRxBluetoothAdapterBinding.inflate(layoutInflater) }

    companion object {
        const val LOCATION_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            RxBluetoothAdapterActivity.LOCATION_PERMISSION_CODE
        )
        binding.apply {
            viewModel = this@RxBluetoothAdapterActivity.viewModel
            lifecycleOwner = this@RxBluetoothAdapterActivity
            blueOnBtn.setOnClickListener { bluetoothOn() }
            blueOffBtn.setOnClickListener { bluetoothOff() }
            blueScanBtn.setOnClickListener { scan() }
            bluePairBtn.setOnClickListener { pair() }
            blueConnectBtn.setOnClickListener { connect() }
            blueDisconnectBtn.setOnClickListener { disconnect() }
        }
        viewModel.data.observe(this) {
        }
    }

    fun bluetoothOn() {
        viewModel.bluetoothOn()
    }

    fun bluetoothOff() {
        viewModel.bluetoothOff()

    }

    fun scan() {
        viewModel.scan()

    }

    fun pair() {
        viewModel.pair()

    }

    fun connect() {
        viewModel.connect()

    }

    fun disconnect() {
        viewModel.disconnect()

    }
}