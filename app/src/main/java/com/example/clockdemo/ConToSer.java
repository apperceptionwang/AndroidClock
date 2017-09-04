package com.example.clockdemo;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by 1234 on 2017/4/14.
 */

public class ConToSer {
    String ip;
    int port;
    Socket socket;
    OutputStream out;
    InputStream is;
    byte[] buf = new byte[1024];
    byte[] rec = new byte[1024];
    ConToSer(String ip,int port){
        try {

            this.ip = ip;
            this.port = port;
            this.socket = new Socket(this.ip, this.port);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            Log.e("aaa", "连接成功");
            out = this.socket.getOutputStream();
            is = this.socket.getInputStream();

        } catch (UnknownHostException e) {
            Log.e("bbb", "连接失败1");

            e.printStackTrace();
        } catch (IOException e) {
            Log.e("bbb", "连接失败2");

            e.printStackTrace();
        }
    }
}
