package com.example.bluetoothtestapplication

import android.Manifest
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.bluetoothtestapplication.databinding.ActivityRxBluetoothAdapterBinding
import com.example.bluetoothtestapplication.databinding.ActivityRxBluetoothBinding
import splitties.resources.color

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
//                Manifest.permission.ACCESS_FINE_LOCATION,
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
        viewModel.scanComplete.observe(this) {
            when (it) {
                true -> {
                    binding.blueOnBtn.isEnabled = true
                    binding.blueOffBtn.isEnabled = true
                    binding.blueScanBtn.isEnabled = true
                    binding.bluePairBtn.isEnabled = true
                    binding.blueConnectBtn.isEnabled = true
                    binding.blueDisconnectBtn.isEnabled = false
                }
                false -> {
                    binding.blueOnBtn.isEnabled = false
                    binding.blueOffBtn.isEnabled = false
                    binding.blueScanBtn.isEnabled = false
                    binding.bluePairBtn.isEnabled = false
                    binding.blueConnectBtn.isEnabled = false
                    binding.blueDisconnectBtn.isEnabled = false
                }
            }
        }
        viewModel.pairable.observe(this) {
            binding.bluePairableTextView.text =
                when (it) {
                    true -> {
                        binding.bluePairBtn.isEnabled = true
                        binding.bluePairableTextView.setTextColor(Color.GREEN)
                        "페어링 가능"
                    }
                    else -> {
                        binding.bluePairBtn.isEnabled = false
                        binding.bluePairableTextView.setTextColor(Color.RED)
                        "페어링 불가능"
                    }
                }
        }
        viewModel.connectionState.observe(this) {
            when (it) {
                -1 -> {
                    binding.blueOnBtn.isEnabled = true
                    binding.blueOffBtn.isEnabled = true
                    binding.blueScanBtn.isEnabled = true
                    binding.bluePairBtn.isEnabled = true
                    binding.blueConnectBtn.isEnabled = true
                    binding.blueDisconnectBtn.isEnabled = false
                }
                0 -> {
                    binding.blueOnBtn.isEnabled = false
                    binding.blueOffBtn.isEnabled = false
                    binding.blueScanBtn.isEnabled = false
                    binding.bluePairBtn.isEnabled = false
                    binding.blueConnectBtn.isEnabled = false
                    binding.blueDisconnectBtn.isEnabled = false
                }
                1 -> {
                    binding.blueOnBtn.isEnabled = true
                    binding.blueOffBtn.isEnabled = true
                    binding.blueScanBtn.isEnabled = true
                    binding.bluePairBtn.isEnabled = true
                    binding.blueConnectBtn.isEnabled = false
                    binding.blueDisconnectBtn.isEnabled = true
                }
            }
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