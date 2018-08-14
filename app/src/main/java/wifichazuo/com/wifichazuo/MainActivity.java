package wifichazuo.com.wifichazuo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import android.os.Handler;
import android.os.Message;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.TimePicker;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wangfuyangTCP.Data;
import wangfuyangTCP.TCP_client;
import wangfuyangTCP.TCP_server;


public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String TAG_1 = "TCPChat";
    //开关控件
    private Switch switchhahah;
    private Switch switchtime;
    private Switch switchtimeoff;
    private Switch switchbing;
    private Switch switchtimebing;
    private Switch switchtimebingoff;
    private Switch gettimesendkong;
    private Switch gettimesendbing;
    //滑条控件
    private SeekBar settime;
    //控件按钮
    private Button start;
    private Button stop;
    private Button clear_;
    private Button send;

    //复选按钮控件
    private CheckBox hex_show;
    private CheckBox auto_huang;
    private CheckBox hex_send;
    private CheckBox auto_send;
    //文本显示控件

    private TextView showtime;
    private TextView showtimebing;
    private TextView ip_mode;
    private TextView port_mode;
    private TextView de_state;                        //设置状态
    private TextView ip_show;                        //连接的对象IP 显示
    private TextView name_show;                      //连接的对象主机名号 显示
    private TextView re_count;                       //接收字节数
    private TextView se_count;                       //发送字节数
    private TextView re_data_show;
    private TextView settimekong;
    private TextView settimebing;//接收字节显示
    //编辑框控件
    private EditText edit_ip;
    private EditText edit_port;
    private EditText edit_time;
    private EditText edit_data;
    private EditText settimebingmm;
    private EditText settimebinghh;

    //下拉控件
    private Spinner link_mode;       //连接模式
    //
    private boolean exit;
    //网络连接模式选择
    public final static int MODE_TCP_SERVER = 0;
    public final static int MODE_TCP_CLIENT = 1;
    private int ch_mode = 0;
    //TCP服务器通信模式下
    private TCP_server tcp_service = null;
    private int ser_port;
    private boolean ser_islink = false;
    public final static int SERVER_STATE_CORRECT_READ = 3;
    public final static int SERVER_STATE_CORRECT_WRITE = 4;               //正常通信信息
    public final static int SERVER_STATE_ERROR = 5;                 //发生错误异常信息
    public final static int SERVER_STATE_IOFO = 6;                  //发送SOCKET信息
    // TCP客户端通信模式下
    private TCP_client tcp_client = null;
    private final static int CLIENT_STATE_CORRECT_READ = 7;
    public final static int CLIENT_STATE_CORRECT_WRITE = 8;               //正常通信信息
    public final static int CLIENT_STATE_ERROR = 9;                 //发生错误异常信息
    public final static int CLIENT_STATE_IOFO = 10;                  //发送SOCKET信息
    private boolean client_islink = false;

    //复选状态信息
    private boolean Hex_show = false;
    private boolean Auto_huang = false;
    private boolean Hex_send = false;
    private boolean Auto_send = false;
    //计数用
    private int countin = 0;
    private int countout = 0;
    public DateFormat dff;
    public DateFormat dfff;
    public int systimehh;
    public int systimemm;
    public int time;
    public String shijianshijian;
    Calendar c=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        settimekong.setText(sharedPreferences.getString("remembershi",""));
        //getsystime();
        link_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ch_mode = position;
                if (ch_mode == MODE_TCP_SERVER) {
                    ip_mode.setText("本地 I P");
                    port_mode.setText("本地端口");
                    start.setText("启动");
                    de_state.setText("");
                    ip_show.setHint("对象IP");
                    name_show.setHint("对象主机名");
                    clear();
                }
                if (ch_mode == MODE_TCP_CLIENT) {
                    ip_mode.setText("目的 I P");
                    port_mode.setText("目的端口");
                    start.setText("连接");
                    de_state.setText("");
                    ip_show.setHint("对象IP");
                    name_show.setHint("对象主机名");
                    clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        edit_ip.setText(getLocalIpAddress());   //获取本地IP地址显示
        edit_port.setText(8266 + "");             //设置默认端口号
        start.setOnClickListener(startlistener);
        stop.setOnClickListener(stoplistener);
        send.setOnClickListener(sendlistener);
        clear_.setOnClickListener(clearlistener);

        hex_send.setOnCheckedChangeListener(listener);
        hex_show.setOnCheckedChangeListener(listener);
        auto_huang.setOnCheckedChangeListener(listener);
        auto_send.setOnCheckedChangeListener(listener);
        switchhahah.setOnCheckedChangeListener(listener);
        switchtime.setOnCheckedChangeListener(listener);
        switchtimeoff.setOnCheckedChangeListener(listener);
        switchbing.setOnCheckedChangeListener(listener);
        switchtimebing.setOnCheckedChangeListener(listener);
        switchtimebingoff.setOnCheckedChangeListener(listener);
        gettimesendkong.setOnCheckedChangeListener(listener);
        settime.setOnSeekBarChangeListener(seelistener);
        gettimesendbing.setOnCheckedChangeListener(listener);
    }/////主函数就到这里喽

    private void getsystime() {
        dff = new SimpleDateFormat("HH");
        dff.format(new Date());
        systimehh = Integer.parseInt(dff.format(new Date()));
        dfff = new SimpleDateFormat("mm");
        dfff.format(new Date());
        systimemm = Integer.parseInt(dfff.format(new Date()));

    }

    //初始化控件函数
    private void init() {
        link_mode = (Spinner) findViewById(R.id.ch_mode);
        ip_mode = (TextView) findViewById(R.id.ip_mode);
        port_mode = (TextView) findViewById(R.id.port_mode);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        clear_ = (Button) findViewById(R.id.de_clear);
        send = (Button) findViewById(R.id.de_send);


        de_state = (TextView) findViewById(R.id.de_action);
        ip_show = (TextView) findViewById(R.id.de_ip);
        name_show = (TextView) findViewById(R.id.de_sport);
        re_count = (TextView) findViewById(R.id.receive_count);
        se_count = (TextView) findViewById(R.id.send_count);
        re_data_show = (TextView) findViewById(R.id.receive);
        re_data_show.setMovementMethod(ScrollingMovementMethod
                .getInstance());// 使TextView接收区可以滚动

        edit_ip = (EditText) findViewById(R.id.ip_edit);
        edit_port = (EditText) findViewById(R.id.port_edit);
        edit_time = (EditText) findViewById(R.id.edi_auto);
        edit_data = (EditText) findViewById(R.id.send_data);
        edit_data.setCursorVisible(false);

        hex_show = (CheckBox) findViewById(R.id.hex_show);
        auto_huang = (CheckBox) findViewById(R.id.autohuang);
        hex_send = (CheckBox) findViewById(R.id.hex_send);
        auto_send = (CheckBox) findViewById(R.id.auto_send);
        //
        switchhahah = (Switch) findViewById(R.id.switchhahah);
        switchbing = (Switch) findViewById(R.id.switchbing);
        switchtime = (Switch) findViewById(R.id.switchtime);
        switchtimebing = (Switch) findViewById(R.id.switchtimebing);
        switchtimeoff = (Switch) findViewById(R.id.switchtimeoff);
        switchtimebingoff = (Switch) findViewById(R.id.switchtimebingoff);
        showtime = (TextView) findViewById(R.id.showtime);
        showtimebing = (TextView) findViewById(R.id.showtimebing);
        settime = (SeekBar) findViewById(R.id.settime);

        gettimesendkong = (Switch) findViewById(R.id.gettimesendkong);
        gettimesendbing = (Switch) findViewById(R.id.gettimesendbing);
        settimekong=(TextView)findViewById(R.id.settimekong);
        settimebing=(TextView)findViewById(R.id.settimebing );
    }

    private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.hex_show:
                    if (isChecked) {
                        Toast.makeText(MainActivity.this, "16进制显示",
                                Toast.LENGTH_SHORT).show();
                        Hex_show = true;
                    } else
                        Hex_show = false;

                    break;
                case R.id.autohuang:
                    if (isChecked) {
                        Toast.makeText(MainActivity.this, "自动换行",
                                Toast.LENGTH_SHORT).show();
                        Auto_huang = true;
                    } else
                        Auto_huang = false;
                    break;
                case R.id.hex_send:
                    if (isChecked) {
                        Toast.makeText(MainActivity.this, "16进制发送",
                                Toast.LENGTH_SHORT).show();
                        Hex_send = true;
                    } else
                        Hex_send = false;

                    break;
                case R.id.auto_send:
                    if (isChecked) {
                        Toast.makeText(MainActivity.this, "自动发送",
                                Toast.LENGTH_SHORT).show();
                        Auto_send = true;
                    } else
                        Auto_send = false;

                    break;
                case R.id.switchhahah:
                    if (isChecked) {//开关控制函数
                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message1 = "A0";//开

                                sendmessage(message1);


                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message1 = "A0";//关
                                sendmessage(message1);
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message1 = "A6";//开
                                sendmessage(message1);
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message1 = "A6";//关
                                sendmessage(message1);
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    break;
                case R.id.switchbing:
                    if (isChecked) {//开关控制函数
                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message1 = "B0";//开

                                sendmessage(message1);


                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message1 = "B0";//关
                                sendmessage(message1);
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message1 = "B6";//开
                                sendmessage(message1);
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message1 = "B6";//关
                                sendmessage(message1);
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    break;
                case R.id.switchtime:
                    if (isChecked) {//开关控制函数
                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "A" + shijianshijian;//开

                                sendmessage(message2);
                                //timeonoff=true;
                                Toast.makeText(MainActivity.this, "空调定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "A" + shijianshijian;//开

                                sendmessage(message2);
                                Toast.makeText(MainActivity.this, "空调定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                                //  timeonoff=true;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "Ab";//开
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "Ab";//关
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    break;
                case R.id.switchtimebing:
                    if (isChecked) {//开关控制函数
                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "B" + shijianshijian;//开

                                sendmessage(message2);
                                //timeonoff=true;
                                Toast.makeText(MainActivity.this, "冰箱定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "B" + shijianshijian;//开

                                sendmessage(message2);
                                Toast.makeText(MainActivity.this, "冰箱定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                                //  timeonoff=true;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "Bb";//开
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "Bb";//关
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    break;
                case R.id.switchtimeoff:
                    if (isChecked) {//开关控制函数
                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "AA" + shijianshijian;//开

                                sendmessage(message2);
                                //timeonoff=true;
                                Toast.makeText(MainActivity.this, "空调定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "AA" + shijianshijian;//开

                                sendmessage(message2);
                                Toast.makeText(MainActivity.this, "空调定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                                //  timeonoff=true;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "Ab";//开
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "Ab";//关
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    break;
                case R.id.switchtimebingoff:
                    if (isChecked) {//开关控制函数
                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "BB" + shijianshijian;//开

                                sendmessage(message2);
                                //timeonoff=true;
                                Toast.makeText(MainActivity.this, "冰箱定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "BB" + shijianshijian;//开

                                sendmessage(message2);
                                Toast.makeText(MainActivity.this, "冰箱定时时间为" + shijianshijian + "小时", Toast.LENGTH_SHORT).show();
                                //  timeonoff=true;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        if (ch_mode == MODE_TCP_SERVER) {
                            if (ser_islink == true) {
                                String message2 = "Bb";//开
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立server！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (ch_mode == MODE_TCP_CLIENT) {
                            if (client_islink == true) {
                                String message2 = "Bb";//关
                                sendmessage(message2);
                                // timeonoff=false;
                            } else {
                                Toast.makeText(MainActivity.this, "连接未建立client！！！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    break;
                case R.id.gettimesendkong:
                    if (isChecked) {
                        c.setTimeInMillis(System.currentTimeMillis());
                        int mHour=c.get(Calendar.HOUR_OF_DAY);
                        int mMinute=c.get(Calendar.MINUTE);
                        new TimePickerDialog(MainActivity.this,
                                new TimePickerDialog.OnTimeSetListener()
                                {
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute)
                                    {
                                        c.setTimeInMillis(System.currentTimeMillis());
                                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                        c.set(Calendar.MINUTE,minute);
                                        c.set(Calendar.SECOND,0);
                                        c.set(Calendar.MILLISECOND,0);
                                        CallAlarm call=new CallAlarm();
                                        IntentFilter intentFilter = new IntentFilter();
                                        intentFilter.addAction("edu.jju.broadcastreceiver");
                                        registerReceiver(call, intentFilter);
                                        Intent intent = new Intent();
                                        intent.setAction("edu.jju.broadcastreceiver");
                                        //  sendBroadcast(intent);//发送普通广播
                                        // Intent intent = new Intent(MainActivity.this, CallAlarm.class);
                                        PendingIntent sender=PendingIntent.getBroadcast(
                                                MainActivity.this,0, intent, 0);
                                        AlarmManager am;
                                        am = (AlarmManager)getSystemService(ALARM_SERVICE);
                                        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
                                        String tmpS=format(hourOfDay)+"："+format(minute);
                                        settimekong.setText(tmpS);
                                        //SharedPreferences保存数据，并提交
                                        editor=sharedPreferences.edit();
                                        editor.putString("remembershi",tmpS);
                                        editor.apply();
                                        // SharedPreferences time1Share = getPreferences(0);
                                       // SharedPreferences.Editor editor = time1Share.edit();
                                       // editor.putString("TIME1", tmpS);
                                       // editor.apply();
                                        Toast.makeText(MainActivity.this,"设置时间为"+tmpS, Toast.LENGTH_SHORT).show();
                                    }
                                },mHour,mMinute,true).show();
                            // Toast.makeText(MainActivity.this, "没啥用的，我也很难过", Toast.LENGTH_SHORT).show();

                    } else {
                        CallAlarm call=new CallAlarm();
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("edu.jju.broadcastreceiver");
                        registerReceiver(call, intentFilter);
                        Intent intent = new Intent();
                        intent.setAction("edu.jju.broadcastreceiver");
                        //  sendBroadcast(intent);//发送普通广播

                        // Intent intent = new Intent(MainActivity.this, CallAlarm.class);
                        PendingIntent sender=PendingIntent.getBroadcast(
                                MainActivity.this,0, intent, 0);
                        AlarmManager am;
                        am = (AlarmManager)getSystemService(ALARM_SERVICE);
                        am.cancel(sender);
                        Toast.makeText(MainActivity.this,"闹钟时间删除",
                                Toast.LENGTH_SHORT).show();
                       // setTime1.setText("目前无设置");
                        editor=sharedPreferences.edit();
                        editor.putString("remembershi","");
                        editor.apply();
                     //   SharedPreferences time1Share = getPreferences(0);
                       // SharedPreferences.Editor editor = time1Share.edit();
                       // editor.putString("TIME1", "");
                        //editor.apply();
                        settimekong.setText("当前无设置");
                    }
                    break;
                case  R.id.gettimesendbing:
                    if (isChecked) {
                        c.setTimeInMillis(System.currentTimeMillis());
                        int mHour=c.get(Calendar.HOUR_OF_DAY);
                        int mMinute=c.get(Calendar.MINUTE);
                        new TimePickerDialog(MainActivity.this,
                                new TimePickerDialog.OnTimeSetListener()
                                {
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute)
                                    {
                                        c.setTimeInMillis(System.currentTimeMillis());
                                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                        c.set(Calendar.MINUTE,minute);
                                        c.set(Calendar.SECOND,0);
                                        c.set(Calendar.MILLISECOND,0);
                                        CallAlarm1 call=new CallAlarm1();
                                        IntentFilter intentFilter = new IntentFilter();
                                        intentFilter.addAction("edu.jj.broadcastreceiver");
                                        registerReceiver(call, intentFilter);
                                        Intent intent = new Intent();
                                        intent.setAction("edu.jj.broadcastreceiver");
                                        //  sendBroadcast(intent);//发送普通广播
                                        // Intent intent = new Intent(MainActivity.this, CallAlarm.class);
                                        PendingIntent sender=PendingIntent.getBroadcast(
                                                MainActivity.this,0, intent, 0);
                                        AlarmManager am;
                                        am = (AlarmManager)getSystemService(ALARM_SERVICE);
                                        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
                                        String tmpS=format(hourOfDay)+"："+format(minute);
                                        settimebing.setText(tmpS);
                                        //SharedPreferences保存数据，并提交
                                        editor=sharedPreferences.edit();
                                        editor.putString("remembershi1",tmpS);
                                        editor.apply();
                                        // SharedPreferences time1Share = getPreferences(0);
                                        // SharedPreferences.Editor editor = time1Share.edit();
                                        // editor.putString("TIME1", tmpS);
                                        // editor.apply();
                                        Toast.makeText(MainActivity.this,"设置时间为"+tmpS, Toast.LENGTH_SHORT).show();
                                    }
                                },mHour,mMinute,true).show();
                        // Toast.makeText(MainActivity.this, "没啥用的，我也很难过", Toast.LENGTH_SHORT).show();

                    } else {
                        CallAlarm1 call=new CallAlarm1();
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("edu.jj.broadcastreceiver");
                        registerReceiver(call, intentFilter);
                        Intent intent = new Intent();
                        intent.setAction("edu.jj.broadcastreceiver");
                        //  sendBroadcast(intent);//发送普通广播

                        // Intent intent = new Intent(MainActivity.this, CallAlarm.class);
                        PendingIntent sender=PendingIntent.getBroadcast(
                                MainActivity.this,0, intent, 0);
                        AlarmManager am;
                        am = (AlarmManager)getSystemService(ALARM_SERVICE);
                        am.cancel(sender);
                        Toast.makeText(MainActivity.this,"闹钟时间删除",
                                Toast.LENGTH_SHORT).show();
                        // setTime1.setText("目前无设置");
                        editor=sharedPreferences.edit();
                        editor.putString("remembershi1","");
                        editor.apply();
                        //   SharedPreferences time1Share = getPreferences(0);
                        // SharedPreferences.Editor editor = time1Share.edit();
                        // editor.putString("TIME1", "");
                        //editor.apply();
                        settimebing.setText("当前无设置");
                    }break;


            }

        }
    };

    public class CallAlarm extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Toast.makeText(MainActivity.this,"woshoudaole",Toast.LENGTH_SHORT).show();
            sendmessage("A0");//服务器控制器开启
        }
    }
    public class CallAlarm1 extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Toast.makeText(MainActivity.this,"woshoudaole",Toast.LENGTH_SHORT).show();
            sendmessage("B0");//服务器控制器开启
        }}
    private String format(int x)
    {
        String s=""+x;
        if(s.length()==1) s="0"+s;
        return s;
    }

    private View.OnClickListener startlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ch_mode == MODE_TCP_SERVER) {
                if (tcp_service == null) {//此处初始化默认状态
                    ser_port = Integer.valueOf(edit_port.getText().toString());    //获取设置的端口号 默认8080
                    tcp_service = new TCP_server(ser_handler, ser_port);
                    tcp_service.start();//子线程启动
                    de_state.setText("TCP服务器模式  启动");
                    stop.setEnabled(true);
                    edit_ip.setEnabled(false);
                    edit_port.setEnabled(false);
                } else {
                    Log.e(TAG_1, "断开连接监听 释放资源");
                    de_state.setText("TCP服务器模式  出错");
                }

            }
            if (ch_mode == MODE_TCP_CLIENT) {
                if (tcp_client == null) {
                    tcp_client = new TCP_client(cli_handler);
                    try {
                        InetAddress ipAddress = InetAddress.getByName
                                (edit_ip.getText().toString());
                        int port = Integer.valueOf(edit_port.getText().toString());//获取端口号
                        tcp_client.setInetAddress(ipAddress);
                        tcp_client.setPort(port);

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    edit_ip.setEnabled(false);
                    edit_port.setEnabled(false);
                    tcp_client.start();
                }
                stop.setEnabled(true);
            }
        }
    };

    private View.OnClickListener clearlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clear();
        }
    };

    private View.OnClickListener stoplistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ch_mode == MODE_TCP_SERVER) {
                tcp_service.setis_start(false);
                if (tcp_service != null) {
                    tcp_service.close();
                    tcp_service = null;
                }
                de_state.setText("TCP服务器模式  关闭");
                Ip_clear();
                edit_ip.setEnabled(true);
                edit_port.setEnabled(true);
            }
            if (ch_mode == MODE_TCP_CLIENT) {
                if (tcp_client != null) {
                    tcp_client.close();
                    tcp_client = null;
                }
                Ip_clear();
                edit_ip.setEnabled(true);
                edit_port.setEnabled(true);
                stop.setEnabled(false);

            }

        }

    };
    //发送响应函数
    private View.OnClickListener sendlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (ch_mode == MODE_TCP_SERVER) {
                if (ser_islink == true) {
                    String message = edit_data.getText().toString().replaceAll(" ", "");
                    if (message.equals("")) {
                        Toast.makeText(MainActivity.this, "发送内容不能为空",
                                Toast.LENGTH_SHORT).show();
                    }
                    sendmessage(message);
                } else {
                    Toast.makeText(MainActivity.this, "连接未建立",
                            Toast.LENGTH_SHORT).show();
                }
            }
            if (ch_mode == MODE_TCP_CLIENT) {
                if (client_islink == true) {
                    String message = edit_data.getText().toString().replaceAll(" ", "");
                    if (message.equals("")) {
                        Toast.makeText(MainActivity.this, "发送内容不能为空",
                                Toast.LENGTH_SHORT).show();
                    }
                    sendmessage(message);
                } else {
                    Toast.makeText(MainActivity.this, "连接未建立",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seelistener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                time = i / 10;
                //timeint=i;
                showtime.setText(time + "小时");
                showtimebing.setText(time + "小时");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            shijianshijian = Integer.toString(time);//时间12345小时
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tcp_service != null) {
            tcp_service.setis_start(false);
            tcp_service.close();
            tcp_service = null;
        }
        if (tcp_client != null) {
            tcp_client.close();
            tcp_client = null;
        }
    }

    //服务端模式下。。。。。handle处理
    private Handler ser_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SERVER_STATE_ERROR) {
                Toast.makeText(MainActivity.this, "连接异常"
                        , Toast.LENGTH_SHORT).show();
                de_state.setText("TCP服务器模式 连接异常");
                ip_show.setHint("对象IP");
                name_show.setHint("对象主机名");
                ser_islink = false;
            }
            //发送数据
            if (msg.what == SERVER_STATE_CORRECT_WRITE) {
                Handler_send(msg);
            }
            //接收数据
            if (msg.what == SERVER_STATE_CORRECT_READ) {
                Handler_receive(msg);
            }
            if (msg.what == SERVER_STATE_IOFO) {
                ser_islink = true;
                de_state.setText("TCP服务器模式  建立连接");
                stop.setEnabled(true);
                String[] strings = (String[]) msg.obj;
                ip_show.append(strings[0] + "\n");
                name_show.append(strings[1] + "\n");
            }
        }
    };
    //客户端通信模式下handle处理
    private Handler cli_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLIENT_STATE_ERROR:
                    Toast.makeText(MainActivity.this, "连接异常"
                            , Toast.LENGTH_SHORT).show();
                    de_state.setText("TCP客户端模式 连接异常");
                    ip_show.setHint("对象IP");
                    name_show.setHint("对象主机名");
                    client_islink = false;
                    break;
                case CLIENT_STATE_IOFO:
                    client_islink = true;
                    de_state.setText("TCP客户端模式  建立连接");
                    String[] strings = (String[]) msg.obj;
                    ip_show.append(strings[0] + "\n");
                    name_show.append(strings[1] + "空调服务器" + "\n");
                    break;
                //接收数据
                case CLIENT_STATE_CORRECT_READ:
                    Handler_receive(msg);
                    break;
                //发送数据
                case CLIENT_STATE_CORRECT_WRITE:
                    Handler_send(msg);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        exit();
    }

    //获取wifi本地IP和主机名
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    //发送数据函数
    public void sendmessage(String message) {
        if (Hex_send == true) {
            byte[] send = Data.hexStr2Bytes(message);
            if (ch_mode == MODE_TCP_SERVER) {
                tcp_service.write(send);//从主线程去服务器子线程
            } else if (ch_mode == MODE_TCP_CLIENT) {
                tcp_client.sendmessage(send);//从主线程去客户端子线程
            }
        } else {
            byte[] send = message.getBytes();
            if (ch_mode == MODE_TCP_SERVER)//从主线程去服务器子线程
            {
                tcp_service.write(send);
            } else if (ch_mode == MODE_TCP_CLIENT) {
                tcp_client.sendmessage(send);
            }
        }
    }
    //发送命令函数

    //页面退出函数
    public void exit() {
        if (exit == true) {
            this.finish();
        }
        exit = true;
        Toast.makeText(this, "再按一次，返回上一页", Toast.LENGTH_SHORT).show();
    }

    //定时返回函数
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String message = edit_data.getText().toString();
            sendmessage(message);
        }
    };

    //清除函数
    private void clear() {
        countin = 0;
        countout = 0;
        re_count.setText("0个");
        se_count.setText("0个");
        re_data_show.setText("");
    }

    // 接收数据处理分析函数，通过handler从子线程回传到主线程
    private void Handler_receive(Message msg) {
        byte[] buffer = (byte[]) msg.obj;
        if (Hex_show == true) {
            String readMessage = " "
                    + Data.bytesToHexString(buffer, msg.arg1);
            re_data_show.append(readMessage);
            if (Auto_huang == true) {
                re_data_show.append("\n");
            }
            countin += readMessage.length() / 2;                               // 接收计数
            re_count.setText("" + countin + "个");
        } else if (Hex_show == false) {
            String readMessage = null;
            try {
                readMessage = new String(buffer, 0, msg.arg1, "GBK");
                if (readMessage=="CLOSE"){
                 switchhahah.setChecked(false);}
                if (readMessage=="OPEN"){
                    switchhahah.setChecked(true);}//返回数据分析
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            re_data_show.append(readMessage);

            if (Auto_huang == true) {
                re_data_show.append("\n");
            }
            countin += readMessage.length();                                   // 接收计数
            re_count.setText("" + countin + "个");
        }
    }

    //发送数据处理分析函数，通过handler从子线程回传主线程
    private void Handler_send(Message msg) {
        byte[] writeBuf = (byte[]) msg.obj;
        if (Auto_send == true) {
            String s = edit_time.getText().toString();
            long t = Long.parseLong(s);
            ser_handler.postDelayed(runnable, t);
        } else if (Auto_send == false) {
            ser_handler.removeCallbacks(runnable);
        }

        if (Hex_send == true) {
            String writeMessage = Data.Bytes2HexString(writeBuf);
            countout += writeMessage.length() / 2;
            se_count.setText("" + countout + "个");
        } else if (Hex_send == false) {
            String writeMessage = null;
            try {
                writeMessage = new String(writeBuf, "GBK");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            countout += writeMessage.length();
            se_count.setText("" + countout + "个");
        }
    }

    //目的地址和目的主机名清空函数
    private void Ip_clear() {
        ip_show.setText("");
        name_show.setText("");
    }


}
