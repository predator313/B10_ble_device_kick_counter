package com.example.myb10demo

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minew.beaconplus.sdk.MTCentralManager
import com.minew.beaconplus.sdk.enums.FrameType
import com.minew.beaconplus.sdk.enums.TriggerType
import com.minew.beaconplus.sdk.frames.MinewFrame
import com.minew.beaconplus.sdk.frames.UidFrame
import com.minew.beaconplus.sdk.model.Trigger
import java.util.Date

class B10Service : Service() {
    //important variables
    private var mtCentralManager: MTCentralManager? = null
    private val flag = true
    private var setupdevice = true
    private val count = 0
    private var timestamp = 0.0
    private var timestamp_prev = 0.0
    private var last_time = 0.0
    private val major = 0
    private var trigger: Trigger? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    var wifiMgr: WifiManager? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("b10 scan", "b10 scan service created")
        initTrigger()
        timestamp_prev = Date().time.toDouble()
        last_time = Date().time.toDouble()
        initManager()
        if (kickCounterListener != null) {
            kickCounterListener!!.askPermissions()
        }
        initListener()
        initBroadcastReceiver()
        wifiMgr = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
    }

    //here we are listening to the bluetooth state changes
    //if the bluetooth is turned off then bluetooth_turned_off callback(From KickCounterListener) is given to the registered listeners .
    private fun initBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            mac_address = ""
                            kickCounterListener!!.bluetooth_turned_off()
                            stopSelf()
                            Log.d("broadcast receiver", "broadcast receiver off")
                        }

                        BluetoothAdapter.STATE_TURNING_OFF -> Log.d(
                            "broadcast receiver",
                            "broadcast receiver turning off"
                        )

                        BluetoothAdapter.STATE_ON -> Log.d(
                            "broadcast receiver",
                            "broadcast receiver on"
                        )

                        BluetoothAdapter.STATE_TURNING_ON -> Log.d(
                            "broadcast receiver",
                            "broadcast receiver turning on"
                        )
                    }
                }
            }
        }
    }

    private fun initTrigger() {
        trigger = Trigger()
        trigger!!.condition = 3000
        trigger!!.curSlot = 0
        trigger!!.triggerType = TriggerType.BTN_STAP_EVT
        trigger!!.advInterval = 100
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mac_address = intent.getStringExtra(B10Constants.B10_MAC_ADDRESS)
        initData()
        register_receiver()
        Log.d(
            "b10 scan",
            "b10 scan service on start setupdevice $setupdevice flag $flag timestamp $timestamp timestamp prev $timestamp_prev"
        )
        return START_STICKY
    }

    private fun register_receiver() {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(broadcastReceiver, filter)
    }

    private fun initData() {
        mtCentralManager!!.startScan()
    }

    private fun initManager() {
        mtCentralManager = MTCentralManager.getInstance(this)

        mtCentralManager?.startService()
    }

    private fun initListener() {
        mtCentralManager!!.setMTCentralManagerListener { peripherals ->
            //Log.d("b10 scan", "b10 scan peripheral" );
            val now = Date().time.toDouble()
            Log.d("b10 scan", "b10 scan try catch flag = $flag")
            // if(flag){

            //flag = false;
            //handler.postDelayed(runnable,3000);
            //Log.d("b10 scan","b10 scan try catch major time diff true "+String.valueOf((now-last_time)/1000));
            for (mtPeripheral in peripherals) {
                if (mtPeripheral.mMTFrameHandler.mac.contentEquals(mac_address)) {
                    if (setupdevice) {
                        setupdevice = false
                        counter.postValue("connected")
                    }
                    try {
                        val minewFrames: List<MinewFrame> =
                            mtPeripheral.mMTFrameHandler.advFrames
                        //Log.d("b10 scan","b10 scan try catch adv frames size "+minewFrames.size() );
                        for (minewFrame in minewFrames) {
                            if (minewFrame.frameType == FrameType.FrameUID) {
                                val uidFrame = minewFrame as UidFrame
                                if (uidFrame.instanceId.contentEquals("123456789012")) {
                                    timestamp = Date().time.toDouble()
                                    if (timestamp - last_time >= 3000) {
                                        last_time = timestamp
                                        ///Log.d("b10 scan", "b10 scan try catch major " + String.valueOf(iBeaconFrame.getMajor()) + " " + major + " " + String.valueOf(minewFrame.getCurSlot()) + " timestamp " + String.valueOf(timestamp) + " diff = " + String.valueOf((timestamp - timestamp_prev) / 1000));
                                        counter.postValue(1.toString())
                                        if (kickCounterListener != null) {
                                            kickCounterListener!!.add_kick_count()
                                        }
                                    }
                                    //                                                major = major
                                }

                                //Log.e("b10 scan","b10 scan try catch major "+String.valueOf(iBeaconFrame.getMajor()));
                                uidFrame.setAdvInterval(100)
                                uidFrame.instanceId = "000000000000"
                            } else {
                            }
                            Log.d(
                                "b10 scan",
                                "b10 scan try catch for frame type " + minewFrame.frameType.name + " slot no " + minewFrame.curSlot.toString()
                            )
                        }
                    } catch (e: Exception) {
                        Log.d("b10 scan", "b10 scan try catch exception " + e.message)
                    }
                    Log.e(
                        "b10 scan",
                        "b10 scan peripheral.mac -> " + mtPeripheral.mMTFrameHandler.mac + " " + mtPeripheral.mMTFrameHandler.advFrames.size + " " + count
                    )
                }
            }
            //                }else{
            //                    //Log.d("b10 scan","b10 scan try catch major time diff false "+String.valueOf((now-last_time)/1000));
            //
            //
            //                }
        }
    }

    interface KickCounterListener {
        fun add_kick_count()
        fun bluetooth_turned_off()
        fun askPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("b10 scan", "b10 scan service destroyed")
        mac_address = ""
        unregisterReceiver(broadcastReceiver)
        mtCentralManager!!.stopScan()
        mtCentralManager!!.stopService()
        mtCentralManager = null
        stopSelf()
    }

    companion object {
        //live data objects
        var counter = MutableLiveData<String>()
        var mac_address: String? = "AC:23:3F:AE:2E:B4"

        //Kick counter listener
        var kickCounterListener: KickCounterListener? = null
        fun setonKickCounterListener(kickCounterListener1: KickCounterListener?) {
            kickCounterListener = kickCounterListener1
        }
    }
}
