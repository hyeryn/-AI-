package com.example.imageml;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ListFD extends Activity {
    String urlN;
    private TextView tv;

    class RealTask1 extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder sb = new StringBuilder();
            Document doc = null;
            try {
                Element content;

                doc = Jsoup.connect("https://www.dietshin.com" + urlN).get();
                Elements content2 = doc.select("tbody").select("tr").select("th");
                Elements content3 = doc.select("tbody").select("tr").select("td");

                for (int i = 0; i < 3; i++) {
                    content = content2.get(i);
                    sb.append(content.text())
                            .append("  :  ");
                    content = content3.get(i);
                    sb.append(content.text())
                            .append("\n ")
                            .append(" ============================= \n ");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv.setText(s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactive_main1);

        ImageView imageView = findViewById(R.id.Foodimage);
        
        tv = (TextView)findViewById(R.id.tv1);
        Intent intent = getIntent(); /*데이터 수신*/
        urlN = intent.getExtras().getString("url");
        new RealTask1().execute();

        byte[] arr = intent.getByteArrayExtra("image");
        Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        imageView.setImageBitmap(image);

        findViewById(R.id.cv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.cv2).setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.cv3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                startActivity(intent);
            }
        });
    }

}

