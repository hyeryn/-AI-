package com.example.imageml;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ListFN extends Activity {
    ListView listView;
    List<String> list = new ArrayList<>();
    String[] num1 = new String[20];
    String[] num2 = new String[20];
    byte[] arr;
    int count=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactive_main);
        listView = (ListView)findViewById(R.id.lv1);

        Intent intent = getIntent(); /*데이터 수신*/
        num1 = intent.getStringArrayExtra("name");
        num2 = intent.getStringArrayExtra("url");
        count = intent.getExtras().getInt("num");
        arr = intent.getByteArrayExtra("image");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, num1);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String data = (String) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(ListFN.this, ListFD.class);
                intent.putExtra("url",num2[position]);
                intent.putExtra("image",arr);
                startActivity(intent);
            }
        });

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
