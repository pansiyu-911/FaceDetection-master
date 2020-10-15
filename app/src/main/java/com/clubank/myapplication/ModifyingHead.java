package com.clubank.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clubank.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ModifyingHead extends AppCompatActivity {
    ImageView imageView;
    String imagepath;
    String pathid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifying_head);
        imageView = findViewById(R.id.imageView_Head);
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
                JSONObject student = new JSONObject(date);
                System.out.println(student);

                    //获取到json数据中的array数组里的内容
                    String path = student.getString("student_head_image");

                    imagepath = pathid+"/check_by_face_controller/" + path;
                    System.out.println(imagepath);

                    Message message = new Message();
                    message.what = 1;
                    // 发送消息到消息队列中
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        }

        @SuppressLint("HandlerLeak")
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {

                switch (msg.what) {
                    case 1:

                        Glide
                                .with(ModifyingHead.this)
                                .load(imagepath)
                                .into( imageView);

                        break;
                }
            }
        };
}
