package com.example.mito.famicoco

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult

import java.util.ArrayList

import com.example.mito.famicoco.MainActivity.myBeaconMacAddress

internal class IBeaconScanCallback : ScanCallback() {      //beaconを検知した時に呼ばれる

    override fun onBatchScanResults(results: List<ScanResult>) {
        super.onBatchScanResults(results)
        // BLE受信のディレイ（ScanSettings$Builder#setReportDelay）を設定すると
        // こちらが呼び出されました
    }

    /**
     * {@inheritDoc}
     */
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        // スキャン結果が返ってきます
        // このメソッドかonBatchScanResultsのいずれかが呼び出されます。
        // 通常はこちらが呼び出されます。
        // beaconを検知したらここが呼ばれるみたいだからこの後に処理書けば良さげ
        val mScanRecord = result.scanRecord!!
        val recordByte = mScanRecord.bytes
        if (getUUID(recordByte) == "E4B404EA-E791-4FBB-B854-3163E3551D9B") {
            if (f_init!!) {
                f_init = false
                myBeaconMacAddress = getMinor(recordByte)
            }
            beaconId.add(getMinor(recordByte))
        }
    }

    /**
     * F8:AC:78:96:28:6A
     * {@inheritDoc}
     */
    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        // エラーが発生するとこちらが呼び出されます
        var errorMessage = ""
        when (errorCode) {
            ScanCallback.SCAN_FAILED_ALREADY_STARTED -> errorMessage = "既にBLEスキャンを実行中です"
            ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> errorMessage = "BLEスキャンを開始できませんでした"
            ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> errorMessage = "BLEの検索をサポートしていません。"
            ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> errorMessage = "内部エラーが発生しました"
        }
    }

    private fun getUUID(scanRecord: ByteArray): String {
        return (IntToHex2(scanRecord[9] and 0xff)
                + IntToHex2(scanRecord[10] and 0xff)
                + IntToHex2(scanRecord[11] and 0xff)
                + IntToHex2(scanRecord[12] and 0xff)
                + "-"
                + IntToHex2(scanRecord[13] and 0xff)
                + IntToHex2(scanRecord[14] and 0xff)
                + "-"
                + IntToHex2(scanRecord[15] and 0xff)
                + IntToHex2(scanRecord[16] and 0xff)
                + "-"
                + IntToHex2(scanRecord[17] and 0xff)
                + IntToHex2(scanRecord[18] and 0xff)
                + "-"
                + IntToHex2(scanRecord[19] and 0xff)
                + IntToHex2(scanRecord[20] and 0xff)
                + IntToHex2(scanRecord[21] and 0xff)
                + IntToHex2(scanRecord[22] and 0xff)
                + IntToHex2(scanRecord[23] and 0xff)
                + IntToHex2(scanRecord[24] and 0xff))
    }

    private fun getMinor(scanRecord: ByteArray): String {
        val hexMinor = IntToHex2(scanRecord[27] and 0xff) + IntToHex2(scanRecord[28] and 0xff)
        return Integer.parseInt(hexMinor, 16).toString()
    }

    private fun IntToHex2(i: Int): String {
        val hex_2 = charArrayOf(Character.forDigit(i shr 4 and 0x0f, 16), Character.forDigit(i and 0x0f, 16))
        val hex_2_str = String(hex_2)
        return hex_2_str.toUpperCase()
    }

    companion object {

        var beaconId = ArrayList<String>()
        var f_init: Boolean? = true
    }
}
