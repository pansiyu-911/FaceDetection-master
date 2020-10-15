package com.clubank.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.clubank.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Kaoqin_1Fragment extends Fragment {
    private TextView textView1;
    private TextView textView2;
    private TextView kaoqintv_1;
    private TextView kaoqintv_2;
    private TextView kaoqintv_3;
    private TextView kaoqintv_4;
    private TextView kaoqintv_5;
    private TextView kaoqintv_6;
    private ImageView imageView;
    String teacher_name;
    String picture_path;
    String kechengming;

    String pathid;

    String normal;
    String absence;
    String late;
    String casual_leave;
    String sick_leave;
    String sum;
    private View view;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_kaoqin_1, container, false);

        imageView=view.findViewById(R.id.kaoqin1_image);
        textView1 = view.findViewById(R.id.kaoqing1_text1);
        textView2 = view.findViewById(R.id.kaoqin1_text2);

        kaoqintv_1=view.findViewById(R.id.kaoqin1_tv1);
        kaoqintv_2=view.findViewById(R.id.kaoqin1_tv2);
        kaoqintv_3=view.findViewById(R.id.kaoqin1_tv3);
        kaoqintv_4=view.findViewById(R.id.kaoqin1_tv4);
        kaoqintv_5=view.findViewById(R.id.kaoqin1_tv5);
        kaoqintv_6=view.findViewById(R.id.kaoqin1_tv6);


        init();
        return view;
    }
        private void init() {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                    String sessionId = sharedPreferences.getString("id", "");
                    //服务器返回的地址
                    pathid=Url.ip;

                    Bundle a =getActivity().getIntent().getExtras();
                   teacher_name=a.getString("teacher_name");
                    picture_path=a.getString("picture_path");
                    kechengming=a.getString("kechengming");
                    String class_id = a.getString("class");//商品名

                    System.out.println(teacher_name);
                    System.out.println(kechengming);
                    System.out.println(class_id);

                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(pathid+"/check_by_face_controller/student/findAttendanceBySid.do?class_id="+class_id)
                            .addHeader("Cookie",sessionId).build();
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
                    JSONObject object = new JSONObject(date);

                    normal=object.getString("normal");
                    absence=object.getString("absence");
                    late=object.getString("late");
                    casual_leave=object.getString("casual_leave");
                    sick_leave=object.getString("sick_leave");


                  Integer sum1 = Integer.parseInt(normal)+Integer.parseInt(absence)+
                           Integer.parseInt(late)+
                           Integer.parseInt(casual_leave)
                           +Integer.parseInt(sick_leave);
                   sum = sum1.toString();
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
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        textView1.setText(kechengming);
                        textView2.setText(teacher_name);

                        kaoqintv_1.setText(sum);
                        kaoqintv_2.setText(normal);
                        kaoqintv_3.setText(absence);
                        kaoqintv_4.setText(late);
                        kaoqintv_5.setText(casual_leave);
                        kaoqintv_6.setText(sick_leave);

                        Glide
                                .with(Kaoqin_1Fragment.this)
                                .load(picture_path)
                                .into( imageView);

                        break;
                }
            }
        };
    }
