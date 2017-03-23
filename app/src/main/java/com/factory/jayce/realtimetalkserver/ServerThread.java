package com.factory.jayce.realtimetalkserver;

import android.util.Log;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class ServerThread extends Thread {

    private static final String LOG = "log";
    private final int SERVER_PORT = 8080;
    Socket socket = null;
    MainActivity activity;

    public ServerThread(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            Log.d(LOG, "### SERVER is running ###");
            socket = serverSocket.accept();
            Log.d(LOG, "### Client has been connected ###");
            InputStream socketInput = socket.getInputStream();
            OutputStream socketOutput = socket.getOutputStream();

            new Thread(new InputThread(socketInput)).start();
            new Thread(new OutputThread(socketOutput)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class InputThread implements Runnable {

        private InputStream in;

        public InputThread(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            DataInputStream dis = new DataInputStream(in);
            try {
                while(socket != null) {
                    String message = null;
                    message = dis.readUTF();

                    if(message != null) {
                        try {
                            String received_lang_from = message.substring(0,2);
                            String received_lang_to = message.substring(2,4);
                            switch(received_lang_to) {
                                case "ko":
                                    activity.tts.setLanguage(Locale.KOREAN);
                                    break;
                                case "en":
                                    activity.tts.setLanguage(Locale.ENGLISH);
                                    break;
                                case "ru":
                                    Locale locale = new Locale("ru");
                                    activity.tts.setLanguage(locale);
                                    break;
                                case "fr":
                                    activity.tts.setLanguage(Locale.FRENCH);
                                    break;
                            }
                            message = message.substring(4);
                            Log.d(LOG, message.replaceAll(" ", "+"));
                            String str = activity.getJsonStringYandex(received_lang_from + "-" + received_lang_to, message.replaceAll(" ", "+"));
                            activity.speak(str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        message = null;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    dis.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class OutputThread implements Runnable {

        private OutputStream out;
        private DataOutputStream dos;

        public OutputThread(OutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            dos = new DataOutputStream(out);
            try {
                while(socket != null) {
                    if(activity.voice_message != null) {
                        try {
                            dos.writeUTF(activity.lang_from + activity.lang_to + activity.voice_message);
                            dos.flush();
                            activity.voice_message = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                try {
                    out.close();
                    dos.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
