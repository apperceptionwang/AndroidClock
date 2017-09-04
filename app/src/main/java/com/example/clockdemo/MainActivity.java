package com.example.clockdemo;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import static android.provider.AlarmClock.ACTION_SET_ALARM;
import static android.provider.AlarmClock.EXTRA_HOUR;
import static android.provider.AlarmClock.EXTRA_MINUTES;

public class MainActivity extends AppCompatActivity {
//    ConToSer conToSer=new ConToSer("172.21.11.234",8000);
    private SlidingMenu mMenu;
    Socket socket;
    OutputStream out=null;
    String message=null;
    private Button btn=null;
    private AlarmManager alarmManager=null;
    Calendar cal=Calendar.getInstance();
    byte []bt=new byte[14];
    String device_id="";
    String ip = "172.21.11.135";
    int port = 8000;
    final int DIALOG_TIME = 0; //设置对话框id


    public void toggleMenu(View view)
    {
        mMenu.toggle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * 初始化操作
         */
//        SharedPreferences sharedPreferences = getSharedPreferences("Mercury", Context.MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
//        editor.putInt("ID",0);
//        EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
//        if (String.valueOf(sharedPreferences.getInt("ID",0))!=null) {
//            txt_device_id.setText(String.valueOf(sharedPreferences.getInt("ID",0)));
//        }
//        Log.e("id",String.valueOf(sharedPreferences.getInt("ID",0)));

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);

        Button btn=(Button) findViewById(R.id.btn_device_id);
        Switch sc=(Switch)findViewById(R.id.switch1);

        /**
         *点击按钮键入设备ID
         */
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
//                Context otherAppsContext = createPackageContext("com.example.clockdemo", Context.CONTEXT_IGNORE_SECURITY);
                SharedPreferences sharedPreferences = getSharedPreferences("Mercury",Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE);

                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                device_id=String.valueOf( txt_device_id.getText());
                editor.putInt("ID", Integer.parseInt(device_id));

                editor.commit();//提交修改
                Log.e("ssdhfj",device_id);
                Toast.makeText(MainActivity.this, "保存ID成功", Toast.LENGTH_SHORT).show();
                if(txt_device_id.length()!=10){
                    Log.e("sdf","长度不等于10");
                    Toast.makeText(MainActivity.this, "长度必须等于10", Toast.LENGTH_SHORT).show();//提示用户
                    return;
                }
                txt_device_id.setEnabled(false);

            }
        });


        sc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final String[] data = new String[1];
