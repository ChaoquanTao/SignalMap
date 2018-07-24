package com.example.arrow.sigstrength;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SignalService {
    private TelephonyManager telephonyManager;
    private Context context ;

    public SignalService(Context context) {
        this.context = context ;
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //监听器类
        /*得到信号的强度由每个tiome供应商,有更新*/
        @Override
        public void onSignalStrengthsChanged(final SignalStrength signalStrength) {
            Log.d("Lag1", "signal changed");
            super.onSignalStrengthsChanged(signalStrength);

            final Message message = new Message();

            String[] parts = signalStrength.toString().split(" ");
            String sigName ;
            String sigStrength ;
            switch (telephonyManager.getNetworkType()){
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    sigName="CDMA(dbm)" ;
                    sigStrength = parts[3] ;
                    break;

                case TelephonyManager.NETWORK_TYPE_EDGE:
                    sigName="GSM/EDGE" ;
                    sigStrength = parts[1] ;
                    break;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    sigName="Lte rsrp(dbm)" ;
                    sigStrength = parts[9] ;
                    break;

                default:
                    sigName="unknown" ;
                    sigStrength="unknown" ;
                    break;
            }
            ((TextView)findViewById(R.id.tv_signame)).setText(sigName);
            ((TextView)findViewById(R.id.tv_sigStrength)).setText(sigStrength);
            ((TextView)findViewById(R.id.tv_levelValue)).setText(String.valueOf(signalStrength.getLevel()));

            message.setSigType(sigName);
            message.setSigStrength(Integer.parseInt(sigStrength));
            message.setSigLevel(signalStrength.getLevel());

            //请求获取权限
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},100);
                //return;
            }

            //不知是安卓版本还是什么原因，cellInfo有时候获取不到
            List<CellInfo> list = telephonyManager.getAllCellInfo();
            CellIdentityLte cid =null;
            if(list!=null) {
                for (int i = 0; i < list.size(); i++) {
                    //System.out.println("list "+i+" "+list.get(i));;
                    Log.d("cellinfo", String.valueOf(list.get(i)));
                }

                cid = ((CellInfoLte)list.get(0)).getCellIdentity() ;
                Log.d("cellid", cid.toString());
                ((TextView) findViewById(R.id.tv_cidValue)).setText(cid.toString());
                ((TextView) findViewById(R.id.tv_cellSigStrength)).setText(((CellInfoLte) list.get(0)).getCellSignalStrength().toString());
            }
            else
                Log.e("cellInfo","---couldn't get CellInfo---") ;

            if(list!=null)
                message.setCid(cid);

            //设置定时器向服务端发送数据
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {

                        Socket socket = new Socket(IP,PORT) ;
                        //socket.getOutputStream().write('t');
                        Writer writer = new OutputStreamWriter(socket.getOutputStream()) ;
                        String content = "abc" ;
                        writer.write(new Gson().toJson(message)+'\n');
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },5000);
        }

    }
}
