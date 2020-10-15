package com.clubank.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clubank.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class KechengFragment extends Fragment {
    private View view;
    private GridView gridView;
    private MyAdapter sim_adapter;   //适配器
    public JSONObject object;
    private List<Map<String, Object>> datalist;   //数据源
    String pathid;
    static String kengchenming;
    static String laohiming;
    static Bitmap bitmap;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.fragment_kecheng,container,false);
        gridView=(GridView)view.findViewById(R.id.gridView);
        datalist=new ArrayList<Map<String, Object>>();

        init();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case -1:
                        Toast.makeText(getActivity(),"点击错误", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Bundle a = new Bundle();
                        a.putString("class", (String) datalist.get(position).get("class_id"));
                        a.putString("picture_path", (String) datalist.get(position).get("课程图片"));
                        a.putString("kechengming", (String) datalist.get(position).get("课程名"));
                        a.putString("teacher_name",(String) datalist.get(position).get("老师名"));
                        Intent intent=new Intent(getActivity(), Main4Activity.class);
                        intent.putExtras(a);
                        startActivity(intent);
                        break;
                }
            }
        });
        return view;
    }
    private void init() {
        gridView=view.findViewById(R.id.gridView);
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences= getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
                String sessionId=sharedPreferences.getString("id","");

                OkHttpClient okHttpClient=new OkHttpClient();
                pathid=Url.ip;
              //http://192.168.0.3:8899

                //服务器返回的地址
                Request request=new Request.Builder()
                        .url(pathid+"/check_by_face_controller/student/findCourseBySid.do")
                                .addHeader("Cookie",sessionId).build();
                try{
                    Response response=okHttpClient.newCall(request).execute();

                    //获取到数据
                    String date=response.body().string();
                    System.out.println("date"+date);
                    //把数据传入解析josn数据方法
                    jsonJX(date);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void jsonJX(String date){
        //判断数据是空
        if(date!=null) try {

            JSONArray resultJsonArray = new JSONArray(date);

                //遍历
            for (int i = 0; i < resultJsonArray.length(); i++) {
                object = resultJsonArray.getJSONObject(i);
                    Map<String, Object> map = new HashMap<String, Object>();
                    try {

                        String bit1=object.getString("course_picture");
                        String bitmap=pathid+"/check_by_face_controller/"+bit1;
                        System.out.println(bitmap);
                        String course=object.getString("course_name");  //课程名
                        JSONObject teacher = object.getJSONObject("teachers");
                        String teacher_name=teacher.getString("t_name");//老师名
                         String class_id=object.getString("class_id");


                        //存入map
                        map.put("课程图片",bitmap);
                        map.put("课程名", course);
                        map.put("老师名",teacher_name);
                        map.put("class_id",class_id);
                        //ArrayList集合
                        datalist.add(map);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Handler运行在主线程中(UI线程中)，它与子线程可以通过Message对象来传递数据
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    sim_adapter = new MyAdapter(getActivity(),datalist ,
                            R.layout.kecheng_demo,
                            new String[]{"课程图片", "课程名", "老师名"},
                            new int[]{R.id.imageView3_1_0, R.id.textView3_1_0, R.id.textView3_1_1});
                    gridView.setAdapter(sim_adapter);
                    break;
            }
        }
    };



    public class MyAdapter extends SimpleAdapter {



        public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
          }

        @Override
        public int getCount() {
            return datalist.size();

        }

        @Override
        public Object getItem(int position) {
            return datalist.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView =getLayoutInflater().inflate(R.layout.kecheng_demo,null);
                viewHolder.textView1 = (TextView) convertView.findViewById(R.id.textView3_1_0);
                viewHolder.textView2 = (TextView) convertView.findViewById(R.id.textView3_1_1);
                viewHolder.imageView=(ImageView) convertView.findViewById(R.id.imageView3_1_0);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }



            String url = datalist.get(position).get("课程图片").toString();

            viewHolder.textView1.setText(datalist.get(position).get("课程名").toString());
            viewHolder.textView2.setText(datalist.get(position).get("老师名").toString());


            Glide
                    .with(KechengFragment.this)
                    .load(url)
                    .into( viewHolder.imageView);

            return convertView;
        }

    }

    final static class ViewHolder {
        ImageView imageView;
        TextView textView1;
        TextView textView2;
    }



}

