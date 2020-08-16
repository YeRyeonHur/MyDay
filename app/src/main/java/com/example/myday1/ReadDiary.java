package com.example.myday1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReadDiary extends AppCompatActivity {
    int key_int, color=100;
    String KEY;
    Button btn4, btn1, btn2;
    Context context = this;
    ArrayList<String> keys;
    Button btn3;
    //날씨
    TextView tv_degree;
    ImageView iv_weather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_page);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn4 = (Button)findViewById(R.id.btn4);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        color = pref.getInt("key2", 100);
        btn1.setBackgroundColor(color);
        btn1.setAlpha(0.66f);
        btn2.setBackgroundColor(color);
        btn2.setAlpha(0.75f);
        btn3.setBackgroundColor(color);
        btn3.setAlpha(0.84f);
        btn4.setBackgroundColor(color);
        btn4.setAlpha(0.93f);
        //KEY값 받아오기
        Intent it = getIntent();
        key_int = it.getIntExtra("day_check", 0);
        KEY = Integer.toString(key_int);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToDoList1.class);
                startActivity(intent);
                finish();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), list_3page.class);
                startActivity(intent);
                finish();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), colorchange.class);
                startActivity(intent);
                finish();
            }
        });
        //날짜 설정하기
        int year_int = key_int / 10000;
        int month_int = (key_int / 100) % 100;
        int day_int = key_int % 100;
        TextView dtv = (TextView)findViewById(R.id.today_date);
        dtv.setText(year_int + "/" + month_int + "/" + day_int);

        //내용 불러오기
        String DKEY = KEY + "D";
        String DIARY = PreferenceManager.getString(this, DKEY);
        TextView tv = (TextView)findViewById(R.id.Diary_TV);
        tv.setText(DIARY);

        //이모티콘 불러오기
        String EKEY = KEY + "E";
        int emoji_code = PreferenceManager.getInt(this, EKEY);
        ImageView iv = (ImageView)findViewById(R.id.Emoji);
        if(emoji_code == 1){
            iv.setImageResource(R.drawable.laugh);
        }
        else if(emoji_code == 2){
            iv.setImageResource(R.drawable.smile);
        }
        else if(emoji_code == 3){
            iv.setImageResource(R.drawable.emoji);
        }
        else if(emoji_code == 4){
            iv.setImageResource(R.drawable.angry);
        }
     //날씨 불러오기
        //온도 불러오기
        String DeKEY = KEY + "De";
        String Degree = PreferenceManager.getString(this,DeKEY);

        tv_degree = (TextView)findViewById(R.id.today_weather);
        tv_degree.setText(Degree+" ℃");

        //이모티콘 불러오기
        iv_weather = (ImageView)findViewById(R.id.weather_reading);

        String WKEY = KEY + "W";
        int Weather_code = PreferenceManager.getInt(this,WKEY);

        if(Weather_code==1){
            iv_weather.setImageResource(R.drawable.sun);
        }
        else if(Weather_code==2){
            iv_weather.setImageResource(R.drawable.cloudy);
        }
        else if(Weather_code==3){
            iv_weather.setImageResource(R.drawable.cloud);
        }
        else if(Weather_code==4){
            iv_weather.setImageResource(R.drawable.rain);
        }
        else if(Weather_code==5){
            iv_weather.setImageResource(R.drawable.snow);
        }
        else if(Weather_code==6){
            iv_weather.setImageResource(R.drawable.rain_snow);
        }
        else if(Weather_code==7){
            iv_weather.setImageResource(R.drawable.rain);
        }

        final Button bt1 = (Button)findViewById(R.id.modify);
        bt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder ad = new AlertDialog.Builder(ReadDiary.this);
                ad.setTitle("Modify");
                ad.setMessage("수정할까요?");

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et = (EditText)findViewById(R.id.Diary_ET);
                        TextView tv = (TextView)findViewById(R.id.Diary_TV);
                        Button bt2 = (Button)findViewById(R.id.save);
                        Button bt3 = (Button)findViewById(R.id.delete);

                        bt1.setVisibility(View.GONE);
                        bt2.setVisibility(View.VISIBLE);
                        bt3.setVisibility(View.GONE);

                        String document = tv.getText().toString();
                        et.setText(document);

                        tv.setVisibility(View.GONE);
                        et.setVisibility(View.VISIBLE);
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });

        final String ecode = EKEY, dcode = DKEY, decode = DeKEY, wcode = WKEY;
        final Button bt3 = (Button)findViewById(R.id.delete);
        bt3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder ad = new AlertDialog.Builder(ReadDiary.this);
                ad.setTitle("Delete");
                ad.setMessage("삭제할까요?");

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.removeKey(context, dcode);
                        PreferenceManager.removeKey(context, ecode);
                        PreferenceManager.removeKey(context, decode);
                        PreferenceManager.removeKey(context, wcode);

                        //키값 삭제
                        keys = PreferenceManager.getArray(context, "key_list");
                        int index = findIdx(KEY);
                        if(index > -1) keys.remove(index);
                        PreferenceManager.setArray(context, "key_list", keys);

                        //3번 화면으로 넘어가기
                        Intent it = new Intent(context, list_3page.class);
                        startActivity(it);
                        finish();
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });


    }



    public void to_TextView(View v){
        EditText et = (EditText)findViewById(R.id.Diary_ET);
        TextView tv = (TextView)findViewById(R.id.Diary_TV);
        Button bt1 = (Button)findViewById(R.id.modify);
        Button bt2 = (Button)findViewById(R.id.save);
        Button bt3 = (Button)findViewById(R.id.delete);

        bt2.setVisibility(View.GONE);
        bt1.setVisibility(View.VISIBLE);
        bt3.setVisibility(View.VISIBLE);

        String document = et.getText().toString();
        tv.setText(document);

        String DKEY = KEY + "D";
        PreferenceManager.setString(this, DKEY, document);

        et.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
    }

    int findIdx(String n){
        for(int i = 0; i < keys.size(); i++){
            if(keys.get(i).equals(n)) return i;
        }
        return -1;
    }

    //뒤로가기 버튼 눌렀을때 홈으로 이동하기 메소드
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), NewWriting.class);
        startActivity(intent);
        finish();

    }


}
