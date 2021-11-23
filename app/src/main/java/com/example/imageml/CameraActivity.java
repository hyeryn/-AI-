package com.example.imageml;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.imageml.ml.LiteModelAiyVisionClassifierFoodV11;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CameraActivity extends AppCompatActivity {
    public static final String TAG = "[IC]CameraActivity";
    public static final int CAMERA_IMAGE_REQUEST_CODE = 1;
    private static final String KEY_SELECTED_URI = "KEY_SELECTED_URI";

    private LiteModelAiyVisionClassifierFoodV11 model;
    int foodnum = 0;
    Bitmap bitmap = null;

    List<String> finalFood = new ArrayList<>();
    String f1 = "";
    String f2 = "";
    String f3 = "";

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nothing);
        getImageFromCamera();

        try {
            model = LiteModelAiyVisionClassifierFoodV11.newInstance(this);

        } catch (IOException e) {
            // TODO Handle the exception
        }

        if(savedInstanceState != null) {
            Uri uri = savedInstanceState.getParcelable(KEY_SELECTED_URI);
            if (uri != null)
                selectedImageUri = uri;
        }

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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String foodHtml = bun.getString("HTML_DATA");

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(foodHtml);
            String tmp = element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();

            finalFood.add(tmp);
            Log.e("list",finalFood.get(finalFood.size()-1));
            foodnum++;

            //Intent intent = new Intent(GalleryActivity.this, MainActivity2.class);

            if (foodnum==1){
                f1 = finalFood.get(finalFood.size() - 1);
            } else if (foodnum==2) {
                f2 = finalFood.get(finalFood.size() - 1);
            } else if (foodnum==3) {
                f3 = finalFood.get(finalFood.size() - 1);
            } else {}
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_SELECTED_URI, selectedImageUri);
    }

    private void getImageFromCamera(){
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg");
        selectedImageUri = FileProvider.getUriForFile(this, getPackageName(), file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        startActivityForResult(intent, CAMERA_IMAGE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK &&
                requestCode == CAMERA_IMAGE_REQUEST_CODE) {

            try {
                if(Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source src = ImageDecoder.createSource(
                            getContentResolver(), selectedImageUri);
                    bitmap = ImageDecoder.decodeBitmap(src);
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), selectedImageUri);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to read Image", ioe);
            }

            if(bitmap != null) {

                // Creates inputs for reference.
                try {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                TensorImage image = TensorImage.fromBitmap(bitmap);

                // Runs model inference and gets result.
                LiteModelAiyVisionClassifierFoodV11.Outputs outputs = model.process(image);
                List<Category> probability = outputs.getProbabilityAsCategoryList();

                Iterator<Category> it = probability.iterator();
                Map<String, Float> foods = new HashMap<>();

                while (it.hasNext()) {
                    Category str = it.next();
                    String food = str.toString();
                    String[] stCut = food.split("\"");

                    if (stCut[2].equals(" (score=0.0)>")) {
                    } else {
                        String[] percent = stCut[2].split("=");
                        try {
                            float num = Float.parseFloat(percent[percent.length - 1].substring(0, percent[percent.length - 1].length() - 2));
                            foods.put(stCut[1], num); // 음식명 저장

                        } catch (Exception e) {
                        }
                    }
                }

                List<String> keySetList = new ArrayList<>(foods.keySet());
                // 내림차순 //
                Collections.sort(keySetList, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return foods.get(o2).compareTo(foods.get(o1));
                    }
                });

                new Thread() {
                    public void run() {
                        for (int i = 0; i<3; i++){
                            String foodHtml = getTranslation(keySetList.get(i)); // 번역 실행
                            //Log.v("food",foodHtml);

                            Bundle bun = new Bundle();
                            bun.putString("HTML_DATA", foodHtml);

                            Message msg = handler.obtainMessage();
                            msg.setData(bun);
                            handler.sendMessage(msg);
                        }
                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                        Bitmap sendBitmap = bitmap;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        intent.putExtra("image",byteArray);
                        intent.putExtra("food1",f1);
                        intent.putExtra("food2",f2);
                        intent.putExtra("food3",f3);
                        startActivity(intent);
                    }
                }.start();


            }
        }
    }

    @Override
    protected void onDestroy() {
        model.close();
        super.onDestroy();
    }

    private static String getTranslation(String text){

        String clientId = "LR5nrMj8pOk85v9rGzPf";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "SPqOlIAT8P";//애플리케이션 클라이언트 시크릿값";

        String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        HttpURLConnection con = connect(apiURL);
        String postParams = "source=en&target=ko&text=" + text; // en -> ko
        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            con.setDoOutput(true);

            // 여기서 오류
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}