//                byte bt=0x60;
//                把byte转为字符串的bit
//                String m=(byte) ((bt >> 7) & 0x1) + (byte) ((bt >> 6) & 0x1)
//                        + (byte) ((bt >> 5) & 0x1) + (byte) ((bt >> 4) & 0x1)
//                        + (byte) ((bt>> 3) & 0x1) + (byte) ((bt >> 2) & 0x1)
//                        + (byte) ((bt >> 1) & 0x1) + (byte) ((bt >> 0) & 0x1)+"";
                /**
                 * 二进制字符串转byte
                 */
                message = "01100000";
                bt[0]=decodeBinaryString.decodeBinaryStr(message);
                if (device_id.equals("")) {
                    Toast.makeText(MainActivity.this, "请键入设备ID:", Toast.LENGTH_SHORT).show();//提示用户
                    Switch sc=(Switch)findViewById(R.id.switch1);
                    sc.setChecked(false);
                    EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
                    txt_device_id.setEnabled(true);
                    return ;
                }
                for(int i=4,j=0;i<14;i++){
                    bt[i]=(byte)device_id.charAt(j++);
                }
                if (isChecked) {
//                    message = "我来了";
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = new Socket(ip, port);
                                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                                Log.e("aaa", "连接成功");
                                out = socket.getOutputStream();
                                InputStream is = socket.getInputStream();
                                byte[] buf = new byte[1024];
                                byte[] rec = new byte[1024];
                                buf = message.getBytes();
                                Log.e("ccc", message);
//                                out.write(buf);
//                                out.write(0x60);
                                out.write(bt);

                                int len = is.read(rec, 0, 1024);
                                if (len==-1) {
                                    Log.e("bbb", "服务器没返回值");
                                    data[0]="";
                                    Switch sc=(Switch)findViewById(R.id.switch1);
                                    sc.setChecked(false);
                                    EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
                                    txt_device_id.setEnabled(true);
                                    return ;
                                }
                                data[0] =new String(rec,0,len);
                                Log.e("OK?", data[0]);

                            } catch (UnknownHostException e) {
                                Log.e("bbb", "连接失败1");
                                message=null;
                                data[0]="";
                                e.printStackTrace();

                                e.printStackTrace();
                            } catch (IOException e) {
                                Log.e("bbb", "连接失败2");
                                message=null;
                                data[0]="";
                                e.printStackTrace();
                            }
                        }
                    };
                    Thread thread=new Thread(runnable);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (message == null) {
                        Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();//提示用户
                        Switch sc=(Switch)findViewById(R.id.switch1);
                        sc.setChecked(false);
                    } else {
//                        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();//提示用户
                        Log.e("ddd", message);
                    }

                    /**
                     * 返回信息
                     */

                    if(data[0].equals("0")){
                        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();//提示用户
                    }
                    if(data[0].equals("1")){
                        Toast.makeText(MainActivity.this, "无效ID", Toast.LENGTH_SHORT).show();//提示用户
                        Switch sc=(Switch)findViewById(R.id.switch1);
                        sc.setChecked(false);
                        EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
                        txt_device_id.setEnabled(true);
                    }
                    if(data[0].equals("2")){
                        Toast.makeText(MainActivity.this, "设备没联网呢", Toast.LENGTH_SHORT).show();//提示用户
                        Switch sc=(Switch)findViewById(R.id.switch1);
                        sc.setChecked(false);
                        EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
                        txt_device_id.setEnabled(true);
                    }
                    if(data[0].equals("3")){
                        Toast.makeText(MainActivity.this, "设备网络不好", Toast.LENGTH_SHORT).show();//提示用户
                        Switch sc=(Switch)findViewById(R.id.switch1);
                        sc.setChecked(false);
                        EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
                        txt_device_id.setEnabled(true);
                    }
                    if(data[0].equals("4")){
                        Toast.makeText(MainActivity.this, "服务器内部错误", Toast.LENGTH_SHORT).show();//提示用户
                        Switch sc=(Switch)findViewById(R.id.switch1);
                        sc.setChecked(false);
                        EditText txt_device_id=(EditText)findViewById(R.id.txt_device_id);
                        txt_device_id.setEnabled(true);
                    }
                } else {
//                    message = "我走了";
                    bt[0]=0x50;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = new Socket(ip, port);
                                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                                Log.e("aaa", "连接成功");
//                                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();//提示用户
                                out = socket.getOutputStream();
                                InputStream is = socket.getInputStream();
                                byte[] buf = new byte[1024];
                                byte[] rec = new byte[1024];
                                buf = message.getBytes();
                                Log.e("ccc", message);
//                                out.write(buf);
                                out.write(bt);
//                                int len = is.read(rec, 0, 1024);
//                                data[0] =new String(rec,0,len);
//                                Log.e("OK?", data[0]);

                            } catch (UnknownHostException e) {
                                Log.e("bbb", "连接失败1");
                                message=null;
                                e.printStackTrace();
                            } catch (IOException e) {
                                Log.e("bbb", "连接失败2");
                                message=null;
                                e.printStackTrace();
                            }
                        }
                    };
                    Thread thread=new Thread(runnable);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (message == null) {
                        Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();//提示用户
//                        sc.setChecked(false);
                    } else {
                        Toast.makeText(MainActivity.this, "成功断开连接", Toast.LENGTH_SHORT).show();//提示用户
                        Log.e("ddd", message);
                    }
                }

            }
        });
        btn=(Button)findViewById(R.id.btn_setClock);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Intent alarmas = new Intent(ACTION_SET_ALARM);
//                alarmas.putExtra(EXTRA_HOUR,hour);


                        startActivity(alarmas);
                    }
                };
                Thread thread=new Thread(runnable);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String hour=EXTRA_HOUR;
                String minutes=EXTRA_MINUTES;
                Log.e("时间和分钟",hour+"    "+minutes);
            }
        });
    }
}
