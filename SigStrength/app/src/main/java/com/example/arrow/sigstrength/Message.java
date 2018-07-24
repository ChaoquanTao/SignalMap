package com.example.arrow.sigstrength;

import android.telephony.CellIdentity;
import android.telephony.CellIdentityLte;

public class Message {
    private String sigType ;
    private int sigStrength ;
    private int sigLevel ;
    private CellID cid ;
    private Location loc ;

    public Message() {

    }

    public Message(CellID cid, Location loc) {
        this.cid = cid;
        this.loc = loc;
    }

    public String getSigType() {
        return sigType;
    }

    public void setSigType(String sigType) {
        this.sigType = sigType;
    }

    public int getSigStrength() {
        return sigStrength;
    }

    public void setSigStrength(int sigStrength) {
        this.sigStrength = sigStrength;
    }

    public int getSigLevel() {
        return sigLevel;
    }

    public void setSigLevel(int sigLevel) {
        this.sigLevel = sigLevel;
    }

    public CellID getCid() {
        return cid;
    }

    public void setCid(CellIdentityLte cid) {
        this.cid = new CellID(cid.getMcc(),cid.getMnc()) ;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }
}

class CellID{
    private int mMcc ;
    private int mMnc ;

    public CellID(int mMcc, int mMnc) {
        this.mMcc = mMcc;
        this.mMnc = mMnc;
    }
}

class Location{
    float lat;
    private float lng ;
}

