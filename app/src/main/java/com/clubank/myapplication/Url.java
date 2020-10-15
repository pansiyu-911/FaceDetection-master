package com.clubank.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clubank.R;

public class Url extends AppCompatActivity {
    private EditText editText;
    private Button button;
    public  static String ip;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        editText=findViewById(R.id.set_url);
        button=findViewById(R.id.set_url_but);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(Url.this, Login.class);
                ip= editText.getText().toString();
                startActivity(intent);
            }
        });


    }
}
