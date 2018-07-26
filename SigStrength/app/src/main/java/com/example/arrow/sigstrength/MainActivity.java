package com.example.arrow.sigstrength;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentity;
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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    SignalService signalService ;
    LocationService locationService ;
    Message message = new Message() ;

    String IP = "192.168.1.109";
    int PORT = 3001 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //System.out.println("main thread "+Thread.currentThread().getId());
        signalService = new SignalService(this) ;
        signalService.listen(PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        locationService = new LocationService(this) ;
        locationService.startRequestLocationUpdates();
        //设置界面视图
       // setView();

        sendMessage() ;

    }

    @Override
    protected void onPause() {
        super.onPause();
        //用户不在当前页面时，停止监听。
        signalService.listen(PhoneStateListener.LISTEN_NONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        signalService.listen(PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    protected void setView(){
        ((TextView)findViewById(R.id.tv_signame)).setText(signalService.getSigName());
        ((TextView)findViewById(R.id.tv_sigStrength)).setText(signalService.getSigStrength());
        ((TextView)findViewById(R.id.tv_levelValue)).setText(String.valueOf(signalService.getSigLevel()));
        ((TextView)findViewById(R.id.tv_cidValue)).
                setText(signalService.getCid()==null ? "unknow" : String.valueOf(signalService.getCid()));
        ((TextView)findViewById(R.id.tv_cellSigStrength)).setText(signalService.getCellSigStrength());
    }

    protected void sendMessage(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("strength",signalService.getSigStrength());
                //System.out.println("sub thread "+Thread.currentThread().getId());

                message.setLoc(locationService.getLocation());
                message.setSigType(signalService.getSigName());
                message.setSigStrength(signalService.getSigStrength());
                message.setSigLevel(signalService.getSigLevel());
                message.setCid(signalService.getCid());

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
        },new Date(),5000) ;
    }
}
