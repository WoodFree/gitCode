package com.example.freewood.socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText edit;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= (ImageView) findViewById(R.id.img);
        edit= (EditText) findViewById(R.id.edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
 //              new ServerThread().start();
                new sendThread().start();
                String fileName =getSDPath()+"/Download/aa.jpg";
                Bitmap bm = BitmapFactory.decodeFile(fileName);
                imageView.setImageBitmap(bm);
                Snackbar.make(view, getSDPath()+"/Download/aa.jpg", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Message msg=mHandler.obtainMessage(1,edit.getText().toString());
                mHandler.sendMessage(msg);
            }
        });
    }



    class sendThread extends  Thread{
        @Override
        public void run() {


            Looper.prepare();
            mHandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    try {
                        DatagramSocket socket=new DatagramSocket(4567);
                        InetAddress address=InetAddress.getByName("192.168.31.185");
                        String str=msg.obj.toString();
                        Log.e("mylog",str);
                        byte data[]=str.getBytes();
                        DatagramPacket packet=new DatagramPacket(data,data.length,address,4567);
                        socket.send(packet);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            Looper.loop();
            super.run();
        }
    }

    class ServerThread extends Thread{
       /* @Override888
        public void run() {
            try {
                //创建一个Socket对象，指定服务器端的IP和端口
                Socket socket=new Socket("192.168.31.185",4567);
                //使用InputStream读取内存文件
                InputStream inputStream=new FileInputStream(Environment.getExternalStorageDirectory()+"/Download/aa.jpg");
                OutputStream outputStream=socket.getOutputStream();
                byte buffer[]=new byte[4*1024];
                int temp =0;
                //读取InputStream的数据，写入到OutputStream中
                while ((temp=inputStream.read(buffer))!=-1){
                    outputStream.write(buffer,0,temp);
                    outputStream.flush();
                }
                System.out.println("传输完成！");
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            super.run();
        }*/

        @Override
        public void run() {
            try {
                DatagramSocket socket=new DatagramSocket(4567);
                InetAddress address=InetAddress.getByName("192.168.31.185");
                String str="hello";
                InputStream inputStream=new FileInputStream(Environment.getExternalStorageDirectory()+"/Download/aa.jpg");
                byte data[]=str.getBytes();
                byte file[]=new byte[1024];


                DatagramSocket socketmsg=new DatagramSocket(7654);
                String filelength= String.valueOf(inputStream.available());
                byte msg[]=filelength.getBytes();
                DatagramPacket msgpacket = new DatagramPacket(msg,
                        msg.length, address,7654);
                socketmsg.send(msgpacket);

                int numofBlock = inputStream.available() / file.length;
                int lastSize = inputStream.available() % file.length;
                for (int i = 0; i < numofBlock; i++) {
                    System.out.println("ok--");
                    inputStream.read(file, 0, file.length);// 写入内存
                    DatagramPacket packet = new DatagramPacket(file,
                            file.length, address,4567);
                    socket.send(packet);
                    Thread.sleep(1); // 简单的防止丢包现象
                }
                inputStream.read(file, 0, lastSize);
                DatagramPacket packet = new DatagramPacket(file,
                        file.length, address,4567);
                socket.send(packet);
                Thread.sleep(1); // 简单的防止丢包现象
                //
                inputStream.close();
                socket.close();

/*                int c=inputStream.read(file);
                inputStream.close();
*//*                DatagramPacket packet=new DatagramPacket(data,
                        data.length,address,4567);*//*
                DatagramPacket packet=new DatagramPacket(file,file.length,address,4567);
                    socket.send(packet);
                    TimeUnit.MICROSECONDS.sleep(1);*/

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            super.run();
        }
    }


    public void send() {
        try {
            //文件发送者设置监听端口
            DatagramSocket send = new DatagramSocket(5678);
            InputStream inputStream=new FileInputStream(Environment.getExternalStorageDirectory()+"/Download/aa.jpg");

            //确认信息包
            byte[] messagebuf = new byte[1024];
            DatagramPacket messagepkg = new DatagramPacket(messagebuf, messagebuf.length);
            //文件包
            byte[] buf = new byte[1024 * 30];
            int len;
            while ((len = inputStream.read(buf)) != -1) {

                DatagramPacket  pkg = new DatagramPacket(buf, len, new InetSocketAddress(
                        "192.168.31.185", 5678));
                //设置确认信息接收时间，3秒后未收到对方确认信息，则重新发送一次
                send.setSoTimeout(3000);
                while (true) {
                    send.send(pkg);
                    send.receive(messagepkg);
                    System.out.println(new String(messagepkg.getData()));
                    break;
                }
            }
            // 文件传完后，发送一个结束包
            buf = "end".getBytes();
            DatagramPacket endpkg = new DatagramPacket(buf, buf.length,
                    new InetSocketAddress("192.168.31.185", 5678));
            System.out.println("文件发送完毕");
            send.send(endpkg);
            inputStream.close();
            send.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public String getSDPath(){
        File sdDir = null;
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录

        return sdDir.toString();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
