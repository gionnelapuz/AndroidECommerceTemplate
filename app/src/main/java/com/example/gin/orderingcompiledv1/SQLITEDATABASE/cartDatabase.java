package com.example.gin.orderingcompiledv1.SQLITEDATABASE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gin on 5/14/2017.
 */

public class cartDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "CartDB";

    public static final String CARTTABLE = "cart";
    public static final String CARTID = "id";
    public static final String CARTNAME ="name";
    public static final String CARTSIZE ="size";
    public static final String CARTQUANTITY ="quantity";
    public static final String CARTTOTAL ="total";


    private static final int DB_VERSION = 1;

    public cartDatabase(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String cart = "CREATE TABLE " + CARTTABLE + "(" +
                CARTID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CARTNAME + " VARCHAR, "
                + CARTSIZE + " VARCHAR, "
                + CARTQUANTITY + " VARCHAR, "
                + CARTTOTAL + " VARCHAR);";

        db.execSQL(cart);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String cart = "DROP TABLE IF EXIST cart";
        db.execSQL(cart);
        onCreate(db);
    }

    //CART FUNCTIONS
    public boolean addToCart(String name, String size, String quantity, String total){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CARTNAME,name);
        contentValues.put(CARTSIZE,size);
        contentValues.put(CARTQUANTITY,quantity);
        contentValues.put(CARTTOTAL,total);

        long result = db.insert(CARTTABLE,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getCartItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + CARTTABLE, null);
        return data;
    }
    public Cursor getCartSpecificItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT NAME, SIZE, QUANTITY FROM " + CARTTABLE, null);
        return data;
    }
    public void removeCartItem(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + CARTTABLE + " WHERE " + CARTID + "= '" + id + "'");
        database.close();
    }
    public void deleteCartItems()
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + CARTTABLE);
        database.close();
    }


}
