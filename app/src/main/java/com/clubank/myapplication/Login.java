package com.clubank.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clubank.R;

import java.util.HashMap;
import java.util.Map;

public class Login extends Activity {
    private TextView mTextView_result;
    String pathid;

    public static EditText getEt_username() {
        return et_username;
    }

    public static void setEt_username(EditText et_username) {
        Login.et_username = et_username;
    }

    private  static EditText et_username;
    private EditText et_password;
    private Button btn;
    private Button set_ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pathid=Url.ip;
        System.out.println(pathid);
        setContentView(R.layout.login);
        et_password=findViewById(R.id.et_password);
        et_username=findViewById(R.id.et_uesrname);
        btn=findViewById(R.id.bt_log);
        set_ip=findViewById(R.id.set_ip);
        set_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Url.class);
                startActivity(intent);
            }
        });

    }

    public void login(View view){

        new Thread(new Runnable() {
            @Override
            public void run() {

                //获取用户输入的数据
                String stuName = et_username.getText().toString();
                String stuPassword = et_password.getText().toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", stuName);
                params.put("student_pwd", stuPassword);



                String path=pathid+"/check_by_face_controller/student/login.do";

                String resultsum=HttpUtils.submitPostData(path,params, "utf-8");
                String[] results = resultsum.split(":");

                String sessionID = results[1];

                SharedPreferences sharedPreferences=getSharedPreferences("data",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("id",sessionID);
                editor.commit();

                String result = results[0];
                switch (result){
                    case "1":
                        Intent intent1=new Intent(Login.this, kecheng_me.class);
                        startActivity(intent1);
                        break;
                    case "2":
                        Intent intent2=new Intent(Login.this, InputFace.class);
                        startActivity(intent2);
                        break;
                    case "3":
                        Looper.prepare();
                        Toast.makeText(Login.this,"用户名不正确",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                    case "4":
                        Looper.prepare();
                       Toast.makeText(Login.this,"密码不正确",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                }
            }
        }).start();
    }


}