package com.example.administrator.lanchat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.os.Message;

public class MyService extends Service {

    public MyService() {
    }

    //返回到Activity的对象，返回之后可以上转型对象获得MyBinder，再获得data
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    //自定义Binder-----thread
    class MyBinder extends Binder {
        void getData(TalkActivity.MyHandler myHandler) {
            MyThread myThread = new MyThread(myHandler);
            myThread.start();
        }
    }

    //自定义线程,用来处理server接受消息
    class MyThread extends Thread {
        TalkActivity.MyHandler myHandler;

        public MyThread(TalkActivity.MyHandler myHandler) {
            this.myHandler = myHandler;
        }

        //接受其他ip发来的数据,转换成String
        @Override
        public void run() {
            System.out.println("--------------------------MyThreadSTART");
            //这里是一个死循环,MainActivity.state的为false,可以跳出死循环
            new MyNet().creatUDPServer(MainActivity.port, new MyNet.UDPAction() {
                @Override
                public void getBytes(byte[] bytes) {
                    //第一次用这个构造方法
                    String data = new String(bytes);
                    System.out.println("receive======================" + data);
                    //死循环里用handler传送消息
                    Message msg = myHandler.obtainMessage();
                    msg.obj = data;
                    myHandler.sendMessage(msg);
                }

                @Override
                public boolean closeDs() {
                    return MainActivity.state;
                }
            });
            //线程没结束,这里以后的代码都不会执行
            System.out.println("--------------------------MyThreadEND");
        }

    }

    @Override
    public void onDestroy() {
        System.out.println("--------------------------onDestroy");
        //interrupt不会终止一个正在运行的线程!!!!!
        //myThread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        System.out.println("--------------------------onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}

