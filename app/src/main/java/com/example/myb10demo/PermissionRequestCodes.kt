package com.example.myb10demo

class PermissionRequestCodes {
    //last_integer is for the reference for the developer
    //please update this integer everytime when you add new request codes in this class so that the next time you can use
    //last_integer + 1 for request code
    var last_integer = 10

    companion object {
        var ALL = 10
        var AUDIO_RECORD_REQUEST_CODE = 1
        var BLUETOOTH_SCAN_REQUEST_CODE = 2
        var BLUETOOTH_CONNECT_REQUEST_CODE = 3
        var REQUEST_ENABLE_BLUETOOTH = 8
        var ACCESS_FINE_LOCATION_REQUEST_CODE = 4
        var ACCESS_COARSE_LOCATION_REQUEST_CODE = 5
        var REQUEST_ENABLE_GPS = 9
        var READ_EXTERNAL_STORAGE_REQUEST_CODE = 6
        var WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 7
    }
}
