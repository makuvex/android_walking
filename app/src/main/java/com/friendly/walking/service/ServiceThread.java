package com.friendly.walking.service;

import android.os.Handler;

/**
 * Created by Administrator on 2017-10-22.
 */

public class ServiceThread extends Thread {
    private static final int        TIME_INTERVAL = 3000;

    private Handler                 handler;
    private boolean                 isRun = true;

    public ServiceThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        while(isRun){
            handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
            try{
                Thread.sleep(TIME_INTERVAL); //10초씩 쉰다.
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
