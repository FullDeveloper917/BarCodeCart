package com.example.david.barcodecart;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.barcodecart.Adapters.Product;
import com.example.david.barcodecart.Utils.GMailSender;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MainActivity extends BaseActivity {

    private TextView txtIconScanButton;
    private TextView txtScanButton;
    private LinearLayout layoutScanButton;
    private TextView txtIconCartButton;
    private TextView txtCartButton;
    private LinearLayout layoutCartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tryToRequestMarshmallowReadPhoneStatePermission();
        tryToRequestMarshmallowExternalStoragePermission();
        tryToRequestMarshmallowSMSPermission();

        readData();
        getTodayData();

        findViews();
        setFormula();
        setEvents();

    }

    private void findViews(){
        txtIconScanButton = (TextView) findViewById(R.id.txtIconScanButton);
        txtScanButton = (TextView) findViewById(R.id.txtScanButton);
        layoutScanButton = (LinearLayout) findViewById(R.id.layoutScanButton);

        txtIconCartButton = (TextView) findViewById(R.id.txtIconCartButton);
        txtCartButton = (TextView) findViewById(R.id.txtCartButton);
        layoutCartButton = (LinearLayout) findViewById(R.id.layoutCartButton);
    }

    private void setFormula(){
        txtIconScanButton.setTypeface(iconFont);
        txtIconCartButton.setTypeface(iconFont);
    }

    private void setEvents(){

        layoutScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(BarcodeScanActivity.class);
                flag = 1;
            }
        });

        layoutCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                scanList.add(new Product("first test", "1234", 10, 10, 30, "2017-09-27"));
//                scanList.add(new Product("two test", "1235", 1, 1, 50, "2017-09-27"));
//                scanList.add(new Product("three test", "1237", 10, 10, 70, "2017-09-27"));
//
//                scanList.add(new Product("David", "+8618240220533", 10, 30, "2017-09-30", "2017-09-30 10:32:20.03"));
                //scanList.add(new Product("David", "8618240305900", 1, 10, "2017-09-30", "2017-09-30 10:32:22.03"));


                launchActivity(CartActivity.class);
                flag = 2;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                        if(flag == 2){
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }



}
