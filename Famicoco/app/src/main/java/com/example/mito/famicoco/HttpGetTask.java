package com.example.mito.famicoco;

/**
 * Created by mi151307 on 2016/09/07.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

final class HttpGetTask extends AsyncTask<URL, Void, Boolean> {

    @Override
    protected Boolean doInBackground(URL[] values) {
        HttpURLConnection con = null;
        try {
            // アクセス先URL
            final URL url = values[0];
            // 出力ファイルフルパス
            //final String filePath = "/xxxx/xxxx/xxxx/filename.xxx";

            // ローカル処理
            // コネクション取得
            con = (HttpURLConnection) url.openConnection();
            con.connect();
            InputStream in = con.getInputStream();
            String readSt = readInputStream(in);


            JSONObject jsonData = new JSONObject(readSt).getJSONObject("オブジェクト名");
            // 配列を取得する場合
            JSONArray jsonArray = new JSONObject(readSt).getJSONArray("オブジェクト名");

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                Log.d("connection", "perfect win");
                // 通信に成功した
                // ファイルのダウンロード処理を実行
                // 読み込み用ストリーム
                final InputStream input = con.getInputStream();
                final DataInputStream dataInput = new DataInputStream(input);
                // 書き込み用ストリーム
                //final FileOutputStream fileOutput = new FileOutputStream(filePath);
                //final DataOutputStream dataOut = new DataOutputStream(fileOutput);
                // 読み込みデータ単位
                final byte[] buffer = new byte[4096];
                // 読み込んだデータを一時的に格納しておく変数
//                int readByte = 0;

                // ファイルを読み込む
                //while((readByte = dataInput.read(buffer)) != -1) {
                //    dataOut.write(buffer, 0, readByte);
                //}
                // 各ストリームを閉じる
                dataInput.close();
                //fileOutput.close();
                dataInput.close();
                input.close();
                // 処理成功
                return true;
            }

        } catch (IOException | JSONException e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }
        return false;
    }

    static String readInputStream(InputStream in) throws IOException {
        StringBuffer sb = new StringBuffer();
        String st;

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while ((st = br.readLine()) != null) {
            sb.append(st);
        }
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
