package com.protruly.okhttpdemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    HttpClient httpClient = new HttpClient(MainActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestZhihuTask().execute(this);
            }
        });

    }



    private class RequestZhihuTask extends AsyncTask<Object, Void, List<Object>> {

        private static final String ZHIHU_ZHUANLAN_API = "https://zhuanlan.zhihu.com/api/recommendations/columns";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Object> doInBackground(Object... params) {
            try {
                httpClient.requstHttp();
            }catch (IOException e){
                e.printStackTrace();
            }catch (SecurityException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Object> zhiHuModels) {

        }
    }



}
