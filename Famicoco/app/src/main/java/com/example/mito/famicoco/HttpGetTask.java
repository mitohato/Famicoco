package com.example.mito.famicoco;

import android.os.AsyncTask;
import android.util.Log;

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

            // ローカル処理
            // コネクション取得
            con = (HttpURLConnection) url.openConnection();
            con.connect();

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

        } catch (IOException e1) {
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
        StringBuilder sb = new StringBuilder();
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
