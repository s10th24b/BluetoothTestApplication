package com.example.bluetoothtestapplication

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.HandlerThread
import android.os.SystemClock
import android.util.Log
import android.util.TimeUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.zakaprov.rxbluetoothadapter.ConnectionState
import com.github.zakaprov.rxbluetoothadapter.RxBluetoothAdapter
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.ByteArrayInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.time.seconds

class RxBluetoothAdapterViewModel(application: Application) : AndroidViewModel(application) {
    val data = MutableLiveData<String>("null")
    val bluetoothState = MutableLiveData<String>("null")
    val connectionState = MutableLiveData<Int>(-1)
    val scanComplete = MutableLiveData<Boolean>(true)
    val pairable = MutableLiveData<Boolean>(false)
    val adapter by lazy { RxBluetoothAdapter(application) }
    var mDevice: BluetoothDevice? = null
    var mSocket: BluetoothSocket? = null
    val bluetoothDeviceList = mutableListOf<BluetoothDevice>()

    init {
        val connectionDisposable = adapter.connectionEventStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe({ (state, device) ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        Log.d("KHJ", "${device.address} - connected")
                        connectionState.value = 1
                    }
                    ConnectionState.CONNECTING -> {
                        Log.d("KHJ", "${device.address} - connecting")
                        connectionState.value = 0
                    }
                    ConnectionState.DISCONNECTED -> {
                        Log.d("KHJ", "${device.address} - disconnected")
                        connectionState.value = -1
                    }
                }
            }) { error ->
                Log.e("KHJ", "error in connectionState $error")
            }
    }


    fun bluetoothOn() {
    }

    fun bluetoothOff() {

    }

    fun scan() {
        val disposable = adapter.startDeviceScan()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ device ->
                // Process next discovered device
                scanComplete.value = false
                Log.d(
                    "KHJ",
                    "device: ${device.address} ${device.name} ${device.type} ${device.bondState}"
                )
                if (device !in bluetoothDeviceList) bluetoothDeviceList.add(device)
                if (pairable.value == false && device.name == "HC-06") pairable.value = true
            }, { error ->
                Log.e("KHJ", "error: $error")
                scanComplete.value = true
                // Handle error
            }, {
                Log.d("KHJ", "Scan Complete.")
                scanComplete.value = true
                // Scan complete
            }
            )
    }

    fun pair() {
        Log.d("KHJ", "bluetoothDeviceList: $bluetoothDeviceList")
        mDevice = bluetoothDeviceList.find { it.name == "HC-06" }
        val device = mDevice
        if (device != null) {
            Log.d("KHJ", "pairing with HC-06...")
            val disposable = adapter.pairDevice(device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    // Process pairing result
                    Log.d("KHJ", "pairing result: $result")
                }, { error ->
                    // Handle error
                    Log.e("KHJ", "error in pairing: $error")
                })
        } else {
            Log.e("KHJ", "in pair() device is null")
        }
    }

    fun connect() {
        val device = mDevice
        if (device != null) {
            val disposable = adapter.connectToDevice(device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ socket ->
                    // Connection successful, save and/or use the obtained socket object
                    Log.d("KHJ", "Connection Successful. socket: $socket")
                    mSocket = socket
                    val subject = PublishSubject.create<ByteArray>()
                    val disposable = subject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ byteArray ->
                            val byteList = byteArray.take(8).map { it.toUByte() }
                            data.postValue(byteList.toString())
                            Log.d("KHJ", "$byteList")
                        }) { error ->
                            Log.e("KHJ", "Error in byteArraySubject: $error")
                            data.postValue("null")
                        }
                    ConnectedBluetoothThread(socket, subject).start()

/*
                    // Studying how to observe socket's InputStream in real-time
                    PublishSubject.fromArray(socket.inputStream)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            val buffer = ByteArray(8)
                            val bytes = socket.inputStream.available()
                            it.read(buffer, 0, bytes)
                            Log.d("KHJ", "socket.inputStream buffer: ${buffer.toList()}")
                        }, { error ->
                            Log.e("KHJ", "error in socket.inputstream observable: $error")
                        }, {
                            Log.d("KHJ", "socket.inputStream Completed")
                        })
*/
                }, { error ->
                    // Connection failed, handle the error
                    Log.e("KHJ", "Connection failed. error: $error")
                }
                )
        } else {
            Log.e("KHJ", "in connect() device is null")
        }
    }

    class ConnectedBluetoothThread(
        private val socket: BluetoothSocket,
        private val subject: PublishSubject<ByteArray>
    ) : Thread() {
        val inputStream by lazy { socket.inputStream }
        val outputStream by lazy { socket.outputStream }
        override fun run() {
            val buffer = ByteArray(16)
            var bytes: Int
            while (true) {
                try {
                    bytes = inputStream.available()
                    if (bytes != 0) {
//                        SystemClock.sleep(500) // 500으로 하면 데이터가 8이 아니라 16개가 들어옴.
                        SystemClock.sleep(400)
                        bytes = inputStream.available()
//                        Log.d("KHJ","bytes: $bytes")
                        bytes = inputStream.read(buffer, 0, bytes)
//                        val sdf = SimpleDateFormat("hh:mm:ss:SS")
//                        Log.d("KHJ", "time: ${sdf.format(System.currentTimeMillis())}")
                        subject.onNext(buffer)
//                        Log.d("KHJ", bytes.toString(16))
//                        Log.d("KHJ", "${buffer.map { it.toUByte() }}")
                    }
                } catch (e: IOException) {
                    inputStream.close()
                    outputStream.close()
                    socket.close()
                    subject.onError(e)
                    break
                }
            }
        }
    }


    fun disconnect() {
        mSocket?.outputStream?.close()
        mSocket?.inputStream?.close()
        mSocket?.close()
    }
}