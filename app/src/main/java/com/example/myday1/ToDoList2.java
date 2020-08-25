package com.example.myday1;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ToDoList2 extends AppCompatActivity {

    private String curDate=MainActivity.DATE;

    public static ArrayList<ListViewItem> sendArr = new ArrayList<ListViewItem>();
    private ListView listView;
    private MyAdapter Adapter;
    private String doingnow, start_time, finish_time; //지금 하고 있는 것
    Button btn3, btn1, btn2, btn4;
    private TextView output;
    int color=-8331542;
    private Button completebtn,stopbtn;
    public static boolean flag=true;
    TextView tv1;
    final static int Init = 0;
    final static int Run = 1;
    final static int Pause = 2;

    int cur_Status; //현재의 상태를 저장할변수를 초기화함.
    int myCount = 1;
    public static long myBaseTime;
    long myPauseTime;

    public NotificationManager manager;
    public NotificationCompat.Builder builder;
    SharedPreferences pref;

  private IMyTimerService binder;
  private ServiceConnection connection=new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
          binder=IMyTimerService.Stub.asInterface(service);
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {

      }
  };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_list2);
        saveData2();
        loadData();
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn4 = (Button)findViewById(R.id.btn4);
        output = findViewById(R.id.time_out);
        completebtn = findViewById(R.id.completebtn);
        stopbtn = findViewById(R.id.stopbtn);
        tv1 = findViewById(R.id.saying);
      pref=getSharedPreferences("pref", MODE_PRIVATE);
        color = pref.getInt("key2", -8331542);


        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(color);
        }


        Drawable iv_btn=btn1.getBackground();
        ColorFilter filter=new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        iv_btn.setColorFilter(filter);
        //btn1.setAlpha(0.66f);

        iv_btn=btn2.getBackground();
        iv_btn.setColorFilter(filter);
       // btn2.setAlpha(0.75f);

        iv_btn=btn3.getBackground();
        iv_btn.setColorFilter(filter);
        //btn3.setAlpha(0.84f);

        iv_btn=btn4.getBackground();
        iv_btn.setColorFilter(filter);
        //btn4.setAlpha(0.93f);

        iv_btn=completebtn.getBackground();
        iv_btn.setColorFilter(filter);

        iv_btn=stopbtn.getBackground();
        iv_btn.setColorFilter(filter);


        iv_btn=tv1.getBackground();
        iv_btn.setColorFilter(filter);
        tv1.setTextColor(Color.BLACK);

        completebtn.setAlpha(0.75f);
        stopbtn.setAlpha(0.75f);

        completebtn.setTextColor(Color.BLACK);
        stopbtn.setTextColor(Color.BLACK);

        int list = ((MainActivity)MainActivity.context).list;
        Resources resources = getResources();
        String []arr = resources.getStringArray(R.array.goodsaying);
        tv1 = (TextView)findViewById(R.id.saying);
        tv1.setText(arr[list]);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), list_3page.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), colorchange.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });



        Adapter = new MyAdapter(this, R.layout.to_do_list2_listview, ToDoList1.list);

        listView = findViewById(R.id.list);
        listView.setAdapter(Adapter);
        listView.invalidate();

    }



    class MyAdapter extends BaseAdapter implements View.OnClickListener {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<String> string;
        private int layout;
        AlertDialog.Builder alert;

        public MyAdapter(Context context, int alayout, ArrayList<String> string) {
            this.context = context;
            this.string = string;
            layout = alayout;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return string.size();
        }

        @Override
        public Object getItem(int position) {
            return string.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            if (convertView == null) convertView = inflater.inflate(layout, parent, false);

            TextView txt = (TextView) convertView.findViewById(R.id.listtext);
            txt.setText(string.get(position));
            txt.setSelected(true);

            Button first = (Button) convertView.findViewById(R.id.first);
            first.setOnClickListener(this);

            Drawable iv_btn=first.getBackground();
            ColorFilter filter=new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            iv_btn.setColorFilter(filter);


            final View finalConvertView = convertView;
            first.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert = new AlertDialog.Builder(context);
                    alert.setTitle("프로그램");
                    alert
                            .setMessage("시작할까?")
                            .setCancelable(false)
                            .setPositiveButton("시작", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //intent(pos);
                                    doingnow=(String)Adapter.getItem(pos);
                                    TextView tt1=(TextView)finalConvertView.findViewById(R.id.listtext);
                                    tt1.setTextColor(color);

                                    myBaseTime = SystemClock.elapsedRealtime();
                                  // myTimer.sendEmptyMessage(0);
                                    Intent intent=new Intent(ToDoList2.this, MyTimerService.class);
                                    bindService(intent,connection,BIND_AUTO_CREATE);
                                    cur_Status=Run;
                                    new Thread(new GetTimerThread()).start();

                                    long now=System.currentTimeMillis();
                                    Date mDate=new Date(now);
                                    SimpleDateFormat simpleDate=new SimpleDateFormat("HH:mm:ss");
                                    start_time=simpleDate.format(mDate);


                                    boolean noti = PreferenceManager.getBoolean(ToDoList2.this, "alert");
                                    if(noti) showNoti();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }

            });

            Button last;
            last = convertView.findViewById(R.id.last);
            iv_btn=last.getBackground();
            filter=new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
            iv_btn.setColorFilter(filter);

            last.setOnClickListener(this);
            last.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int count;
                    final int[] checked = new int[1];
                    count = Adapter.getCount();

                    alert = new AlertDialog.Builder(context);
                    alert.setTitle("완료");
                    alert
                            .setMessage("다 했어?")
                            .setCancelable(false)
                            .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (count > 0) {
                                        checked[0] = pos;
                                        if (checked[0] > -1 && checked[0] < count) {
                                            Log.i("인덱스: ", String.valueOf(checked[0]));
                                            finalConvertView.setBackgroundColor(Color.WHITE);
                                            ToDoList1.list.remove(checked[0]);
                                            Adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        listView.setBackgroundColor(Color.CYAN);
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
            });

            return convertView;
        }

        @Override
        public void onClick(View v) {

        }
    }



    //데이터 호출
    public void loadData() {
        SharedPreferences preferences = getSharedPreferences("sharedpreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(curDate, null);


        Type type = new TypeToken<ArrayList<ListViewItem>>() {
        }.getType();
        if (gson.fromJson(json, type) != null) {
            sendArr = gson.fromJson(json, type);
        }

    }


    public void saveData2() {
        SharedPreferences preferences = getSharedPreferences("sharedpreferences2", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(ToDoList1.list);
        editor.putString(curDate+"2", json);
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        saveData2();
    }


    //스탑워치 구현
    public void myonclick(View v) {
        switch (v.getId()) {
            case R.id.stopbtn:
                switch (cur_Status) {
                    case Init:
                        flag = true;
                        myBaseTime = SystemClock.elapsedRealtime();

                       // myTimer.sendEmptyMessage(0);
                        stopbtn.setText("일시정지");

                        Intent intent=new Intent(ToDoList2.this, MyTimerService.class);
                        bindService(intent,connection,BIND_AUTO_CREATE);
                        cur_Status=Run;
                        new Thread(new GetTimerThread()).start();
                        cur_Status = Run;
                        break;

                    case Run: //움직이고 있을 때 멈춘다
                        flag = false;
                        unbindService(connection);
                        cur_Status = Pause;
                        //myTimer.removeMessages(0);
                        myPauseTime = SystemClock.elapsedRealtime();
                        stopbtn.setText("시작");

                        break;

                    case Pause: //시작한다
                        flag = true;
                        long now = SystemClock.elapsedRealtime();
                        myBaseTime += (now - myPauseTime);
                        intent=new Intent(ToDoList2.this, MyTimerService.class);
                        bindService(intent,connection,BIND_AUTO_CREATE);

                        cur_Status=Run;
                        new Thread(new GetTimerThread()).start();

                        //  myTimer.sendEmptyMessage(0);

                        stopbtn.setText("일시정지");

                        break;
                }
                break;
            case R.id.completebtn:
                flag = false;
               // myTimer.removeMessages(0);
                unbindService(connection);
                cur_Status=Run;

                long now=System.currentTimeMillis();
                Date mDate=new Date(now);
                SimpleDateFormat simpleDate=new SimpleDateFormat("HH:mm:ss");
                finish_time=simpleDate.format(mDate);

                /**
                 *sendArr에 저장 (한 일, 시작 시각, 나중시각)
                 */
                ListViewItem item=new ListViewItem(doingnow,start_time+","+finish_time);
                sendArr.add(item);

                listView.setAdapter(Adapter);
                saveData1();

                output.setText("00 : 00 : 00");
               // cur_Status = Init;
                stopbtn.setText("시작");
                myCount = 1;

                break;
        }
    }

   /* Handler myTimer = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            output.setText(getTimeOut());
            myTimer.sendEmptyMessage(0);
        }
    };*/

/*    String getTimeOut(int time) {
      //  long now = SystemClock.elapsedRealtime();
        //long outTime = now - myBaseTime;

        long sec = time / 1000;
        long min = sec / 60;
        long hour = min / 60;
        sec = sec % 60;

        String real_outTime = String.format("%02d : %02d : %02d", hour, min, sec);
        return real_outTime;
    }*/

   private class GetTimerThread implements Runnable{

       private Handler handler=new Handler();
       @Override
       public void run() {
           while(cur_Status==Run){
               if(binder==null) continue;
               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       try{
                           output.setText(binder.getCount()+"");
                       } catch (RemoteException e) {
                           e.printStackTrace();
                       }
                   }
               });
               try{
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }
   }

    private void saveData1() {
        SharedPreferences preferences = getSharedPreferences("sharedpreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sendArr);
        editor.putString(curDate, json);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private boolean isScreenOn(){
        PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        return pm.isInteractive();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public void showNoti() {
        builder=null;
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent pintent=PendingIntent.getActivity(getApplicationContext(),0,intent,Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel("channel1","Channel1",NotificationManager.IMPORTANCE_DEFAULT)
            );

        }
        builder = new NotificationCompat.Builder(ToDoList2.this,"channel1")
                .setContentTitle("일정")
                .setSmallIcon(R.drawable.ic_done_black_24dp)
                .setContentText(doingnow)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
               // .setAutoCancel(true)
                .setShowWhen(true);
        if(!isScreenOn()) builder.setContentIntent(pintent);
        manager.notify(1,builder.build());
    }


}
