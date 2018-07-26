package com.example.arrow.sigstrength;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.RequiresApi;
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

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class SignalService {
    private TelephonyManager telephonyManager;
    private Context context ;
    private MyPhoneStateListener listener ;

    private String sigName ;
    private String sigStrength ;
    private int sigLevel ;
    private CellIdentityLte cid ;
    private String cellSigStrength ;

    public SignalService(Context context) {
        this.context = context ;
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        sigStrength = "-1000" ;
       // cid = (CellIdentityLte) new Object();
    }

    public String getSigName() {
        return sigName;
    }

    public String getSigStrength() {
        return sigStrength;
    }

    public int getSigLevel() {
        return sigLevel;
    }

    public CellIdentityLte getCid() {
        return cid;
    }

    public String getCellSigStrength() {
        return cellSigStrength;
    }

    public void listen(int event){
        telephonyManager.listen(listener,event);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //监听器类
        /*得到信号的强度由每个tiome供应商,有更新*/
        @Override
        public void onSignalStrengthsChanged( SignalStrength signalStrength) {
            Log.d("Lag1", "signal changed");
            super.onSignalStrengthsChanged(signalStrength);

            String[] parts = signalStrength.toString().split(" ");

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
            sigLevel = signalStrength.getLevel();

            //请求获取权限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},100);
                //return;
            }

            //不知是安卓版本还是什么原因，cellInfo有时候获取不到
            List<CellInfo> list = telephonyManager.getAllCellInfo();

            //CellIdentityLte cid = (CellIdentityLte) new Object();
            if(list!=null) {
//                for (int i = 0; i < list.size(); i++) {
//                    //System.out.println("list "+i+" "+list.get(i));;
//                    Log.d("cellinfo", String.valueOf(list.get(i)));
//                }

                cid = ((CellInfoLte)list.get(0)).getCellIdentity() ;
                cellSigStrength = ((CellInfoLte)list.get(0)).getCellSignalStrength().toString() ;
                Log.d("cellid", cid.toString());
             }
            else
                Log.e("cellInfo","---couldn't get CellInfo---") ;

            setView();
        }

    }

    protected void setView(){

        ((TextView)((Activity)context).findViewById(R.id.tv_signame)).setText(sigName);
        ((TextView)((Activity)context).findViewById(R.id.tv_sigStrength)).setText(sigStrength);
        ((TextView)((Activity)context).findViewById(R.id.tv_levelValue)).setText(String.valueOf(sigLevel));
        ((TextView)((Activity)context).findViewById(R.id.tv_cidValue)).
                setText(cid==null ? "unknow" : cid.toString());
        ((TextView)((Activity)context).findViewById(R.id.tv_cellSigStrength)).setText(cellSigStrength);
    }
}
