package com.example.david.barcodecart;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.barcodecart.Adapters.HistoryProductAdapter;
import com.example.david.barcodecart.Adapters.ProductAdapter;
import com.example.david.barcodecart.Utils.GMailSender;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class CartActivity extends BaseActivity {

    private static final String senderMail = "Jamnerdairyfarm@gmail.com";
    private static final String senderName = "Jamnerdairyfarm";
    private static final String senderPassword = "jamner@123";

    private static final String receiverMail = "Jamnerdairyfarm@gmail.com";

    private ListView listViewToday;
    private ListView listViewHistory;
    private ProductAdapter todayAdapter;
    private HistoryProductAdapter historyAdapter;

    private TextView txtToday;
    private TextView txtIconToday;
    private TextView txtHistory;
    private TextView txtIconHistory;
    private LinearLayout tabToday;
    private LinearLayout tabHistory;

    private EditText fromDateText;
    private EditText toDateText;

    private LinearLayout layoutScan;
    private TextView txtTitleDate;
    private TextView txtIconScan;

    private TextView txtIconRtn;
    private LinearLayout layoutRtn;
    private Button btnSave;

    private String fromDate;
    private String toDate;

    private LinearLayout layoutToday;
    private LinearLayout layoutHistory;

    public static final String TAG = "com.unarin.beacon";
    private static final int REQUEST_SMS = 0;

    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initializeObjects();

        saveData();
        readData();
        getTodayData();
        filterData(fromDate, toDate);

        calcMonthSales();

        findViews();
        setFormula();
        setEvents();
        setupToolbar();

    }

    private void initializeObjects(){

        todayAdapter = new ProductAdapter(this, R.layout.product_detail);
        historyAdapter = new HistoryProductAdapter(this, R.layout.product_detail);
        todayAdapter.setMyActivity(this);
        historyAdapter.setMyActivity(this);

        fromDate = getLastMothDate(getCurrentDate());
        toDate = getCurrentDate();
    }
    private void findViews(){

        listViewToday = (ListView) findViewById(R.id.listTodayProducts);
        listViewHistory = (ListView) findViewById(R.id.listHistoryProducts);
        fromDateText = (EditText) findViewById(R.id.fromDateText);
        toDateText = (EditText) findViewById(R.id.toDateText);

        txtToday = (TextView) findViewById(R.id.txtToday);
        txtIconToday = (TextView) findViewById(R.id.txtIconToday);
        txtHistory = (TextView) findViewById(R.id.txtHistory);
        txtIconHistory = (TextView) findViewById(R.id.txtIconHistory);

        tabToday = (LinearLayout) findViewById(R.id.tabToday);
        tabHistory = (LinearLayout) findViewById(R.id.tabHistory);
        layoutToday = (LinearLayout) findViewById(R.id.layoutToday);
        layoutHistory = (LinearLayout) findViewById(R.id.layoutHistory);

        txtIconRtn = (TextView) findViewById(R.id.txtIconRtn);
        layoutRtn = (LinearLayout) findViewById(R.id.layoutRtn);
        btnSave = (Button) findViewById(R.id.btnSave);

        txtTitleDate = (TextView) findViewById(R.id.txtTitleDate);
        layoutScan = (LinearLayout) findViewById(R.id.layoutScanButton_Cart);
        txtIconScan = (TextView) findViewById(R.id.txtIconScanButton_Cart);
    }

    private void setFormula(){
        txtIconToday.setTypeface(iconFont);
        txtIconHistory.setTypeface(iconFont);
        txtIconRtn.setTypeface(iconFont);
        btnSave.setTypeface(iconFont);
        txtIconScan.setTypeface(iconFont);
    }

    private void setEvents(){

        String today = getCurrentDate();

        tabToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutToday.setVisibility(View.VISIBLE);
                layoutHistory.setVisibility(View.GONE);
                btnSave.setVisibility(View.INVISIBLE);
                txtTitleDate.setText("Time");

                tabToday.setBackgroundColor(getResources().getColor(R.color.colorMyGray_1));
                txtToday.setTypeface(Typeface.DEFAULT_BOLD);
                txtToday.setTextColor(getResources().getColor(R.color.colorMyBlack));
                txtIconToday.setTextColor(getResources().getColor(R.color.colorMyRed));

                tabHistory.setBackgroundColor(getResources().getColor(R.color.colorMyGray_5));
                txtHistory.setTypeface(Typeface.DEFAULT);
                txtHistory.setTextColor(getResources().getColor(R.color.colorMyGray_9));
                txtIconHistory.setTextColor(getResources().getColor(R.color.colorMyGray_9));

            }
        });

        tabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutToday.setVisibility(View.GONE);
                layoutHistory.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                txtTitleDate.setText("Date & Time");

                tabToday.setBackgroundColor(getResources().getColor(R.color.colorMyGray_5));
                txtToday.setTypeface(Typeface.DEFAULT_BOLD);
                txtToday.setTextColor(getResources().getColor(R.color.colorMyGray_9));
                txtIconToday.setTextColor(getResources().getColor(R.color.colorMyGray_9));

                tabHistory.setBackgroundColor(getResources().getColor(R.color.colorMyGray_1));
                txtHistory.setTypeface(Typeface.DEFAULT);
                txtHistory.setTextColor(getResources().getColor(R.color.colorMyBlack));
                txtIconHistory.setTextColor(getResources().getColor(R.color.colorMyDarkBlue));
            }
        });


        fromDateText.setText(getLastMothDate(today));
        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEditTextDatePicker datePicker = new MyEditTextDatePicker(CartActivity.this, R.id.fromDateText);

            }
        });

        fromDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                fromDate = editable.toString();
                filterData(fromDate, toDate);
                historyAdapter.setLists(filteredProductList);
                listViewHistory.setAdapter(historyAdapter);
            }
        });

        toDateText.setText(today);
        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEditTextDatePicker datePicker = new MyEditTextDatePicker(CartActivity.this, R.id.toDateText);

            }
        });

        toDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                toDate = editable.toString();
                filterData(fromDate, toDate);
                historyAdapter.setLists(filteredProductList);
                listViewHistory.setAdapter(historyAdapter);
            }
        });

        todayAdapter.setLists(scanList);
        historyAdapter.setLists(filteredProductList);
        listViewToday.setAdapter(todayAdapter);
        listViewHistory.setAdapter(historyAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CartActivity.this).setTitle("Message")
                        .setMessage("Do you really want to export bill doc to Excel?")
                        .setNegativeButton("No", null)
                        .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            exportExcel();
                            }
                        }).create().show();

            }
        });

        layoutRtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layoutScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(BarcodeScanActivity.class);
                finish();
                flag = 1;
            }
        });
    }

    private void calcMonthSales(){

        if (oneProduct == null) return;

        getNowMonthDate(getCurrentDate());

        double allQty = 0.0;
        double allPrice = 0.0;
        for ( int i = 0; i < nowMonthProductList.size(); i++){
            if (oneProduct.getCode().equals(nowMonthProductList.get(i).getCode())) {
                allQty += nowMonthProductList.get(i).getQty();
                allPrice += nowMonthProductList.get(i).getPrice();
            }
        }

        String message = "Thanks "+ oneProduct.getName().split(" ")[0] +".Your outstanding for " + getMonthName() + " is " + allPrice + getResources().getString(R.string.inr) + "-Jamner Dairy Farm";

        sendSMS(oneProduct.getCode(), message);

    }

    private String getMonthName(){
        switch (Integer.valueOf(getCurrentDate().substring(5, 7))){
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "this month";
    }

    public void sendSMS(String phoneNo, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();


            SmsManager sms = SmsManager.getDefault();
            // if message length is too long messages are divided
            List<String> messages = sms.divideMessage(message);

            for (String msg : messages) {
                smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            }

            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void exportExcel(){
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("mh permission error...", "Storage not available or read only");
            return;
        }

        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "bill.xls";

        final File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;

            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("productList", 0);

            // column and row
            try {
                sheet.addCell(new Label(0, 0, "Phone Number"));
                sheet.addCell(new Label(1, 0, "Name"));
                sheet.addCell(new Label(2, 0, "Quality"));
                sheet.addCell(new Label(3, 0, "Price"));
                sheet.addCell(new Label(4, 0, "Date"));

                for (int i = 0; i < filteredProductList.size(); i++) {
                    sheet.addCell(new Label(0, i + 1, filteredProductList.get(i).getCode()));
                    sheet.addCell(new Label(1, i + 1, filteredProductList.get(i).getName()));
                    sheet.addCell(new Label(2, i + 1, String.valueOf(filteredProductList.get(i).getQty())));
                    sheet.addCell(new Label(3, i + 1, String.valueOf(filteredProductList.get(i).getPrice())));
                    sheet.addCell(new Label(4, i + 1, filteredProductList.get(i).getDate()));
                }
            } catch (RowsExceededException e) {
                e.printStackTrace();
            }
            catch (WriteException e) {
                    e.printStackTrace();
            }

            //closing cursor
            workbook.write();


            try {
                workbook.close();
            }
            catch (WriteException e) {
                e.printStackTrace();
            }

            sendMail(receiverMail, "Send Bill File", "This mail has been sent from FamnerDairyFarm android app along with attachment");

//          open excel file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(CartActivity.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }


    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderMail, senderPassword);
            }
        });
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws
            MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderMail, senderName));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);

        String filename = Environment.getExternalStorageDirectory() + "/bill.xls";
        Multipart _multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);

        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
        message.setContent(_multipart);

        return message;
    }


    public class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CartActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(CartActivity.this, "Mail sent!!!", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        protected Void doInBackground(javax.mail.Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void autoSendMail(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender(senderMail, senderPassword);
                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/bill.xls");
                    sender.sendMail("Send Excel File", "This mail has been sent from FamnerDairyFarm android app along with attachment", senderMail, receiverMail);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Mail Sending Error", Toast.LENGTH_LONG).show();
                }
            }

        }).start();
    }

}
