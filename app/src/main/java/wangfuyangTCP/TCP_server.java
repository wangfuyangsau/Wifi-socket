package wangfuyangTCP;

/**
 * Created by wangfuyang on 2018/4/23.
 */
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class TCP_server extends Thread{
    private static final String TAG_1 = "TCPChat";
    private Handler mhandler;
    private ServerSocket serverSocket;
    public InputStream  Ser_inputStream;
    public OutputStream Ser_outputStream;
    ///

    ////
    public int biaojia=1;
    private Socket Ser_Socket;
    private Socket Serr_Socket=null;
    private Socket Se1_Socket=null;
    private Socket Se2_Socket=null;
    private  int count=0;
    public  ArrayList<Socket>  sockets;

    private  int mport;
    private boolean is_start =true;
    public static int  SERVER_STATE_CORRECT_READ=3;
    public static int  SERVER_STATE_CORRECT_WRITE=4;               //正常通信信息
    public static int  SERVER_STATE_ERROR=5;                 //发生错误异常信息
    public static int  SERVER_STATE_IOFO=6;                  //发送SOCKET信息
    public TCP_server(Handler mhandler,int mport){
        this.mhandler=mhandler;
        this.mport=mport;
    }

    @Override
    public void run() {
        super.run();
        try {
            serverSocket= new ServerSocket(mport);

        } catch (IOException e) {
            e.printStackTrace();
            send_Error();
        }
        try {

            while (is_start)
            {
                Log.e(TAG_1,"等待客户端连接 is_start="+ is_start);

                Ser_Socket=serverSocket.accept();

                if (Ser_Socket != null) {
                    Receive_Thread receive_Thread = new Receive_Thread(Ser_Socket);    //当有客户端连接时，开启数据接收的线程
                    receive_Thread.start();
                }





            } }catch (IOException e) {
            e.printStackTrace();
            send_Error();                                                //  发送错误

        }
    }
    class  Receive_Thread extends  Thread {
        private Socket socket = null;

        public Receive_Thread(Socket socket) {
            this.socket = socket;
            getAddress();
            try {


                Ser_inputStream = socket.getInputStream();
                Ser_outputStream = socket.getOutputStream();


            } catch (IOException e) {
                e.printStackTrace();
                send_Error();
            }
        }

        public void getAddress() {
            InetAddress inetAddress = socket.getInetAddress();
            String[] strings = new String[2];
            strings[0] = inetAddress.getHostAddress();
            strings[1] = inetAddress.getHostName();
            Message message = mhandler.obtainMessage(SERVER_STATE_IOFO, strings);
            mhandler.sendMessage(message);
        }
        @Override
        public void run() {
            super.run();
            while (is_start) {
                try {
                    while (Ser_inputStream.available() == 0) {
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final byte[] buf = new byte[1024];
                    final int len = Ser_inputStream.read(buf);
                    Message message = mhandler.obtainMessage(SERVER_STATE_CORRECT_READ, len, 1, buf);
                    mhandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    send_Error();
                }
            }
            try {
                if (Ser_inputStream != null)
                    Ser_inputStream.close();
                if (Ser_outputStream != null)
                    Ser_outputStream.close();
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
                Log.e(TAG_1, "断开连接监听 释放资源");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void close() {
        try {
            if (serverSocket != null)
                serverSocket.close();
            if (Ser_Socket != null)
                Ser_Socket.close();

            Log.e(TAG_1, "断开连接监听 关闭监听SOCKET");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //数据写ru函数
    public void write(byte[] buffer){


        try {
            if (Ser_outputStream!=null){
                Ser_outputStream.write(buffer);

                Message er_message = mhandler.
                        obtainMessage(SERVER_STATE_CORRECT_WRITE,-1,-1,buffer);
                mhandler.sendMessage(er_message);//返回数据成功发送出去信息
            }else{ send_Error();}
        } catch (IOException e) {
            e.printStackTrace();
            send_Error();
        }
    }

    public  void send_Error(){
        Message er_message = mhandler.obtainMessage(SERVER_STATE_ERROR);
        mhandler.sendMessage(er_message);
    }
    public void setis_start(boolean is_start) {
        this.is_start = is_start;
    }}