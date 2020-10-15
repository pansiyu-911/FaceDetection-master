package com.clubank.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.clubank.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main5Activity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private Uri imageUri;
    EditText editText1;
    String pathid;
    private final String IMAGE_TYPE = "image/*";

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kaoqin_change_request);
        button=findViewById(R.id.button5_1);
        imageView=findViewById(R.id.imageButton5_1);

        editText1=(EditText) findViewById(R.id.ET5_1);
        pathid=Url.ip;
        initEvent();

    }
    /*public void submission(View view){

        new Thread(new Runnable() {
            @Override
            public void run() {
            //获取用户输入的数据
            String reason = editText1.getText().toString();
            Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
            System.out.println(bitmap);
            Bundle b = getIntent().getExtras();
            String time= b.getString("datetime");
            String state=b.getString("state");
            System.out.println(time);
             System.out.println(state);

                SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
                String sessionId=sharedPreferences.getString("id","");


                Map<String, String> params = new HashMap<String, String>();
            params.put("att_time", time);
            params.put("att_statu",state);
            params.put("reason", reason);
            Map<String,File> paramsfile=new HashMap<String,File>();
            File file=saveImage(bitmap);
            paramsfile.put("picture",file);

            String path=pathid+"/check_by_face_controller/student/addAttendanceChange.do";

                try {
                    String result=UploadUtil.post(sessionId,path,params, paramsfile);
                    switch (result){
                        case "1":
                            Looper.prepare();
                            Toast.makeText(Main5Activity.this,"提交成功",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            break;
                        case "2":
                            Looper.prepare();
                            Toast.makeText(Main5Activity.this,"提交失败，请重新提交",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            break;

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }).start();

    }*/


    public File saveImage(Bitmap bmp) {

        File appDir = (File) getFilesDir();
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = "23"+ ".png";
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

    private void initEvent() {


        editText1.setSelection(editText1.getText().length() , editText1.getText().length());
        editText1.setMovementMethod(ScrollingMovementMethod.getInstance());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择");

        builder.setPositiveButton("拍照",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //系统相册的路径
                        String cameraPath= Environment.getExternalStorageDirectory()
                                + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;

                        String photoName = System.currentTimeMillis() + ".jpg";
                        File outputImage = new File(cameraPath,photoName);



                        try {
                            if(outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.canExecute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //对运行的设备系统进行判断如果低于Android 7.0
                        // 就调用Uri.fromFile()将file独享转化为uri对象
                        if (Build.VERSION.SDK_INT >= 24){
                            //将file转化为封装过得Uri对象
                    /*
                    getUriFroFile()
                    第一个参数是content
                    第二个参数是任何唯一的字符串
                    第三个参数是File对象
                    Android7.0开始意识到直接使用真实地址是不安全的
                    FileProvider是文件内容提供器，
                    使用提供与内容提供器相似的机制来实现输几局的保存。
                    可以选择性的将封装过得Uri共享给外部。
                     */
                            imageUri = FileProvider.getUriForFile(Main5Activity.this,
                                    "com.clubank.myapplication.provider",
                                    outputImage);
                            System.out.println(imageUri);
                        }else {
                            imageUri = Uri.fromFile(outputImage);
                        }

                        //启动相机程序
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        //指定图片的输出地址
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

                        startActivityForResult(intent,TAKE_PHOTO);


                    }
                } );
        builder.setNegativeButton("从相册中选择", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ContextCompat.checkSelfPermission(Main5Activity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main5Activity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void openAlbum(){
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(IMAGE_TYPE);
        if (Build.VERSION.SDK_INT <19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }else {
                    Toast.makeText(this,"You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将图片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        System.out.println(bitmap);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机的版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上的系统使用的这个方法处理图片
                        handleImageOnKitKat(data);
                    }else {
                        //4.4以下的系统使用的图片处理方法
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }

    }


    @SuppressLint("NewApi")
    private void handleImageOnKitKat(Intent data) {

        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            //如果document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://" +
                        "downloads//public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是普通content类型的uri，则使用普通的方法处理
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果使用file类型的uri，直接获取图片的路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri externalContentUri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(externalContentUri,
                null,selection,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            }
            cursor.close();
        }
        return path;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }


}
