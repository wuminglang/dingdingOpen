package com.example.wml.myapplication;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    KeyguardManager keyguardManager;
    PowerManager pm;
    PowerManager.WakeLock wl;
    KeyguardManager.KeyguardLock keyguardLock;
    PackageManager packageManager;
    String packageName = "com.alibaba.android.rimet";//要打开应用的包名,以钉钉为例
    private Timer timer = new Timer(true);
    private Button btn;
    private EditText start;
    private String setTime;
    private Handler handler;

    private TimerTask task1 = new TimerTask() {
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);

        start = (EditText) findViewById(R.id.start);

        packageManager = getPackageManager();
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        keyguardLock = keyguardManager.newKeyguardLock("unLock");

        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    System.out.println("进入任务完成");
                    SimpleDateFormat df = new SimpleDateFormat("HHmmss");//设置日期格式
                    String dateTime = df.format(new Date());
                    System.out.println("当前检查时间：" + dateTime);
                    System.out.println("setTime：" + setTime);
                    if (dateTime.startsWith(setTime)) {
                        System.out.println("准备解锁屏幕");
                        wakeUpAndUnlock();
                        System.out.println("准备启动任务");
                        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
                        if (launchIntentForPackage != null)
                            startActivity(launchIntentForPackage);
                        else
                            Toast.makeText(MainActivity.this, "手机未安装该应用", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        setTime = start.getText().toString();
        if (setTime.length() <= 2) {
            Random rand = new Random();
            int randNumber = rand.nextInt(59 - 40 + 1) + 40;
            setTime = setTime + String.valueOf(randNumber);
            System.out.println("默认随机setTime" + setTime);
        }
        //timer.schedule(task1, 0, 20*60*1000);//间隔 20 分钟
        if (task1 != null) {
            task1.cancel();
            task1 = new TimerTask() {
                public void run() {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            };
        }
        timer.schedule(task1, 0, 60 * 1000);//间隔 60秒
        System.out.println("设置完成setTime" + setTime);
        Toast.makeText(MainActivity.this, "设置完成", Toast.LENGTH_SHORT).show();
    }




    public void wakeUpAndUnlock() { // 获取电源管理器对象
        if (!pm.isScreenOn()) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            wl.acquire(10000);
            // 点亮屏幕
            wl.release(); // 释放
        }
        //keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁

    }
}
