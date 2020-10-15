package com.clubank.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


import com.clubank.R;
public class kecheng_me extends AppCompatActivity {
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;

    // 设置默认进来是tab 显示的页面
    private void setDefaultFragment(){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content1,new KechengFragment());
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener m1OnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.kecheng:
                    transaction.replace(R.id.content1,new KechengFragment());
                    transaction.commit();
                    return true;
                case R.id.me:
                    transaction.replace(R.id.content1,new MeFragment());
                    transaction.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kecheng_me);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        setDefaultFragment();
        navView.setOnNavigationItemSelectedListener(m1OnNavigationItemSelectedListener);

    }


}
