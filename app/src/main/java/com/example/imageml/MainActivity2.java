package com.example.imageml;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity2 extends Activity {

    String[] num1;
    String[] num2;
    String food = "";
    String f1 = "";
    String f2 = "";
    String f3 = "";
    int count = 0;
    byte[] arr;

    private ImageView imageView;

    class RealTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder sb = new StringBuilder();

//            Et = (EditText)findViewById(R.id.editT);
//            num3 = Et.getText().toString();

            //실시간 검색어를 가져오기위한 String객체(String 과는 차이가 있음)
            Document doc = null;
            try {
                Element content;
                doc = Jsoup.connect("https://www.dietshin.com/calorie/calorie_search.asp?keyword=" + food).get();
                Elements content1 = doc.select("td").select(".subject").select("a");//회차 id값 가져오기

                count = content1.size();
                num1 = new String[count];
                num2 = new String[count];

                for(int i=0; i<count; i++){
                    content = content1.get(i);
                    num1[i] = content.text();
                    num2[i] = content.attr("href");
                    sb.append(num1[i])
                    .append("\n ");
                    Log.e("num1", String.valueOf(num1[i]));
                    Log.e("num2", String.valueOf(num2[i]));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent(MainActivity2.this, ListFN.class);
            intent.putExtra("image",arr);
            intent.putExtra("name",num1);
            intent.putExtra("url",num2);
            intent.putExtra("num",count);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        imageView = findViewById(R.id.imageView);

        Button b1 = (Button) findViewById(R.id.FBtn1);
        Button b2 = (Button) findViewById(R.id.FBtn2);
        Button b3 = (Button) findViewById(R.id.FBtn3);
        Button select = (Button)findViewById(R.id.select);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RealTask().execute();
                food = f1;
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RealTask().execute();
                food = f2;
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RealTask().execute();
                food = f3;
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RealTask().execute();
                EditText Et = (EditText)findViewById(R.id.editT);
                food = Et.getText().toString();
            }
        });

        Intent intent = getIntent();
        arr = intent.getByteArrayExtra("image");
        Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        imageView.setImageBitmap(image);

        f1 = intent.getExtras().getString("food1");
        f2 = intent.getExtras().getString("food2");
        f3 = intent.getExtras().getString("food3");
        b1.setText(f1);
        b2.setText(f2);
        b3.setText(f3);

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