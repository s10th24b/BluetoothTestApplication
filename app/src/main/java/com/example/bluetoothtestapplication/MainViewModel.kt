package com.example.bluetoothtestapplication

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import splitties.toast.toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val data = MutableLiveData<String>("null")
    val state = MutableLiveData<String>("비활성화")
    val pub: PublishSubject<ByteArray> by lazy { PublishSubject.create<ByteArray>() }

    val mBluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    lateinit var mPairedDevices: Set<BluetoothDevice>
    lateinit var mListPairedDevices: MutableList<String>
    lateinit var mBluetoothDevice: BluetoothDevice
    lateinit var mBluetoothSocket: BluetoothSocket
    private lateinit var mThreadConnectedBluetooth: ConnectedBluetoothThread

    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val BT_MESSAGE_READ = 2
        const val BT_CONNECTING_STATUS = 3
        val BT_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    init {
        if (mBluetoothAdapter.isEnabled) state.value = "활성화"
        val disposable = pub.subscribe({
            data.postValue(it.map{byte -> byte.toUByte()}.toString())
            Log.d("KHJ",it.map{byte -> byte.toUByte()}.toString())
        }) { throwable -> Log.d("KHJ", "throwable: $throwable") }
    }

    fun bluetoothOn() {
        if (mBluetoothAdapter == null) {
            state.value = "블루투스 미지원"
        } else {
            if (mBluetoothAdapter.isEnabled) {
//                toast("블루투스가 이미 활성화 되어 있습니다.")
                state.value = "활성화"
            } else {
//                toast("블루투스가 활성화 되어 있지 않습니다.")
                state.value = "비활성화"
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
            if (::mThreadConnectedBluetooth.isInitialized) {
                mThreadConnectedBluetooth.cancel()
            }
            mBluetoothAdapter.disable()
//            toast("블루투스가 비활성화 되었습니다.")
            state.value = "비활성화"
        } else {
//            toast("블루투스가 이미 비활성화 되어 있습니다.")
        }
    }

    fun listPairedDevices() {
        if (mBluetoothAdapter.isEnabled) {
            mPairedDevices = mBluetoothAdapter.bondedDevices
            if (mPairedDevices.isNotEmpty()) {
                mListPairedDevices = mutableListOf()
                for (device in mPairedDevices) {
                    mListPairedDevices.add(device.name)
                }
                val index = mListPairedDevices.indexOfFirst { it == "HC-06" }
                if (index != -1) {
                    connectSelectedDevice(mListPairedDevices[index])
                } else {
                    Log.d("KHJ", "no HC-06 in mListPairedDevices")
                }
            } else {
//                toast("페어링된 장치가 없습니다.")
                Log.d("KHJ", "페어링된 장치가 없습니다.")
            }
        } else {
//            toast("블루투스가 비활성화 되어 있습니다.")
            Log.d("KHJ", "블루투스가 비활성화 되어 있습니다.")
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
            toast("connecting..")
            mBluetoothSocket.connect()
            toast("after connecting..")
            mThreadConnectedBluetooth = ConnectedBluetoothThread(mBluetoothSocket, pub)
            mThreadConnectedBluetooth.start()
            toast("starting..")
//            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget()
        } catch (e: IOException) {
//            toast("블루투스 연결 중 오류가 발생했습니다.")
            Log.d("KHJ", "블루투스 연결 중 오류가 발생했습니다.")
            Log.d("KHJ", "$e")
        }
    }

    private class ConnectedBluetoothThread(
        private val mmSocket: BluetoothSocket,
        private val subject: PublishSubject<ByteArray>
    ) : Thread() {
        private lateinit var mmInStream: InputStream
        private lateinit var mmOutStream: OutputStream

        init {
            try {
                mmInStream = mmSocket.inputStream;
                mmOutStream = mmSocket.outputStream;
            } catch (e: IOException) {
//                toast("소켓 연결 중 오류가 발생했습니다.")
                Log.d("KHJ", "소켓 연결 중 오류가 발생했습니다.")
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
                        subject.onNext(buffer)
//                        Log.d("KHJ", bytes.toString(16))
                        Log.d("KHJ", "${buffer.map { it.toUByte() }}")
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
//                toast("데이터 전송 중 오류가 발생했습니다.")
                Log.d("KHJ", "데이터 전송 중 오류가 발생했습니다.")
            }
        }

        fun cancel() {
            try {
                mmInStream.close()
                mmOutStream.close()
                mmSocket.close()
                subject.doOnComplete { Log.d("KHJ", "subject doOnComplete") }
                subject.onComplete()
            } catch (e: IOException) {
//                toast("소켓 해제 중 오류가 발생했습니다.")
                Log.d("KHJ", "소켓 해제 중 오류가 발생했습니다.")
            }
        }
    }

    fun write(str: String) {
        mThreadConnectedBluetooth.write(str)
    }

    fun bluetoothDisconnect() {
        mBluetoothSocket.close()
    }
}
