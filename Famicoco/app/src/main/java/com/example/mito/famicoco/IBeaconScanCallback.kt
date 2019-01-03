package com.example.mito.famicoco

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import com.example.mito.famicoco.MainActivity.Companion.myBeaconMacAddress
import java.util.ArrayList
import kotlin.experimental.and

internal class IBeaconScanCallback : ScanCallback() { // beaconを検知した時に呼ばれる
    
    /**
     * {@inheritDoc}
     */
    override fun onScanResult(
            callbackType: Int,
            result: ScanResult
    ) {
        super.onScanResult(
                callbackType,
                result
        )
        // スキャン結果が返ってきます
        // このメソッドかonBatchScanResultsのいずれかが呼び出されます。
        // 通常はこちらが呼び出されます。
        // beaconを検知したらここが呼ばれるみたいだからこの後に処理書けば良さげ
        val mScanRecord = result.scanRecord ?: return
        val recordByte = mScanRecord.bytes
        if (getUUID(recordByte) == "E4B404EA-E791-4FBB-B854-3163E3551D9B") {
            if (f_init) {
                f_init = false
                myBeaconMacAddress = getMinor(recordByte)
            }
            beaconIds.add(getMinor(recordByte))
        }
    }
    
    /**
     * F8:AC:78:96:28:6A
     * {@inheritDoc}
     */
    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        // エラーが発生するとこちらが呼び出されます
        val errorMessage = when (errorCode) {
            SCAN_FAILED_ALREADY_STARTED -> "既にBLEスキャンを実行中です"
            SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "BLEスキャンを開始できませんでした"
            SCAN_FAILED_FEATURE_UNSUPPORTED -> "BLEの検索をサポートしていません。"
            SCAN_FAILED_INTERNAL_ERROR -> "内部エラーが発生しました"
            else -> ""
        }
        
        Log.d("scan failed", errorMessage)
    }
    
    private fun getUUID(scanRecord: ByteArray): String {
        return (intToHex2(scanRecord[9] and 0xff.toByte()) +
                intToHex2(scanRecord[10] and 0xff.toByte()) +
                intToHex2(scanRecord[11] and 0xff.toByte()) +
                intToHex2(scanRecord[12] and 0xff.toByte()) +
                "-" +
                intToHex2(scanRecord[13] and 0xff.toByte()) +
                intToHex2(scanRecord[14] and 0xff.toByte()) +
                "-" +
                intToHex2(scanRecord[15] and 0xff.toByte()) +
                intToHex2(scanRecord[16] and 0xff.toByte()) +
                "-" +
                intToHex2(scanRecord[17] and 0xff.toByte()) +
                intToHex2(scanRecord[18] and 0xff.toByte()) +
                "-" +
                intToHex2(scanRecord[19] and 0xff.toByte()) +
                intToHex2(scanRecord[20] and 0xff.toByte()) +
                intToHex2(scanRecord[21] and 0xff.toByte()) +
                intToHex2(scanRecord[22] and 0xff.toByte()) +
                intToHex2(scanRecord[23] and 0xff.toByte()) +
                intToHex2(scanRecord[24] and 0xff.toByte()))
    }
    
    private fun getMinor(scanRecord: ByteArray): String {
        val hexMinor = intToHex2(scanRecord[27] and 0xff.toByte()) +
                intToHex2(scanRecord[28] and 0xff.toByte())
        return Integer.parseInt(
                hexMinor,
                16
        ).toString()
    }
    
    private fun intToHex2(i: Byte): String {
        val j = i.toInt()
        val hex2 = charArrayOf(
                Character.forDigit(
                        j shr 4 and 0x0f,
                        16
                ),
                Character.forDigit(
                        j and 0x0f,
                        16
                )
        )
        return String(hex2).toUpperCase()
    }
    
    companion object {
        var beaconIds = ArrayList<String>()
        var f_init: Boolean = true
    }
}
