package com.f10company.barcodewidgetf10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;
import static com.google.zxing.integration.android.IntentIntegrator.CODE_128;
import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE;

public class ScanQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        IntentIntegrator intentIntegrator =
                new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setPrompt("바코드/QR코드를 인식해주세요.");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //스캔을 하지 못하면
            if (result.getContents() == null) {
                Toast.makeText(this, "카메라 인식 취소", Toast.LENGTH_LONG).show();
                finish();
            }
            //스캔을 완료하면
            else {

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ScanQR.this, MainActivity.class);
                intent.putExtra("codeString", result.getContents());
                intent.putExtra("codeFormat", result.getFormatName());
                if(result.getFormatName().equals(QR_CODE))
                    intent.putExtra("codeNickname", MainActivity.DEFAULT_CODE_QR_NICK);
                else
                    intent.putExtra("codeNickname", MainActivity.DEFAULT_CODE_BAR_NICK);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
