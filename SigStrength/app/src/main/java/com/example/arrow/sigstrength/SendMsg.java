package com.example.arrow.sigstrength;

import java.util.Timer;
import java.util.TimerTask;

public class SendMsg {
    public void sendMessage(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //send message to the server here



            }
        },1*60*1000);
    }

}
