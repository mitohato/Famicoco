package com.example.mito.famicoco

import android.os.AsyncTask
import android.util.Log

import java.io.BufferedReader
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

internal class HttpGetTask : AsyncTask<URL, Void, Boolean>() {

    override fun doInBackground(values: Array<URL>): Boolean {
        var con: HttpURLConnection? = null
        try {
            // アクセス先URL
            val url = values[0]
            // 出力ファイルフルパス

            // ローカル処理
            // コネクション取得
            con = url.openConnection() as HttpURLConnection
            con.connect()

            // HTTPレスポンスコード
            val status = con.responseCode
            if (status == HttpURLConnection.HTTP_OK) {
                Log.d("connection", "perfect win")
                // 通信に成功した
                // ファイルのダウンロード処理を実行
                // 読み込み用ストリーム
                val input = con.inputStream
                val dataInput = DataInputStream(input)
                // 書き込み用ストリーム
                //final FileOutputStream fileOutput = new FileOutputStream(filePath);
                //final DataOutputStream dataOut = new DataOutputStream(fileOutput);
                // 読み込みデータ単位
                // 読み込んだデータを一時的に格納しておく変数
                //                int readByte = 0;

                // ファイルを読み込む
                //while((readByte = dataInput.read(buffer)) != -1) {
                //    dataOut.write(buffer, 0, readByte);
                //}
                // 各ストリームを閉じる
                dataInput.close()
                //fileOutput.close();
                dataInput.close()
                input.close()
                // 処理成功
                return true
            }

        } catch (e1: IOException) {
            e1.printStackTrace()
        } finally {
            con?.disconnect()
        }
        return false
    }

    companion object {

        @Throws(IOException::class)
        fun readInputStream(`in`: InputStream): String {
            val sb = StringBuilder()

            val br = BufferedReader(InputStreamReader(`in`, "UTF-8"))
                sb.append(br.readLine())
            try {
                `in`.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return sb.toString()
        }
    }
}
