package com.example.sathish.SafeDrive_pro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sathish on 13-11-2016.
 */

class CustomerDatabaseHelper extends SQLiteOpenHelper
{
    private static final String db_name = "register.db";

    private static final String fav_tb_name= "favorite_table";
    private static final String fav_name ="favorite_name";
    private static final String fav_number ="favorite_number";

    private static final String tb_name = "customer_register_table";
    private static final String f_name ="firstname";
    private static final String l_name="lastname";
    private static final String dob="dateofbirth";
    private static final String bgroup="b_group";
    private static final String gender ="sex";
    private static final String abt_u="abt_u";
    private static final String mobile ="mob_num";
    private static final String email="email";
    private static final String aadhar_no="adrno";
    private static final String c1="contact1";
    private static final String c2="contact2";
    private static final String c3="contact3";
    public static final String id="id";

    CustomerDatabaseHelper(Context context)
    {
        super(context,db_name,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + tb_name + "(firstname text not null,lastname text not null,dateofbirth text not null,b_group text not null,sex text,mob_num text not null,email text not null,adrno text not null,abt_u text not null,contact1 text not null,contact2 text not null, contact3 text not null, id text unique)");
        db.execSQL("create table " + fav_tb_name + "(favorite_name text not null,favorite_number text UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+tb_name);
        db.execSQL("DROP TABLE IF EXISTS "+fav_tb_name);
        onCreate(db);
    }

    boolean insertData(String name1,String name2,String d,String bg,String gen,String mob,String eml,String addrno,String abtu,String con1,String con2,String con3)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(f_name,name1);
        contentValues.put(l_name,name2);
        contentValues.put(dob,d);
        contentValues.put(bgroup,bg);
        contentValues.put(gender,gen);
        contentValues.put(mobile,mob);
        contentValues.put(email,eml);
        contentValues.put(aadhar_no,addrno);
        contentValues.put(abt_u,abtu);
        contentValues.put(c1,con1);
        contentValues.put(c2,con2);
        contentValues.put(c3,con3);
        contentValues.put(id,"1");
        long result = db.insert(tb_name,null,contentValues);
        if(result==-1)
        {
            db.delete(tb_name, "id = ?", new String[] {"1"});
            result = db.insert(tb_name,null,contentValues);
            if(result==-1)
                return false;
        }
        return true;
    }

    Cursor getAllData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return (db.rawQuery("select * from "+tb_name,null));
    }

    boolean favList(String name,String number)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(fav_name,name);
        contentValues.put(fav_number,number);
        long result = db.insert(fav_tb_name,null,contentValues);
        return (result!=-1);
    }

    Integer delFav(String number)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(fav_tb_name, "favorite_number = ?", new String[] {number});
    }

    Cursor getAllFav()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return (db.rawQuery("select * from "+fav_tb_name,null));
    }
}