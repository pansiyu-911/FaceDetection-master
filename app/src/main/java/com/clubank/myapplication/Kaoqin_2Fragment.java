package com.clubank.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

public class Kaoqin_2Fragment extends Fragment {
    private ListView listView;
    private SimpleAdapter sim_adapter;   //适配器
    private List<Map<String, Object>> datalist;   //数据源
    private EditText editText1;
    public JSONObject object;
    private View view;
    String pathid;
    private TextView textView1;
    private TextView textView2;
    String teacher_name;
    String picture_path;
    String kechengming;
    private ImageView imageView;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_kaoqin_2, container, false);

        textView1=view.findViewById(R.id.kaoqin2_tv1);
        textView2=view.findViewById(R.id.kaoqin2_tv2);
        imageView=view.findViewById(R.id.kaoqin2_image);

        //加粗文字
        TextView tv1 = (TextView)view.findViewById(R.id.kaoqin2_tv3);
        TextPaint tp1 = tv1.getPaint();
        tp1.setFakeBoldText(true);
        TextView tv2 = (TextView)view.findViewById(R.id.kaoqin2_tv4);
        TextPaint tp2 = tv2.getPaint();
        tp2.setFakeBoldText(true);
        TextView tv3 = (TextView)view.findViewById(R.id.kaoqin2_tv5);
        TextPaint tp3 = tv3.getPaint();
        tp3.setFakeBoldText(true);
        TextView tv4 = (TextView)view.findViewById(R.id.kaoqin2_tv6);
        TextPaint tp4 = tv4.getPaint();
        tp4.setFakeBoldText(true);

        editText1=(EditText) view.findViewById(R.id.kaoqin2_search_edit);

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                sim_adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        datalist=new ArrayList<Map<String, Object>>();
        init();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                view.findViewById(R.id.tv4_1_4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(getActivity(),Main5Activity.class);
                        Bundle b = new Bundle();
                        b.putString("datetime", (String) datalist.get(position).get("考勤时间"));
                        b.putString("state",(String) datalist.get(position).get("考勤状态"));
                        intent.putExtras(b);

                        startActivity(intent);
                        /*sim_adapter.notifyDataSetChanged();//更新listview*/
                    }
                });
            }
        });

        return view;
    }
    private void init() {
        datalist.clear();
        listView=(ListView) view.findViewById(R.id.listview);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences= getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                String sessionId=sharedPreferences.getString("id","");

                pathid=Url.ip;

                Bundle a = getActivity().getIntent().getExtras();

                teacher_name=a.getString("teacher_name");
                picture_path=a.getString("picture_path");
                kechengming=a.getString("kechengming");

                System.out.println(kechengming);



                OkHttpClient okHttpClient=new OkHttpClient();
                //服务器返回的地址

                Request request=new Request.Builder()
                        .url(pathid+"/check_by_face_controller/student/findEveryAttendanceBySid.do")
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

                        //获取到json数据中的array数组里的内容id,datetime,state,change
                        String datetime=object.getString("att_time");
                        String state=object.getString("att_statu");

                        //存入map
                        map.put("序号", i);
                        map.put("考勤时间", datetime);
                        map.put("考勤状态", state);
                        //ArrayList集合
                        datalist.add(map);
                        System.out.println(datalist);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);

            } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    //Handler运行在主线程中(UI线程中)，  它与子线程可以通过Message对象来传递数据
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    sim_adapter=new MyAdapter(getActivity(),datalist,
                            R.layout.activity_main4_1,
                            new String[]{"序号","考勤时间","考勤状态","申请变更"},
                            new int[]{R.id.tv4_1_1,R.id.tv4_1_2, R.id.tv4_1_3,R.id.tv4_1_3});
                    listView.setAdapter(sim_adapter);

                    textView1.setText(kechengming);
                    textView2.setText(teacher_name);
                    Glide
                            .with(Kaoqin_2Fragment.this)
                            .load(picture_path)
                            .into(imageView);

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

                convertView =getLayoutInflater().inflate(R.layout.activity_main4_1,null);
                viewHolder.textView1 = (TextView) convertView.findViewById(R.id.tv4_1_1);
                viewHolder.textView2 = (TextView) convertView.findViewById(R.id.tv4_1_2);
                viewHolder.textView3 = (TextView) convertView.findViewById(R.id.tv4_1_3);
                viewHolder.textView4 = (TextView) convertView.findViewById(R.id.tv4_1_4);


                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            for (int i=0;i<=position;i++){
                Integer x = (Integer) datalist.get(i).get("序号");
               viewHolder.textView1.setText(x.toString());
               viewHolder.textView2.setText(datalist.get(i).get("考勤时间").toString());
               viewHolder.textView3.setText(datalist.get(i).get("考勤状态").toString());
            }
            return convertView;
        }

    }

    final static class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;

    }



}
