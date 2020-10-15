package com.clubank.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.utils.Utils;
import com.clubank.R;

import java.util.HashMap;
import java.util.Map;

public class Change_Password extends AppCompatActivity {
    EditText editText1;
String pathid;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);
        editText1=findViewById(R.id.change_pass_ed1);

        button=findViewById(R.id.pass_but);

    }
    public  void  Submission(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String new_pass = editText1.getText().toString();
                pathid=Url.ip;
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_pwd", new_pass);

                String path = pathid+"/check_by_face_controller/student/updateStudentPassword.do";

                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                String sessionId = sharedPreferences.getString("id", "");

                String result = utils.submitPostData(sessionId, path, params, "utf-8");

                switch (result) {
                    case "1":
                        Looper.prepare();
                        Toast.makeText(Change_Password.this, "修改成功", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(Change_Password.this, Main1Activity.class);
                        startActivity(intent1);
                        Looper.loop();
                        break;
                    case "2":
                        Looper.prepare();
                        Toast.makeText(Change_Password.this, "修改失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                }
            }
        }).start();
    }
}