package com.example.david.barcodecart;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by hkg328 on 6/21/2017.
 */

public class MyEditTextDatePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EditText _editText;
    private String _day;
    private String _month;
    private String _year;
    private Context _context;

    public MyEditTextDatePicker(Context context, int editTextViewID)
    {
        Activity act = (Activity)context;
        this._editText = (EditText)act.findViewById(editTextViewID);
        this._editText.setOnClickListener(this);
        this._context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _year = String.valueOf(year);
        monthOfYear++;
        if (monthOfYear < 10)
            _month = "0" + String.valueOf(monthOfYear);
        else
            _month = String.valueOf(monthOfYear);

        if (dayOfMonth < 10)
            _day = "0" + String.valueOf(dayOfMonth);
        else
            _day = String.valueOf(dayOfMonth);

        updateDisplay();
    }
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(_context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    // updates the date in the birth date EditText
    private void updateDisplay() {
        _editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_year).append("-").append(_month).append("-").append(_day)   );
                //.append(_day).append("/").append(_month + 1).append("/").append(_birthYear).append(" "));

    }
}