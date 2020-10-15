package com.clubank.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
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

public class MeFragment extends Fragment {
    private TextView text_id;
    private TextView text_name;
    private ImageView imageView;
    private Button me_btu1;
    private Button me_btu2;
    private Button me_btu3;
    private Button me_btu4;
    private View view;
    private String id;
    private String name;
    private String imagepath;
     String pathid;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_me, container, false);

        text_name = view.findViewById(R.id.me_text2);
        text_id = view.findViewById(R.id.me_text4);
        imageView = view.findViewById(R.id.me_1);

        me_btu1 = (Button) view.findViewById(R.id.me_button_1);
        me_btu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), Stu_Name.class);
                startActivity(intent1);
            }
        });
        me_btu2 = (Button) view.findViewById(R.id.me_button_2);
        me_btu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), Change_Password.class);
                startActivity(intent2);
            }
        });

        me_btu3 = (Button) view.findViewById(R.id.me_button_3);
        me_btu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getActivity(), ModifyingHead.class);
                startActivity(intent3);
            }
        });
        me_btu4 = (Button) view.findViewById(R.id.me_button_4);
        me_btu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getActivity(), Main1Activity.class);
                startActivity(intent4);
            }
        });

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

                id = student.getString("student_name");
                name = student.getString("student_id");

                String path=student.getString("student_head_image");
                imagepath=pathid+"/check_by_face_controller/"+path;
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

                    text_id.setText(id);
                    text_name.setText(name);
                    Glide
                            .with(MeFragment.this)
                            .load(imagepath)
                            .into( imageView);

                    break;
            }
        }
    };


}
