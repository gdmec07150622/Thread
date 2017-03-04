package com.example.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;


//两种以上的方法实现在非主线程中对UI进行修改
public class MainActivity extends AppCompatActivity {

    private Button but1;
    private Button but2;
    private EditText address;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
    }

    //初始化界面
    public void findView(){
        but1=(Button)findViewById(R.id.but1);
        but2=(Button)findViewById(R.id.but2);
        address=(EditText)findViewById(R.id.adress);
        but1.setOnClickListener(new OnClickListener());
        but2.setOnClickListener(new OnClickListener());
        iv=(ImageView)findViewById(R.id.imageView);
    }


    //实现监听
    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.but1:
               new Thread(mRunnable).start();
                    break;
                case R.id.but2:
                    aTask at=new aTask();
                    String ad=address.getText().toString();
                    at.execute(ad);
                    break;
                default:
                    break;
            }
        }
    }




    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap=(Bitmap)msg.obj;
            iv.setImageBitmap(bitmap);
        }
    };


    private class aTask extends AsyncTask {

        //后台线程执行时
        @Override
        protected Object doInBackground(Object... params) {
            // 耗时操作
            try {
                URL url = new URL(params[0].toString());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  null;
        }
        //后台线程执行结束后的操作，其中参数result为doInBackground返回的结果
        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
           if(result!=null) {
               iv.setImageBitmap((Bitmap)result);
           }else{
               Toast.makeText(MainActivity.this,"空",Toast.LENGTH_SHORT);
           }
        }
    }


    Runnable mRunnable=new Runnable() {


        @Override
        public void run() {
            try {
                String ad=address.getText().toString();
                URL url=new URL(ad);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                Bitmap bitmap= BitmapFactory.decodeStream(httpURLConnection.getInputStream());

                Message msg=new Message();
                msg.obj=bitmap;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
