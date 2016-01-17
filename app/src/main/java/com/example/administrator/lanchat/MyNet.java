package com.example.administrator.lanchat;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MyNet {

    public MyNet() {
    }

    //UDPClient
    void creatUDPClient(String ip, int dstPort, byte[] data) {
        DatagramSocket ds = null;
        try {
            //一定要记住啊client不要端口啊，要也不要和server的端口一样啊
            ds = new DatagramSocket();
            InetAddress ia = InetAddress.getByName(ip);
            DatagramPacket dp = new DatagramPacket(data, data.length, ia, dstPort);
            ds.send(dp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ds != null)
                ds.close();
        }
    }

    //UDPServer
    void creatUDPServer(int dstPort, UDPAction udpAction) {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(dstPort);
            //注意这里是死循环,接口事务必须在循环里处理
            while (true) {
                //前两行最好也放入死循环,要不然dp里的value显示有问题，长度问题
                byte[] data = new byte[1024];
                DatagramPacket dp = new DatagramPacket(data, data.length);
                ds.receive(dp);
                udpAction.getBytes(dp.getData());
                //必须先发送一个数据,让ds不在阻塞,代码才会执行到这里
                if (!udpAction.closeDs())
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ds != null)
                ds.close();
        }
    }

    //UDPAction
    interface UDPAction {
        void getBytes(byte[] bytes);

        //最好定义一个共享变量,用来关闭ds,
        boolean closeDs();
    }

    //获取WIFIManger
    WifiManager creatWifiManger(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    //获取LAN本机IP
    String getLocWifiAddress(WifiManager wifiManager) {
        WifiInfo wifiinfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiinfo.getIpAddress();
        System.out.println("getLocWifiAddress----------------" + intToIp(ipAddress));
        return intToIp(ipAddress);
    }

    //获取IP前缀
    String getAddrIndex(String locAddress) {
        if (!locAddress.equals("")) {
            System.out.println("getLocAddrIndex----------------" + locAddress.substring(0, locAddress.lastIndexOf(".") + 1));
            return locAddress.substring(0, locAddress.lastIndexOf(".") + 1);
        }
        return null;
    }

    //将intIP转化为stringIP
    String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    //遍历LAN里的256个ip
    List<String> getSubIp(String addressIndex) {
        List<String> list = new ArrayList<>();
        int j;
//        for (int i = 0; i < 256; i++)
        for (int i = 1; i < 255; i++) {
            j = i;
            String current_ip = addressIndex + j;
            list.add(current_ip);
        }
        return list;
    }

}
