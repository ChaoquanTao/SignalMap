package com.example.arrow.sigstrength;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    TelephonyManager telephonyManager;
    MyPhoneStateListener myListener;

    String IP = "192.168.1.101";
    String PORT = "3000" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myListener = new MyPhoneStateListener();

        telephonyManager.listen(myListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //用户不在当前页面时，停止监听。
        telephonyManager.listen(myListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        telephonyManager.listen(myListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //监听器类
        /*得到信号的强度由每个tiome供应商,有更新*/
        @Override
        public void onSignalStrengthsChanged(final SignalStrength signalStrength) {
            String signalName ;
            Log.d("Lag1", "signal changed");
            super.onSignalStrengthsChanged(signalStrength);

            Log.d("networktype", String.valueOf(telephonyManager.getNetworkType()));
            String[] parts = signalStrength.toString().split(" ");
            //Log.d("hint", "here is context");
            for (int i = 0; i < parts.length; i++) {
                Log.d(String.valueOf(i), parts[i]);
            }

            switch (telephonyManager.getNetworkType()){
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    ((TextView)findViewById(R.id.tv_signame)).setText("CDMA Dbm ");
                    ((TextView)findViewById(R.id.tv_sigStrength)).setText(parts[3]);
                    break;

                case TelephonyManager.NETWORK_TYPE_EDGE:
                    ((TextView)findViewById(R.id.tv_signame)).setText("GSM/EDGE ");
                    ((TextView)findViewById(R.id.tv_sigStrength)).setText(parts[1]);
                    break;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    ((TextView)findViewById(R.id.tv_signame)).setText("Lte rsrp(dbm): ");
                    ((TextView)findViewById(R.id.tv_sigStrength)).setText(String.valueOf(parts[9]));
                    break;

                 default:
                     break;
            }

            ((TextView)findViewById(R.id.tv_levelValue)).setText(String.valueOf(signalStrength.getLevel()));

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},100);
                //return;
            }
            List<CellInfo> list = telephonyManager.getAllCellInfo();
            if(list!=null) {
                //System.out.println("===================================================="+list);
                Log.d("----", String.valueOf(list));
                for (int i = 0; i < list.size(); i++) {
                    //System.out.println("list "+i+" "+list.get(i));;
                    Log.d("cellinfo", String.valueOf(list.get(i)));
                }
                Log.d("cellid", ((CellInfoLte) list.get(0)).getCellIdentity().toString());
                ((TextView) findViewById(R.id.tv_cidValue)).setText(((CellInfoLte) list.get(0)).getCellIdentity().toString());
                ((TextView) findViewById(R.id.tv_cellSigStrength)).setText(((CellInfoLte) list.get(0)).getCellSignalStrength().toString());
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket(IP,3001) ;
                        //socket.getOutputStream().write('t');
                        Writer writer = new OutputStreamWriter(socket.getOutputStream()) ;
                        String content = "abc" ;
                        writer.write(content+'\n');
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },5000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 100: {
                // 授权被允许
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("-------->", "授权请求被允许");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e("-------->", "授权请求被拒绝");
                }
                return;
            }
        }
    }

}
