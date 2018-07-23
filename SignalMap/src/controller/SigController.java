package controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SigController {
    public static ArrayList<Socket> socketList = new ArrayList<Socket>() ;
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(3001) ;

        while(true){
            Socket socket = serverSocket.accept();
            socketList.add(socket) ;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader br = new BufferedReader
                                (new InputStreamReader(socket.getInputStream())) ;
                        //read message from client
                        System.out.println(br.readLine()) ;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
