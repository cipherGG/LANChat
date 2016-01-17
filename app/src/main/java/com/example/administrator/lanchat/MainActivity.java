package com.example.administrator.lanchat;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //存放端口号/频道
    static int port = 1492;
    //存放用户名称
    static String name = "我是猪";
    //共享变量,用来终止Service里的子线程
    static boolean state = true;
    MyNet myNet = new MyNet();
    MyUI myUI = new MyUI(this);
    Button startTalk, settingChannel, changeName, appIntroduce;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化
        startTalk = (Button) findViewById(R.id.start_talk);
        settingChannel = (Button) findViewById(R.id.setting_channel);
        changeName = (Button) findViewById(R.id.change_name);
        appIntroduce = (Button) findViewById(R.id.app_introduce);

        //wifiManager
        wifiManager = myNet.creatWifiManger(this);

        //加载监听
        settingChannel.setOnClickListener(new SettingChannelListener());
        startTalk.setOnClickListener(new StartTalkListener());
        changeName.setOnClickListener(new ChangeNameListener());
        appIntroduce.setOnClickListener(new AppIntroduceListener());
    }

    //加入群聊
    class StartTalkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                //跳转到聊天界面
                Intent i = new Intent(MainActivity.this, TalkActivity.class);
                MainActivity.this.startActivity(i);
            } else {
                myUI.creatToast("请检查WIFI是否异常");
            }
        }
    }

    //设置端口
    class SettingChannelListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final EditText et = new EditText(MainActivity.this);
            et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            et.setSingleLine();
            //显示默认端口号
            et.setText(String.valueOf(MainActivity.port));
            myUI.creatBaseDialog(null, "频道就是端口", null, et, new MyUI.DialogAction() {
                @Override
                public void lunchAction() {
                }

                @Override
                public void positiveAction() {
                    String str = et.getText().toString();
                    if (!str.equals(""))
                        //设置端口号为输入的数字
                        MainActivity.port = Integer.parseInt(str);
                }
            });
        }
    }

    //更改名称
    class ChangeNameListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final EditText et = new EditText(MainActivity.this);
            et.setSingleLine();
            //显示默认用户名
            et.setText(MainActivity.name);
            myUI.creatBaseDialog(null, "还是不要改了吧", null, et, new MyUI.DialogAction() {
                @Override
                public void lunchAction() {
                }

                @Override
                public void positiveAction() {
                    String str = et.getText().toString();
                    if (!str.equals(""))
                        //设置名称为输入的字符串
                        MainActivity.name = str;
                }
            });
        }
    }

    //软件介绍
    class AppIntroduceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, IntroduceActivity.class);
            MainActivity.this.startActivity(i);
        }
    }

}
