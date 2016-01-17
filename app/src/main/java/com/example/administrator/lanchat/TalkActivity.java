package com.example.administrator.lanchat;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class TalkActivity extends ListActivity {

    MyNet myNet = new MyNet();
    MyUI myUI = new MyUI(this);
    WifiManager wifiManager;
    List<String> ipList;
    Button btn;
    EditText et;
    List<String> dataList;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        et = (EditText) findViewById(R.id.et);
        btn = (Button) findViewById(R.id.btn);
        dataList = new ArrayList<>();

        //让server线程跑起来
        MainActivity.state = true;

        //获取wifiManager
        wifiManager = myNet.creatWifiManger(this);
        //获取局域网所有ip
        ipList = getLANIp(wifiManager);

        //启动Service接受并显示data
        i = new Intent(TalkActivity.this, MyService.class);
        bindService(i, sc, BIND_AUTO_CREATE);

        //发送按钮
        btn.setOnClickListener(new btnListener());
    }

    //返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainActivity.state = false;
            //1.发送消息告知其他人退出房间
            //2.最主要是让serverSocket退出阻塞状态，并关闭serverSocket
            String data = MainActivity.name + ": 退出频道";
            sendData(data);
            //销毁service,要不然再次进入不能发送消息?????????
            //看来不是service的问题，是线程没有关闭!!!!
            //unbindService(sc);
        }
        return super.onKeyDown(keyCode, event);
    }

    //实现ServiceConnection接口
    ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            MyHandler myHandler = new MyHandler();
            myBinder.getData(myHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //发送按钮Listener
    class btnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //获取消息内容
            String etString = et.getText().toString();
            //消息不为空时可发送
            if (!etString.equals("")) {
                //要发送的数据
                String data = MainActivity.name + ": " + etString;
                System.out.println("send=====================================" + data);
                //UDP发送数据
                sendData(data);
            } else {
                myUI.creatToast("消息不可为空");
            }
            //发送完后清空et
            et.setText("");
        }
    }

    //遍历ip发送UDP数据------Thread
    void sendData(final String data) {
        final byte[] bytes = data.getBytes();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String ip : ipList) {
                    myNet.creatUDPClient(ip, MainActivity.port, bytes);
                    System.out.println("ip------------------" + ip);
                }
            }
        }).start();
    }

    //获取局域网所有ip
    List<String> getLANIp(WifiManager wifiManager) {
        //获取本机ip
        String localAddress = myNet.getLocWifiAddress(wifiManager);
        //获取ip前缀
        String index = myNet.getAddrIndex(localAddress);
        //返回遍历的ip
        return myNet.getSubIp(index);
    }

    //接受子线程传来的string
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("handleMessage===================" + msg.obj.toString());
            updateLV(msg.obj.toString());
        }
    }

    //更新listView
    void updateLV(String str) {
        dataList.add(str);
        System.out.println("updateLV===================" + str);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(TalkActivity.this, android.R.layout.simple_expandable_list_item_1, dataList);
        TalkActivity.this.setListAdapter(arrayAdapter);
        //使listView的最后一行显示于屏幕底部
        TalkActivity.this.setSelection(arrayAdapter.getCount());
    }

}
