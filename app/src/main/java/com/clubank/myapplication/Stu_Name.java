package com.clubank.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.clubank.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Stu_Name extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    public JSONObject object;
    String student_name;
    String student_sex;
    String  student_id;
    String department;
    String major;
    String class_name;
    String pathid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu__name);

        textView1 = findViewById(R.id.tv_name_1);
        textView2 = findViewById(R.id.tv_name_2);
        textView3 = findViewById(R.id.tv_name_3);
        textView4 = findViewById(R.id.tv_name_4);
        textView5 = findViewById(R.id.tv_name_5);
        textView6 = findViewById(R.id.tv_name_6);

        init();

    }

    private void init() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                String sessionId = sharedPreferences.getString("id", "");
                //服务器返回的地址

                pathid=Url.ip;
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(pathid+"/check_by_face_controller/student/findStudentBySid.do")
                        .addHeader("Cookie", sessionId).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    //获取到数据
                    String date = response.body().string();
                    System.out.println(date);
                    //把数据传入解析josn数据方法
                    jsonJX(date);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void jsonJX(String date) {
        //判断数据是空
        if (date != null) {

            //获取到json数据中里的array对象内容
            try {
               JSONObject student =new JSONObject(date);

                try {
                    Message message = new Message();
                    message.what = 1;
                    // 发送消息到消息队列中
                    handler.sendMessage(message);
                 //获取到json数据中的array数组里的内容
                   student_name=student.getString("student_name");
                   student_sex=student.getString("student_sex");
                   student_id=student.getString("student_id");
                   department=student.getString("department");
                   major=student.getString("major");
                   class_name=student.getString("class_name");


                } catch (JSONException e) {
              e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    textView1.setText(student_name);
                    textView2.setText(student_sex);
                    textView3.setText(student_id);
                    textView4.setText(department);
                    textView5.setText(major);
                    textView6.setText(class_name);
                    break;
            }
        }
    };

}