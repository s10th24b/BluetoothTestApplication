package com.example.bluetoothtestapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import splitties.toast.toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainViewModelBackUp : ViewModel() {
    val data = MutableLiveData<String>(null)
}
/*
    private val mBluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private lateinit var mListPairedDevices: MutableList<String>
    private lateinit var mBluetoothDevice: BluetoothDevice
    private lateinit var mBluetoothSocket: BluetoothSocket
    private lateinit var mThreadConnectedBluetooth: ConnectedBluetoothThread

    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val BT_MESSAGE_READ = 2
        const val BT_CONNECTING_STATUS = 3

        //        val BT_UUID: UUID = UUID.fromString("1234")
        val BT_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    fun bluetoothOn() {
        if (mBluetoothAdapter == null) {
            toast("블루투스를 지원하지 않는 기기입니다.")
            toast("블루투스를 지원하지 않는 기기입니다.")
        } else {
            if (mBluetoothAdapter.isEnabled) {
                toast("블루투스가 이미 활성화 되어 있습니다.")
                binding.stateTextView.text = "활성화"
            } else {
                toast("블루투스가 활성화 되어 있지 않습니다.")
                val intentBluetoothEnable = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intentBluetoothEnable, REQUEST_ENABLE_BT)
            }
        }
        val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceType = device.type // MAC address
            val deviceHardwareAddress = device.address // MAC address
            Log.d("KHJ", "Pairing Devices: $deviceName , $deviceType, $deviceHardwareAddress")
        }
    }

    fun bluetoothOff() {
        if (mBluetoothAdapter.isEnabled) {
            mBluetoothAdapter.disable()
            toast("블루투스가 비활성화 되었습니다.")
            binding.stateTextView.text = "비활성화"
        } else {
            toast("블루투스가 이미 비활성화 되어 있습니다.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        toast("RESULT_OK")
                        binding.stateTextView.text = "활성화"
                    }
                    AppCompatActivity.RESULT_CANCELED -> {
                        toast("RESULT_CANCELED")
                        binding.stateTextView.text = "비활성화"
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

    fun listPairedDevices() {
        if (mBluetoothAdapter.isEnabled) {
            mPairedDevices = mBluetoothAdapter.bondedDevices
            if (mPairedDevices.isNotEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("장치 선택")
                mListPairedDevices = mutableListOf()
                for (device in mPairedDevices) {
                    mListPairedDevices.add(device.name)
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                builder.setItems(mListPairedDevices.toTypedArray()) { _, item ->
                    connectSelectedDevice(mListPairedDevices[item])
                }
                builder.create().show()
            } else {
                toast("페어링된 장치가 없습니다.")
            }
        } else {
            toast("블루투스가 비활성화 되어 있습니다.")
        }
    }

    fun connectSelectedDevice(selectedDeviceName: String) {
        for (tempDevice in mPairedDevices) {
            if (selectedDeviceName == tempDevice.name) {
                mBluetoothDevice = tempDevice
                break
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID)
            mBluetoothSocket.connect()
            mThreadConnectedBluetooth = ConnectedBluetoothThread(mBluetoothSocket,viewModel)
            mThreadConnectedBluetooth.start()
//            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget()
        } catch (e: IOException) {
            toast("블루투스 연결 중 오류가 발생했습니다.")
            Log.d("KHJ", "$e")
        }
    }

    private class ConnectedBluetoothThread(private val mmSocket: BluetoothSocket, val viewModel: MainViewModel) : Thread() {
        private lateinit var mmInStream: InputStream
        private lateinit var mmOutStream: OutputStream

        init {
            try {
                mmInStream = mmSocket.inputStream;
                mmOutStream = mmSocket.outputStream;
            } catch (e: IOException) {
                toast("소켓 연결 중 오류가 발생했습니다.")
            }
        }

        override fun run() {
            val buffer = ByteArray(8)
            var bytes: Int
            while (true) {
                try {
                    bytes = mmInStream.available()
                    if (bytes != 0) {
                        SystemClock.sleep(100)
                        bytes = mmInStream.available()
                        bytes = mmInStream.read(buffer, 0, bytes)
//                        Log.d("KHJ", bytes.toString(16))
                        Log.d("KHJ", "${buffer.map{it.toUByte()}}")
                    }
                } catch (e: IOException) {
                    break
                }
            }
        }

        fun write(str: String) {
            val bytes = str.toByteArray()
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                toast("데이터 전송 중 오류가 발생했습니다.")
            }
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                toast("소켓 해제 중 오류가 발생했습니다.")
            }
        }
    }
    fun bluetoothDisconnect() {
        mBluetoothSocket.close()
    }
}*/
