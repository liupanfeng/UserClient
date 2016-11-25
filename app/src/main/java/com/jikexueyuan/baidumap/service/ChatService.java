package com.jikexueyuan.baidumap.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.jikexueyuan.baidumap.lis.onLocationCallBackListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatService extends Service {
    private static final String IP="192.168.1.124";
    private static final int PORT=12345;
    private com.jikexueyuan.baidumap.lis.onLocationCallBackListener onLocationCallBackListener;
    Socket socket = null;
    BufferedReader br = null;
    BufferedWriter bw = null;

    public ChatService() {
    }

    public  void setOnLocationCallBackListener(onLocationCallBackListener onLocationCallBackListener) {
        this.onLocationCallBackListener = onLocationCallBackListener;
    }


    @Override
    public IBinder onBind(Intent intent) {
       return new MyBinder();
    }
    @Override
    public void onCreate() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        try {
            socket.close();
            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    public void connect() throws IOException {
        AsyncTask<Void, String, Void> read = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    socket = new Socket(IP, PORT);
                    bw = new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream()));
                    br = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    publishProgress("success");
                    String line;
                    while ((line = br.readLine()) != null) {
                        publishProgress(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(String... values) {
                if (values[0].equals("success")) {
                    return;
                }
                // 此处拿到经纬度 用：分割 纬度在前
                String lat = values[0].split(":")[0];// 纬度
                String lon = values[0].split(":")[1];// 经度
                if(onLocationCallBackListener!=null){
                    onLocationCallBackListener.callBackData(lat,lon);
                }
                super.onProgressUpdate(values);
            }
        };
        read.execute();
    }

    public void send(double longitude, double latitude) {
        try {
            if (!"".equals(latitude) && !"".equals(longitude)&&bw!=null) {
                //为了显示测试效果 经纬度均加上一定的数值
                latitude+=0.000530;
                longitude+=0.000520;
                bw.write(latitude+":"+longitude+"\n");
                bw.flush();
                Thread.sleep(2500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class MyBinder extends Binder{
        public ChatService getService() {
            return ChatService.this;
        }
    }
}
