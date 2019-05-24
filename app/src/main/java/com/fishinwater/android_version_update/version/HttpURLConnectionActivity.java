package com.fishinwater.android_version_update.version;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fishinwater.android_version_update.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_urlconnection);
        //在页面上显示版本信息
        TextView tv_versionName = findViewById(R.id.tv_versionName);
        try {
            tv_versionName.setText("版本名：" + getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {// api 23+ 您需要请求读/写权限，即使它们已经在您的清单中。
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //检测本程序的版本，这里假设从服务器中获取到最新的版本号为3
    public void checkVersion(View view) {
        //如果检测本程序的版本号小于服务器的版本号，那么提示用户更新
        if (getVersionCode() < 3) {// 三是范例 你需要在服务器上存储你版本的信息，以 json 形式获得后比较
            showDialogUpdate();//弹出提示版本更新的对话框

        }else{
            //否则吐司，说现在是最新的版本
            Toast.makeText(this,"当前已经是最新的版本", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 提示版本更新的对话框
     */
    private void showDialogUpdate() {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                // 设置提示框的图标
                        setIcon(R.mipmap.ic_launcher).
                // 设置要显示的信息
                        setMessage("发现新版本！请及时更新").
                // 设置确定按钮
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this, "选择确定哦", 0).show();
                        loadNewVersionProgress();//下载最新的版本程序
                    }
                }).

                // 设置取消按钮,null是什么都不做，并关闭对话框
                        setNegativeButton("取消", null);

        // 生产对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();


    }

    /**
     * 下载新版本程序
     */
    private void loadNewVersionProgress() {
        final String uri="http://47.107.132.227/CETX";//发送该请求则从服务器下载
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        //启动子线程下载任务
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(uri, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    //下载apk失败
//                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }}.start();
    }

    /**
     * 安装apk
     */
    private void installApk(final File file) {
        new Thread(){
            @Override
            public void run() {
                if (file != null) {
                    if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
                        Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), "com.fishinwater.android_version_update.fileProvider", file);//在AndroidManifest中的android:authorities值
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 一定要记得 先 setFlags 在 addFlags 否则 set 会覆盖 add
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(install);
                    } else{
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(install);
                    }
                }
            }
        }.start();
    }


    /**
     * 从服务器获取apk文件的代码
     * 传入网址uri，进度条对象即可获得一个File文件
     * （要在子线程中执行哦）
     */
    public static File getFileFromServer(String uri, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(uri);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            long time= System.currentTimeMillis();//当前时间的毫秒数
            File file = new File(Environment.getExternalStorageDirectory(), time+"CETX.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else{
            return null;
        }
    }


    /*
     * 获取当前程序的版本名
     */
    private String getVersionName() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionName;

    }


    /*
     * 获取当前程序的版本号
     */
    private int getVersionCode() {
        try {

            //获取packagemanager的实例
            PackageManager packageManager = getPackageManager();
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            Log.e("TAG", "版本号" + packInfo.versionCode);
            Log.e("TAG", "版本名" + packInfo.versionName);
            return packInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return  1;
    }

    public static void anctionStart(AppCompatActivity activity){
        Intent intent = new Intent(activity, HttpURLConnectionActivity.class);
        activity.startActivity(intent);
    }
}
