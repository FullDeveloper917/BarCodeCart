package com.example.david.barcodecart;






import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.david.barcodecart.Adapters.Product;
import com.example.david.barcodecart.Utils.FontManager;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by David on 9/17/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public static String lastScanTime = "";

    public static DatabaseHelper myDb;
    public static List<Product> scanList = new ArrayList<>();
    public static List<Product> allProductList = new ArrayList<>();
    public static List<Product> filteredProductList = new ArrayList<>();
    public static List<Product> nowMonthProductList = new ArrayList<>();

    private static final int BUILD_VERSION_CODES_M = 23;
    public static Typeface iconFont;

    public static final int ZXING_CAMERA_PERMISSION = 1;
    public static Class<?> mClss;

    public static Product oneProduct;


    public static int flag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tryToRequestMarshmallowReadPhoneStatePermission();
        tryToRequestMarshmallowExternalStoragePermission();
        tryToRequestMarshmallowSMSPermission();

        myDb = new DatabaseHelper(this);
        iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public int getProductIndex(Product oneProduct) {
        Log.i("scan", "" + scanList.size());
        for (int i = 0; i < scanList.size(); i++) {
            if (scanList.get(i).getCode().equals(oneProduct.getCode())) {
                return i;
            }
        }
        return -1;
    }

    public String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    public String getLastMothDate(String today){
        String result = "";

        int yearOne = Integer.valueOf(today.substring(0, 4));
        int monthOne = Integer.valueOf(today.substring(5, 7));
        int dayOne = Integer.valueOf(today.substring(8, 10));

        if (dayOne == 31) dayOne = 1;
        if (monthOne == 3 && (dayOne == 29 && dayOne ==30 && dayOne == 31)) {
            dayOne = 28;
        }

        if (monthOne == 1) {
            monthOne = 12;
            yearOne--;
        }
        else
            monthOne--;

        result += String.valueOf(yearOne);

        result += "-";
        if (monthOne < 10)
            result += ( "0" + String.valueOf(monthOne));
        else
            result += String.valueOf(monthOne);

        result += "-";
        if (dayOne < 10)
            result += ( "0" + String.valueOf(dayOne));
        else
            result += String.valueOf(dayOne);
        return result;
    }

    public String getCurrentTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return df.format(c.getTime());
    }

    public void saveData(){
        for (int i = 0; i < scanList.size(); i++){
            if (!myDb.insertData(scanList.get(i))) {
                Toast.makeText(this, "Data save failed!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void readData(){
        allProductList.clear();
        Cursor res = myDb.getData();
        if (res.getCount() == 0) {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
        while (res.moveToNext()){
            Product oneProduct = new Product();
            oneProduct.setName(res.getString(1));
            oneProduct.setQty(res.getDouble(2));
            oneProduct.setPrice(res.getDouble(3));
            oneProduct.setDate(res.getString(4));
            oneProduct.setCode(res.getString(5));
            oneProduct.setTime(res.getString(6));
            allProductList.add(oneProduct);
        }
        res .close();
    }

    public void filterData(String fromDate, String toDate){
        filteredProductList.clear();
        for (int i = 0; i < allProductList.size(); i++){
            String dataDate = allProductList.get(i).getDate();
            if (isBefore(fromDate, dataDate) && isBefore(dataDate, toDate)) {
                filteredProductList.add(allProductList.get(i));
            }
        }
    }

    public void getNowMonthDate(String nowDate){
        String firstDate = nowDate.substring(0, 9) + "01";
        nowMonthProductList.clear();
        for (int i = 0; i < allProductList.size(); i++){
            String dataDate = allProductList.get(i).getDate();
            if (isBefore(firstDate, dataDate) && isBefore(dataDate, nowDate)) {
                nowMonthProductList.add(allProductList.get(i));
            }
        }
    }

    public void getTodayData(){
        scanList.clear();
        for (int i = 0; i< allProductList.size(); i++){
            String dataDate = allProductList.get(i).getDate();
            if (isBefore(getCurrentDate(), dataDate) && isBefore(dataDate, getCurrentDate())) {
                scanList.add(allProductList.get(i));
            }
        }
    }

    public boolean isBefore(String oneDate, String twoDate){
        int yearOne = Integer.valueOf(oneDate.substring(0, 4));
        int monthOne = Integer.valueOf(oneDate.substring(5, 7));
        int dayOne = Integer.valueOf(oneDate.substring(8, 10));

        int yearTwo = Integer.valueOf(twoDate.substring(0, 4));
        int monthTwo = Integer.valueOf(twoDate.substring(5, 7));
        int dayTwo = Integer.valueOf(twoDate.substring(8, 10));

        if (yearOne < yearTwo) return true;
        else if (yearOne > yearTwo) return false;
        else{
            if (monthOne < monthTwo) return true;
            else if (monthOne > monthTwo) return false;
            else {
                if (dayOne > dayTwo) return false;
                else return true;
            }
        }
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void tryToRequestCameraPermission() {

        if (Build.VERSION.SDK_INT < 23) return;

        final Method checkSelfPermissionMethod = getCheckSelfPermissionMethod();

        if (checkSelfPermissionMethod == null) return;

        try {

            final Integer permissionCheckResult = (Integer) checkSelfPermissionMethod.invoke(this, Manifest.permission.CAMERA);
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) return;

            final Method requestPermissionsMethod = getRequestPermissionsMethod();
            if (requestPermissionsMethod == null) return;

            requestPermissionsMethod.invoke(this, new String[]{Manifest.permission.CAMERA}, 1);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Method getCheckSelfPermissionMethod() {
        try {
            return Activity.class.getMethod("checkSelfPermission", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Method getRequestPermissionsMethod() {
        try {
            final Class[] parameterTypes = {String[].class, int.class};

            return Activity.class.getMethod("requestPermissions", parameterTypes);

        } catch (Exception e) {
            return null;
        }
    }

    public void tryToRequestMarshmallowExternalStoragePermission() {

        if (Build.VERSION.SDK_INT < BUILD_VERSION_CODES_M) return;

        final Method checkSelfPermissionMethod = getCheckSelfPermissionMethod();

        if (checkSelfPermissionMethod == null) return;

        try {

            final Integer permissionCheckResult = (Integer) checkSelfPermissionMethod.invoke(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) return;

            final Method requestPermissionsMethod = getRequestPermissionsMethod();
            if (requestPermissionsMethod == null) return;

            requestPermissionsMethod.invoke(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void tryToRequestMarshmallowSMSPermission() {

        if (Build.VERSION.SDK_INT < BUILD_VERSION_CODES_M) return;

        final Method checkSelfPermissionMethod = getCheckSelfPermissionMethod();

        if (checkSelfPermissionMethod == null) return;

        try {

            final Integer permissionCheckResult = (Integer) checkSelfPermissionMethod.invoke(this, Manifest.permission.SEND_SMS);
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) return;

            final Method requestPermissionsMethod = getRequestPermissionsMethod();
            if (requestPermissionsMethod == null) return;

            requestPermissionsMethod.invoke(this, new String[]{Manifest.permission.SEND_SMS}, 1);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void tryToRequestMarshmallowReadPhoneStatePermission() {

        if (Build.VERSION.SDK_INT < BUILD_VERSION_CODES_M) return;

        final Method checkSelfPermissionMethod = getCheckSelfPermissionMethod();

        if (checkSelfPermissionMethod == null) return;

        try {

            final Integer permissionCheckResult = (Integer) checkSelfPermissionMethod.invoke(this, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) return;

            final Method requestPermissionsMethod = getRequestPermissionsMethod();
            if (requestPermissionsMethod == null) return;

            requestPermissionsMethod.invoke(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}

