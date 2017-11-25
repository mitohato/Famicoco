package com.example.mito.famicoco;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.mito.famicoco.MainActivity.myBeaconMacAddress;
import static com.google.android.gms.internal.zzs.TAG;

class IBeaconScanCallback extends ScanCallback {      //beaconを検知した時に呼ばれる

    static ArrayList<String> beaconId = new ArrayList<>();
    static Boolean f_init = true;

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        // BLE受信のディレイ（ScanSettings$Builder#setReportDelay）を設定すると
        // こちらが呼び出されました
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        // スキャン結果が返ってきます
        // このメソッドかonBatchScanResultsのいずれかが呼び出されます。
        // 通常はこちらが呼び出されます。
        // beaconを検知したらここが呼ばれるみたいだからこの後に処理書けば良さげ
        ScanRecord mScanRecord = result.getScanRecord();
        assert mScanRecord != null;
        byte[] recordByte = mScanRecord.getBytes();
        if (getUUID(recordByte).equals("E4B404EA-E791-4FBB-B854-3163E3551D9B")) {
            if (f_init) {
                f_init = false;
                myBeaconMacAddress = getMinor(recordByte);
            }
            beaconId.add(getMinor(recordByte));
            Log.d(TAG, getUUID(recordByte));
            Log.d(TAG, getMinor(recordByte));
        }
    }

    /**
     * F8:AC:78:96:28:6A
     * {@inheritDoc}
     */
    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
        // エラーが発生するとこちらが呼び出されます
        String errorMessage = "";
        switch (errorCode) {
            case SCAN_FAILED_ALREADY_STARTED:
                errorMessage = "既にBLEスキャンを実行中です";
                break;
            case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                errorMessage = "BLEスキャンを開始できませんでした";
                break;
            case SCAN_FAILED_FEATURE_UNSUPPORTED:
                errorMessage = "BLEの検索をサポートしていません。";
                break;
            case SCAN_FAILED_INTERNAL_ERROR:
                errorMessage = "内部エラーが発生しました";
                break;
        }
        Log.d(TAG, errorMessage);
    }

    private String getUUID(byte[] scanRecord) {
        return IntToHex2(scanRecord[9] & 0xff)
                + IntToHex2(scanRecord[10] & 0xff)
                + IntToHex2(scanRecord[11] & 0xff)
                + IntToHex2(scanRecord[12] & 0xff)
                + "-"
                + IntToHex2(scanRecord[13] & 0xff)
                + IntToHex2(scanRecord[14] & 0xff)
                + "-"
                + IntToHex2(scanRecord[15] & 0xff)
                + IntToHex2(scanRecord[16] & 0xff)
                + "-"
                + IntToHex2(scanRecord[17] & 0xff)
                + IntToHex2(scanRecord[18] & 0xff)
                + "-"
                + IntToHex2(scanRecord[19] & 0xff)
                + IntToHex2(scanRecord[20] & 0xff)
                + IntToHex2(scanRecord[21] & 0xff)
                + IntToHex2(scanRecord[22] & 0xff)
                + IntToHex2(scanRecord[23] & 0xff)
                + IntToHex2(scanRecord[24] & 0xff);
    }

    private String getMinor(byte[] scanRecord) {
        String hexMinor = IntToHex2(scanRecord[27] & 0xff) + IntToHex2(scanRecord[28] & 0xff);
        return String.valueOf(Integer.parseInt(hexMinor, 16));
    }

    private String IntToHex2(int i) {
        char hex_2[] = {Character.forDigit((i >> 4) & 0x0f, 16), Character.forDigit(i & 0x0f, 16)};
        String hex_2_str = new String(hex_2);
        return hex_2_str.toUpperCase();
    }
}
