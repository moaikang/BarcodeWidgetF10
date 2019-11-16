package com.example.barcodewidgetf10;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.suke.widget.SwitchButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //토글 버튼 관련 변수
    SwitchButton switchButton;
    SharedPreferences.Editor prefEditor;
    SharedPreferences prefs;
    Bitmap codeImage;
    private Button scanQRBtn;
    private TextView tv;
    public ImageView iv;
    ArrayList<String> codeString;
    ArrayList<String> codeFormat;
    private ViewPager viewPager ;
    private ImageViewAdapter pagerAdapter ;
    ArrayListSaveByJson temp = new ArrayListSaveByJson();
    Bitmap sub_codeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanQRBtn  = (Button)findViewById(R.id.QRBarcodeBtn);
        tv = (TextView)findViewById(R.id.textview);
        iv = (ImageView)findViewById(R.id.codeImage);
        //////////////////////////////////////////////////////////////

       // SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.


        /*codeString = sf.getString("text",null);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 널를 반환
        codeFormat = sf.getString("format",null);*///@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22222222
        codeString = temp.getStringArrayPref(MainActivity.this,"codeString");
        codeFormat = temp.getStringArrayPref(MainActivity.this,"codeFormat");

        if(codeString.isEmpty())
            tv.setText("바코드/QR코드를 스캔하세요.");
        else {
            //tv.setText(codeString.get(0));
            String tempString="";
            for(int i=0; i<codeString.size(); i++)
            {
                tempString = tempString + " \n " + codeString.get(i);
            }
            tv.setText(tempString);
            createBitMatrix(codeString.get(0), codeFormat.get(0));
        }
        //////////////////////////////////////////////////////////////

        switchInit();
        viewPager = (ViewPager) findViewById(R.id.viewPager) ;
        pagerAdapter = new ImageViewAdapter(this) ;
        viewPager.setAdapter(pagerAdapter) ;

        scanQRBtn.setOnClickListener(new View.OnClickListener() { // 버튼을 클릭했을 시에 수행할 내용
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanQR.class);
                startActivityForResult(intent, 1); //인텐트 다녀 온 후 onActivityResult 호출
            }
        });
    }
    public void switchInit(){ //토글 버튼 시작 함수
        switchButton = findViewById(R.id.swithButton);
        prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());



        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    // DO what ever you want here when button is enabled
                    // 버튼이 ON 일때 하는 동작
                    notification();
                    /////////////////////////////////////////////////////
                    prefEditor.putString("checked", "yes");
                    prefEditor.apply();
                }
                else {
                    // DO what ever you want here when button is disabled
                    //버튼이 OFF 일때 하는 동작

                    /////////////////////////////////////////////////////
                    prefEditor.putString("checked", "false");
                    notification();
                    prefEditor.apply();
                }
            }
        });

        if (prefs.getString("checked", "no").equals("yes")) {
            switchButton.setChecked(true);
        }
        else {
            switchButton.setChecked(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void createBitMatrix(String text, String format) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        //////////////////////////////////
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //////////////////////////디스플레이의 길이를 알기 위한 방법.

        try{
            BarcodeFormat f = BarcodeFormat.valueOf(format);//format과 같은 상수형을 대입
            BitMatrix bitMatrix = multiFormatWriter.encode(text,f, width,400);
            BitMatrix sub_bitMatrix=multiFormatWriter.encode(text, BarcodeFormat.CODE_39,width,200);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Bitmap sub_bitmap=barcodeEncoder.createBitmap(sub_bitMatrix);//notification 커스텀 전용 바코드
            iv.setImageBitmap(bitmap);

            codeImage = bitmap;
            sub_codeImage=sub_bitmap;//커스텀용 이미지
        }catch (Exception e){}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //requestCode : 어느 인텐트를 다녀왔는지 확인하는 것, resultCOde: 다녀온인텐트가 결과를 알려주는 것,
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            if (resultCode == Activity.RESULT_OK) {
                /*for(int i=0; i<codeString.size(); i++)
                {
                    if(codeString.get(i) == data.getExtras().getString("codeString"))
                    {
                        return;
                    }
                }*/
                codeString.add(data.getExtras().getString("codeString"));
                codeFormat.add(data.getExtras().getString("codeFormat"));
                String text = codeString.get(codeString.size()-1); // 사용자가 입력한 저장할 데이터
                String format = codeFormat.get(codeFormat.size()-1); // 스캔한 코드의 형식

                temp.setStringArrayPref(MainActivity.this,"codeString",codeString);
                temp.setStringArrayPref(MainActivity.this,"codeFormat",codeFormat);

                /*
                // Activity가 종료되기 전에 저장한다.
                //SharedPreferences를 sFile이름, 기본모드로 설정
                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sharedPreferences.edit();



                editor.putString("text", text); // key, value를 이용하여 저장하는 형태
                editor.putString("format",format);
                editor.commit();*/

                tv.setText(text);

                createBitMatrix(text,format);
            }
        }
    }

    private void notification() {
        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.custom_view);//커스텀
        customView.setImageViewBitmap(R.id.content_view,sub_codeImage);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default").setOngoing(true);
        //노티피케이션의 객체 선언부분이라고 보면됨
        //        //setOngoing을 하면 고정
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setCustomContentView(customView);
        //builder.build();
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);//이게 위에 setOngoing때문에 작동을 안함.
        // 알림 표시
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);//잠금화면에서도 보이게 하는 역할

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            //builder.setVisibility();
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());

        if (prefs.getString("checked", "no").equals("yes"))
            notificationManager.cancel(1);//취소하는 경우

    }
}
