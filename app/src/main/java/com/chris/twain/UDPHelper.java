package com.chris.twain;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

public class UDPHelper {

    private static UDPHelper Instance;

    public static UDPHelper getInstance() {
        if (Instance == null) {
            Instance = new UDPHelper();
        }
        return Instance;
    }

    private static final String TAG = "UDPHelper";
    private static final int PORT = xxx;
    private static final String HOST = "xxxxxx";
    private final LinkedList<byte[]> VoiceSendList = new LinkedList<>();     // 发送数据队列
    private final LinkedList<byte[]> VoiceReceiveList = new LinkedList<>();  // 接收数据队列
    private Thread TSend;
    private boolean isStart = false;
    private Thread TReceive;

    private DatagramSocket ds;

    private UDPHelper() {
        initUDP();
        isStart = true;
        TSend = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart) {
                    if (VoiceSendList.size() > 0) {
                        byte[] buf = VoiceSendList.removeFirst();
                        send(buf);
                    }
                }
            }
        });
        TSend.start();
        TReceive = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart) {
                    Log.d(TAG, "run: receive  有数据接收，请处理");
                    byte[] buf = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(buf, buf.length);
                    try {
                        ds.receive(dp);
                        byte[] data = dp.getData();
                        int len = dp.getLength();
                        if (len > 0) {
                            // 有数据
                            // 1. 保存数据，添加到接收队列VoiceReceiveList
                            // 2. 通知UI
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        TReceive.start();
    }

    public void send(byte[] buf) {
        if (ds == null) {
            initUDP();
        }
        try {
            DatagramPacket dp = new DatagramPacket(buf, buf.length, InetAddress.getByName(HOST), PORT);
            ds.send(dp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initUDP() {
        try {
            ds = new DatagramSocket(PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        isStart = false;
        if (TSend != null) {
            TSend.interrupt();
            TSend = null;
        }
        if (TReceive != null) {
            TReceive.interrupt();
            TReceive = null;
        }
        if (ds != null) {
            ds.close();
            ds = null;
        }
    }

    // 将音频采集添加和发送分开
    public  void addVoiceData(byte[] buf) {
        synchronized (VoiceSendList) {
            VoiceSendList.add(buf);
        }
    }

}
