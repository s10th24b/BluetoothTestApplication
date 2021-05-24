package com.example.bluetoothtestapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.example.bluetoothtestapplication.databinding.ActivityMainBinding
import splitties.toast.toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.*


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()

    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val BT_MESSAGE_READ = 2
        const val BT_CONNECTING_STATUS = 3

        //        val BT_UUID: UUID = UUID.fromString("1234")
        val BT_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
            startButton.setOnClickListener {
                bluetoothOn()
            }
            endButton.setOnClickListener {
                bluetoothOff()
            }
            connectButton.setOnClickListener {
                listPairedDevices()
            }
            disconnectButton.setOnClickListener {
                bluetoothDisconnect()
            }
            sendDataButton.setOnClickListener {
                write("A")
            }
        }
        viewModel.connected.observe(this) {
            binding.connectButton.isEnabled = !it
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        toast("RESULT_OK")
                        viewModel.bluetoothOn()
//                        binding.stateTextView.text = "활성화"
                    }
                    AppCompatActivity.RESULT_CANCELED -> {
                        toast("RESULT_CANCELED")
//                        binding.stateTextView.text = "비활성화"
                    }
                    else -> {
                        toast("RESULT_UNKNOWN")
                    }
                }
            }
            else -> {
                toast("Unknown requestCode")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun bluetoothOn() {
        val intentBluetoothEnable = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intentBluetoothEnable, MainViewModel.REQUEST_ENABLE_BT)
        viewModel.bluetoothOn()
    }

    fun bluetoothOff() {
        viewModel.bluetoothOff()
    }

    fun listPairedDevices() {
        viewModel.listPairedDevices()
    }

    fun bluetoothDisconnect() {
        viewModel.bluetoothDisconnect()
    }

    fun write(str: String) {
        viewModel.write(str)
    }

}