package com.example.myb10demo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myb10demo.B10Service.Companion.mac_address
import com.example.myb10demo.adapter.MyAdapter
import com.example.myb10demo.databinding.ActivityMainBinding
import com.minew.beaconplus.sdk.MTCentralManager
import com.minew.beaconplus.sdk.MTPeripheral
import com.minew.beaconplus.sdk.enums.FrameType
import com.minew.beaconplus.sdk.frames.MinewFrame
import com.minew.beaconplus.sdk.frames.UidFrame
import java.util.Date


class MainActivity : AppCompatActivity() {
    private lateinit var mtCentralManager: MTCentralManager
    private lateinit var binding: ActivityMainBinding
    private var is_all_permissions_given = false
    private lateinit var myadapter:MyAdapter
    private val mac="AC:23:3F:AE:2E:B4"
    private var helper:Boolean=false
    private val counter=MutableLiveData<Int>(0)
    private var ctr=0
    private val helperlist= mutableListOf<MTPeripheral>()
    private var timestamp = 0.0
    private var last_time = 0.0
    private val devicesList = mutableListOf<MTPeripheral>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myadapter= MyAdapter()
        get_required_permissions()
        mtCentralManager=MTCentralManager.getInstance(this)
//        Log.d("hello",mtCentralManager.toString()+"mtcentralManager")
//        mtCentralManager.setBluetoothChangedListener {
//            mtCentralManager.startScan()
//        }
        setupRecyclerView()
        myadapter.setOnItemClickListener {
//            Toast.makeText(this@MainActivity, it.address, Toast.LENGTH_SHORT).show()
//            connectToDevice(it)
            Toast.makeText(this@MainActivity,"Successfully Connected to B10 Device",Toast.LENGTH_SHORT).show()
            binding.rvMain.visibility = View.GONE
            binding.btnScan.visibility=View.GONE
            devicesList.clear()
            helper=true
//            binding.pRate.visibility = View.VISIBLE
//            myGattCallback.sendStartMeasurementCommand(bluetoothGatt)
        }


//        mtCentralManager.setMTCentralManagerListener { peripherals ->
//            for (mtPeripheral in peripherals) {
//                // get FrameHandler of a device.
//                val mtFrameHandler = mtPeripheral.mMTFrameHandler
//                val mac = mtFrameHandler.mac //mac address of device
//                val name = mtFrameHandler.name // name of device
//                val battery = mtFrameHandler.battery //battery
//                val rssi = mtFrameHandler.rssi //rssi
//                // all data frames of device（such as:iBeacon，UID，URL...）
//                val advFrames = mtFrameHandler.advFrames
//                Log.d("hello",mac.toString()+"-----mac address")
//                Log.d("hello",name.toString()+"----name")
//                Log.d("hello",battery.toString()+"---battery")
//                Log.d("hello",rssi.toString()+"---rsi")
//                if(mac=="AC:23:3F:AE:2E:B4"){
//                    devicesList.add(mtPeripheral)
//                    ctr++
//                    Log.d("hello","$ctr current value of counter")
//                    binding.etSys.text=ctr.toString()
//                }
//
//            }
//        }
        incCounter()
//        val btn = findViewById<Button>(R.id.scanButton)
        binding.btnScan.setOnClickListener {
//            devicesList.clear() // Clear the previous devices
//            myadapter.bles = devicesList // Update the RecyclerView with an empty list (clears the previous devices from the UI)
//            myadapter.notifyDataSetChanged()
            mtCentralManager.startScan() // Start scanning for new devices
            Log.d("hello", "Scan started")
//            myadapter.bles = devicesList // Update the RecyclerView with the newly scanned devices
//            myadapter.notifyDataSetChanged()
//            Log.d("hello","${devicesList.size}+ device list size ")
            binding.btnScan.visibility=View.GONE
//            incCounter()
//
//            // Use Handler to stop scanning after 10 seconds
//            val handler = Handler()
//            handler.postDelayed({
//                mtCentralManager.stopScan()
//                Log.d("hello", "Scan stopped")
////                myadapter.bles = devicesList // Update the RecyclerView with the newly scanned devices
////                myadapter.notifyDataSetChanged()
//                Log.d("hello", devicesList.size.toString() + " device list size")
//            }, 100000000000) // 10000 milliseconds = 10 seconds
        }
//        if(helper)incCounter()
        Log.d("hello",helper.toString()+"boolean")
//        while(helper){
//            //means we came inside
//            Log.d("hello","live from helper while loop")
//            //now activite the scan for the infinite period
//            mtCentralManager.startScan()
//            mtCentralManager.setMTCentralManagerListener {
//                for(ele in it){
//                    if(ele.mMTFrameHandler.mac=="AC:23:3F:AE:2E:B4"){
//                        helperlist.add(ele)
//                    }
//                }
//            }
//            binding.etSys.text=helperlist.size.toString()
//        }




    }
    private fun get_required_permissions() {
        val requestPermissions: Array<String>
        requestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //Toast.makeText(getContext(), "one", Toast.LENGTH_SHORT).show();
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            //Toast.makeText(getContext(), "two", Toast.LENGTH_SHORT).show();
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        requestPermissions(requestPermissions, PermissionRequestCodes.ALL)
    }
    @SuppressLint("MissingPermission")
    private fun turn_on_bluetooth() {
        val intentBtEnabled = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        // The REQUEST_ENABLE_BT constant passed to startActivityForResult() is a locally defined integer (which must be greater than 0), that the system passes back to you in your onActivityResult()
        // implementation as the requestCode parameter.
        startActivityForResult(intentBtEnabled, PermissionRequestCodes.REQUEST_ENABLE_BLUETOOTH)
    }

    private fun turnongps() {
        val lm = ContextCompat.getSystemService<LocationManager>(
            this,
            LocationManager::class.java
        )
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!gps_enabled || !network_enabled) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, id ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        PermissionRequestCodes.REQUEST_ENABLE_GPS
                    )
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                    turnongps()
                }
            val alert = builder.create()
            alert.show()
        } else {
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //we are checking if all permissions are granted
        //only if all permissions are granted we check for bluetooth
        if (requestCode == PermissionRequestCodes.ALL) {
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    is_all_permissions_given = false
                    get_required_permissions()
                } else {
                    is_all_permissions_given = true
                }
            }
            //only if all permissions are granted we check for bluetooth
            //if bluetooth is off we ask the user to turn on bluetooth.
            //if it is on we ask the user to turn on gps .
            if (is_all_permissions_given) {
               val bluetoothManager =
                    this.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
               val bluetoothAdapter = bluetoothManager.getAdapter()
                if (!bluetoothAdapter.isEnabled()) {
                    turn_on_bluetooth()
                } else {
                    turnongps()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //this block checks result for bluetooth
        //if bluetooth is enabled we request the user to enable gps .
        //if denied we request the user to turn on bluetooth again
        if (requestCode == PermissionRequestCodes.REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                turnongps()
            } else {
                turn_on_bluetooth()
            }
        }

        //this block checks result for gps
        //here we check if the user turned on GPS
        //if he didn't we prompt the user to turn on gps again .
        if (requestCode == PermissionRequestCodes.REQUEST_ENABLE_GPS) {
            if (resultCode == RESULT_OK) {
                turnongps()
            } else {
                turnongps()
            }
        }
    }
    private fun setupRecyclerView() {
//        BleAdapter = MyAdapter()
        binding.rvMain.apply {
            adapter = myadapter
            layoutManager = LinearLayoutManager(this@MainActivity)

        }


    }
    private fun incCounter() {
        mtCentralManager.setMTCentralManagerListener { peripherals ->

            for (mtPeripheral in peripherals) {
                if (mtPeripheral.mMTFrameHandler.mac.contentEquals(mac_address)) {

                    try {
                        val minewFrames: List<MinewFrame> =
                            mtPeripheral.mMTFrameHandler.advFrames
                        //Log.d("b10 scan","b10 scan try catch adv frames size "+minewFrames.size() );
                        for (minewFrame in minewFrames) {
                            if (minewFrame.frameType == FrameType.FrameUID) {
                                val uidFrame = minewFrame as UidFrame
                                if (uidFrame.instanceId!!.contentEquals("123456789012")) {
                                    timestamp = Date().time.toDouble()
                                    if (timestamp - last_time >= 2000) {
                                        last_time = timestamp
                                        ///Log.d("b10 scan", "b10 scan try catch major " + String.valueOf(iBeaconFrame.getMajor()) + " " + major + " " + String.valueOf(minewFrame.getCurSlot()) + " timestamp " + String.valueOf(timestamp) + " diff = " + String.valueOf((timestamp - timestamp_prev) / 1000));
                                        ctr++
                                        binding.etSys.text=ctr.toString()
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

                }
            }
            //                }else{
            //                    //Log.d("b10 scan","b10 scan try catch major time diff false "+String.valueOf((now-last_time)/1000));
            //
            //
            //                }
        }
    }
//

}