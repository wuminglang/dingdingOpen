package com.example.wml.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Timer timer = new Timer(true);
    private Button btn;
    private EditText start ;
    private String setTime;

    private TimerTask task1 = new TimerTask() {
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };


    private TimerTask task2 = new TimerTask() {
        public void run() {
            Message msg = new Message();
            msg.what = 2;
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
    }


    @Override
    public void onClick(View v) {
        setTime = start.getText().toString();
        if(setTime.length()<=2){
            Random rand = new Random();
            int randNumber = rand.nextInt(59 - 40 + 1) + 40;
            setTime = setTime+String.valueOf(randNumber);
            System.out.println("默认随机setTime"+setTime);
        }
        //timer.schedule(task1, 0, 20*60*1000);//间隔 20 分钟
        if(task1 !=null){
            task1.cancel();
            task1 = new TimerTask() {
                public void run() {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            };
        }
        timer.schedule(task1, 0, 60*1000);//间隔 60秒
        System.out.println("设置完成setTime"+setTime);
        Toast.makeText(MainActivity.this, "设置完成", Toast.LENGTH_SHORT).show();
    }

    private Handler handler  = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             if(msg.what == 1){
                 System.out.println("进入任务完成");
                SimpleDateFormat df = new SimpleDateFormat("HHmmss");//设置日期格式
                String dateTime =df.format(new Date());
                System.out.println("当前检查时间："+dateTime);
                 System.out.println("setTime："+setTime);
                if(dateTime.startsWith(setTime)){
                    System.out.println("准备启动任务");
                    PackageManager packageManager = getPackageManager();
                    String packageName = "com.alibaba.android.rimet";//要打开应用的包名,以钉钉为例
                    // String packageName = "com.tencent.mm";//要打开应用的包名,以微信为例
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
