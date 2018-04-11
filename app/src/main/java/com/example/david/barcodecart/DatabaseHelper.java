package com.example.david.barcodecart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.david.barcodecart.Adapters.Product;

/**
 * Created by David on 9/26/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "product.db";
    public static final String TABLE_NAME = "product_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "NUMBER";
    public static final String COL_4 = "PRICE";
    public static final String COL_5 = "DATE";
    public static final String COL_6 = "CODE";
    public static final String COL_7 = "TIME";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,NUMBER DOUBLE,PRICE DOUBLE,DATE STRING,CODE STRING,TIME STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(Product product){
        SQLiteDatabase db = this.getWritableDatabase();

        Log.i("save_data", product.getDate());
        Log.i("save_data", product.getCode());
        String selection = COL_5 + " LIKE ? AND " + COL_6 + " LIKE ?";
        String[] selectionArgs = { product.getDate(), product.getCode() };

        String[] projection = {COL_1, COL_2, COL_3, COL_4, COL_5, COL_6, COL_7};

        Cursor res = db.query(
                TABLE_NAME,  			            // The table to query
                projection,                         // The columns to return
                selection,                          // The columns for the WHERE clause
                selectionArgs,                     	// The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                null                                // The sort order
        );

        if (res.getCount() > 0){

            res.close();

            Log.i("save_data", "update...");
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, product.getName());
            contentValues.put(COL_3, product.getQty());
            contentValues.put(COL_4, product.getPrice());
            contentValues.put(COL_5, product.getDate());
            contentValues.put(COL_6, product.getCode());
            contentValues.put(COL_7, product.getTime());

            try {
                long result = db.update(
                        TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                Log.i("save_data", "update result - " + result);
                return  (result != -1);
            }
            catch (Exception e) {
                Log.i("save_data", "update error...");
                e.printStackTrace();
                return false;
            }
        }
        else {

            res.close();

            Log.i("save_data", "insert...");
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, product.getName());
            contentValues.put(COL_3, product.getQty());
            contentValues.put(COL_4, product.getPrice());
            contentValues.put(COL_5, product.getDate());
            contentValues.put(COL_6, product.getCode());
            contentValues.put(COL_7, product.getTime());

            try {
                long result = db.insert(
                        TABLE_NAME,
                        null,
                        contentValues);
                return  (result != -1);
            }
            catch (Exception e) {
                Toast.makeText(context, "insert error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
}
