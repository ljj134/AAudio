package com.chris.twain;

import android.util.Log;

public enum EchoEngine {

    INSTANCE;

    static {
        System.loadLibrary("echo");
    }

    static native boolean create();

    static native void delete();

    static native void setEchoOn(boolean isEchoOn);

    static native void setRecordingDeviceId(int deviceId);

    static native void setPlaybackDeviceId(int deviceId);

    static native void startRecord(String path);

    static native void stopRecord();

    static native void startPlayer(String path);

    static native void stopPlayer();

    static native void setAudioCallBackObject(AudioCallBack audioCallBack);

    interface AudioCallBack {
        void onAudioData(byte[] data);
    }

    private static AudioCallBack audioCallBack;

    public static void setAudioCallBack(AudioCallBack CallBack) {
        audioCallBack = CallBack;
        setAudioCallBackObject(CallBack);
    }


    // JNI callback method
    public void onAudioData(byte[] data) {
        if (audioCallBack != null) {
            audioCallBack.onAudioData(data);
        }
        processAudioData(data);
    }

    private void processAudioData(byte[] data) {
        // Handle the audio data here
        Log.d("app", "processAudioData: ");
    }
}
