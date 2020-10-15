package com.clubank.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.clubank.R;

public class Main4Activity extends AppCompatActivity {
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;


    private void setDefaultFragment(){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content2,new Kaoqin_1Fragment());
        transaction.commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.kaoqin1:
                    transaction.replace(R.id.content2,new Kaoqin_1Fragment());
                    transaction.commit();
                    return true;
                case R.id.kaoqin2:
                    transaction.replace(R.id.content2,new Kaoqin_2Fragment());
                    transaction.commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        BottomNavigationView navView = findViewById(R.id.kaoqin_view);
        setDefaultFragment();
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
