package com.example.freewood.socketserver;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn= (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ServerThread().start();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    class ServerThread extends Thread{
       /* @Override
        public void run() {
            ServerSocket serverSocket=null;
            try {
                //创建一个ServerSocket对象，并在4567端口监听
                serverSocket=new ServerSocket(4567);
                //调用ServerSocket的accept的方法，阻塞接收客户端请求
                Socket socket=serverSocket.accept();
                InputStream inputStream=socket.getInputStream();
                //实例化文件输出流的位置和文件名和位置
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/aa.jpg");
                byte buffer[]=new byte[1024*4];
                int temp=0;
                //从InputStream中读取接收的数据
                while((temp=inputStream.read(buffer))!=-1){
                    //通过输出流将文件生成
                    out.write(buffer,0,temp);
                    out.flush();
                }
                inputStream.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            super.run();
        }*/


        @Override
        public void run() {
           try {
                //创建一个DtagramSocket对象，并且指定监听的端口号
                DatagramSocket socket=new DatagramSocket(4567);

               byte msg[]=new byte[1024];
                //创建一个空得DatagramPacket对象

               DatagramPacket msgpacket=new DatagramPacket(msg,msg.length);
                //同样使用recive方法接收客户端数据，也是阻塞的方式

               while (true){
                   socket.receive(msgpacket);
                   String result=new String(msgpacket.getData(),msgpacket.getOffset(),msgpacket.getLength());
                   System.out.println("---"+result);
                   System.out.println("结束！");
               }



            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            super.run();
        }
    }




    public void receive() {
        try {
            // 接收文件监听端口
            DatagramSocket receive = new DatagramSocket(4567);
            // 写文件路径
            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/aa.jpg");


            // 读取文件包
            byte[] buf = new byte[1024 * 63];
            DatagramPacket pkg = new DatagramPacket(buf, buf.length);
            // 发送收到文件后 确认信息包
            byte[] messagebuf = new byte[1024];
            messagebuf = "ok".getBytes();
            DatagramPacket messagepkg = new DatagramPacket(messagebuf, messagebuf.length,
                    new InetSocketAddress("192.168.31.225", 5678));
            // 循环接收包，每接到一个包后回给对方一个确认信息，对方才发下一个包(避免丢包和乱序)，直到收到一个结束包后跳出循环，结束文件传输，关闭流
            while (true) {
                receive.receive(pkg);
                if (new String(pkg.getData(), 0, pkg.getLength()).equals("end")) {
                    System.out.println("文件接收完毕");
                    out.close();
                    receive.close();
                    break;
                }
                receive.send(messagepkg);
                System.out.println(new String(messagepkg.getData()));
                out.write(pkg.getData(), 0, pkg.getLength());
                out.flush();
            }
            out.close();
            receive.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
