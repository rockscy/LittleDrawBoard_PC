package com.rock.drawboard.socket;

import com.rock.drawboard.constant.Config;
import com.rock.drawboard.module.*;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.lang.invoke.SwitchPoint;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 石昌宜 on 2017/3/20.
 */
public class ServerAction extends Thread{
    private static final String TAG = ServerAction.class.getSimpleName();
    private static Client client;
    public ServerAction() {
        initSocket();
    }

    private void initSocket() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Config.PORT);
            Socket socket=serverSocket.accept();
            socket.setKeepAlive(true);
            System.out.println(TAG+": client success join,client ip:"+socket.getInetAddress().getHostAddress());
            client = new Client(socket);
            sendData(DataPackage.DataType.LOGIN,new Login(true));

        } catch (IOException e) {
            e.printStackTrace();
            closeSocket();
        }

    }

    @Override
    public void run() {

        while (!client.isClosed()) {
            try {

                DataPackage dtpg = ((DataPackage) client.receive());
                Object object = dtpg.getData();
                switch (dtpg.getType()) {
                    case POINT:
                        onEventListener.onPencil((Point) object);
                        break;
                    case COMMAND:
                        onEventListener.onCommand((Command) object);
                        break;
                    case COLOR:
                        onEventListener.onColor((SelectColor)object);
                        break;
                    case STROKE:
                        onEventListener.onStroke((StrokeWidth) object);
                        break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                closeSocket();
            } catch (IOException e) {
                e.printStackTrace();
                closeSocket();
            }
        }
    }
    private OnEventListener onEventListener;
    public interface OnEventListener{
        void onPencil(Point point);
        void onCommand(Command command);
        void onColor(SelectColor color);
        void onStroke(StrokeWidth strokeWidth);
    }
    public void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }
    public static void sendData(DataPackage.DataType type, Object data) {
        DataPackage dp = new DataPackage(type, data);
        sendData(dp);
    }
    public static void sendData(DataPackage dp) {
        if (client != null && !client.isClosed()) {
            try {
                client.send(dp);
            } catch (IOException e) {
                closeSocket();
            }
        }
    }
    public static void closeSocket() {
        if(client!=null) {
            client.close();
            System.out.println(TAG+": server close");
        }
    }
}
