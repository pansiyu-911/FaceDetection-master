package com.clubank.facedetection;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.clubank.R;
import com.clubank.myapplication.Main1Activity;
import com.clubank.myapplication.Main2Activity;
import com.clubank.myapplication.Main2_1Activity;
import com.clubank.myapplication.UploadUtil;
import com.clubank.myapplication.Url;
import com.clubank.utils.DrawFacesView;
import com.clubank.view.CameraListener;
import com.clubank.view.CameraPreview;
import com.clubank.view.CircleCameraLayout;
import com.clubank.view.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 10;
    private String[] mPermissions = {Manifest.permission.CAMERA};
    private CircleCameraLayout rootLayout;
    private DrawFacesView facesView;
    private ImageView imageView;
    private TextView result;
    private CameraPreview cameraPreview;
    private boolean hasPermissions;
    private boolean resume = false;//解决home键黑屏问题
    private boolean hasTake = false;//是否已拍照
    String pathid;
    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(R.id.surface);
        facesView = findViewById(R.id.face);
        imageView = findViewById(R.id.image);
        result = findViewById(R.id.result);
        //权限检查
        if (Util.checkPermissionAllGranted(this, mPermissions)) {
            hasPermissions = true;
        } else {
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSION_REQUEST_CODE);
        }
    }

    public void doWork(View view) {
        switch (view.getId()) {
            case R.id.take_photo:
                if (hasTake) {//已拍照
                    cameraPreview.startPreview();
                    hasTake = false;
                } else {
                    cameraPreview.captureImage();//抓取照片
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermissions) {
            startCamera();
            resume = true;
        }
    }

    private void startCamera() {
        if (null != cameraPreview) cameraPreview.releaseCamera();
        cameraPreview = new CameraPreview(this);
        //rootLayout.removeAllViews();
        rootLayout.setCameraPreview(cameraPreview);
        if (!hasPermissions || resume) {
            rootLayout.startView();
        }
        cameraPreview.setCameraListener(new CameraListener() {
            @Override
            public void onCaptured(Bitmap bitmap) {
                if (null != bitmap) {
                    hasTake = true;
                    imageView.setImageBitmap(bitmap);

                    Toast.makeText(MainActivity.this, "拍照成功:" , Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "拍照失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cameraPreview.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if (faces.length > 0) {
                    Camera.Face face = faces[0];
                    Rect rect = face.rect;
                    StringBuffer str = new StringBuffer();
                    str.append("可信度：" + face.score + "\n")
                            .append("face detected: " + faces.length + "\n")
                            .append(" Face 1 Location X: " + rect.centerX() + "\n")
                            .append("Y: " + rect.centerY() + "   " + rect.left + " " + rect.top + " " + rect.right + " " + rect.bottom);
                    result.setText(str.toString());
                    Matrix matrix = updateFaceRect();
                    facesView.updateFaces(matrix, faces);
                } else {
                    // 只会执行一次
                    Toast.makeText(MainActivity.this, "未检测到人脸", Toast.LENGTH_SHORT).show();
                    facesView.removeRect();
                }
            }
        });
    }

    /**
     * 因为对摄像头进行了旋转，所以同时也旋转画板矩阵
     * 详细请查看{@link Camera.Face#rect}
     *
     * @return
     */
    private Matrix updateFaceRect() {
        Matrix matrix = new Matrix();
        Camera.CameraInfo info = new Camera.CameraInfo();
        // Need mirror for front camera.
        boolean mirror = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(90);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(rootLayout.getWidth() / 2000f, rootLayout.getHeight() / 2000f);
        matrix.postTranslate(rootLayout.getWidth() / 2f, rootLayout.getHeight() / 2f);
        return matrix;
    }

    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            for (int grant : grantResults) {  // 判断是否所有的权限都已经授予了
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) { // 所有的权限都授予了
                startCamera();
            } else {// 提示需要权限的原因
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("拍照需要允许权限, 是否再次开启?")
                        .setTitle("提示")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, mPermissions, PERMISSION_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                builder.create().show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != cameraPreview) {
            cameraPreview.releaseCamera();
        }
        rootLayout.release();
    }



   public File saveImage(Bitmap bmp) {

       File appDir = (File) getFilesDir();
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String name=Main1Activity.getEt_username().getText().toString();


        String fileName = name+ ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void upload(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {

                pathid=Url.ip;

                String url= pathid+"/check_by_face_controller/test/testUpload.do";
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                if(bitmap!=null){
                File file=saveImage(bitmap);

                SharedPreferences sharedPreferences=getSharedPreferences("data", Context.MODE_PRIVATE);
                String sessionId=sharedPreferences.getString("id","");

                final String request = UploadUtil.uploadFile( file,url,sessionId);
                    switch (request){
                    case "1":
                        Looper.prepare();
                        Toast.makeText(MainActivity.this,"录入成功",Toast.LENGTH_SHORT).show();

                        Intent intent1=new Intent(MainActivity.this, Main2_1Activity.class);
                        startActivity(intent1);
                        Looper.loop();
                        break;
                    case "2":
                        Looper.prepare();
                        Toast.makeText(MainActivity.this,"录入失败，请重新录入",Toast.LENGTH_SHORT).show();

                        Intent intent2=new Intent(MainActivity.this, Main2Activity.class);
                        startActivity(intent2);
                        Looper.loop();
                        break;
                }
            }
            }
        }).start();
    }
  }