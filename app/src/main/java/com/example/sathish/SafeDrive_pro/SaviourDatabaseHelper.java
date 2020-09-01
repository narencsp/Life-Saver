package com.example.sathish.SafeDrive_pro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sathish on 13-11-2016.
 */

class SaviourDatabaseHelper extends SQLiteOpenHelper
{
    private static final String db_name = "register.db";

    private static final String tb_name = "saviour_register_table";
    private static final String f_name ="firstname";
    private static final String l_name="lastname";
    private static final String dob="dateofbirth";
    private static final String gender ="sex";
    private static final String mobile ="mob_num";
    private static final String aadhar_no="adrno";
    private static final String licence_no ="licno";
    public static final String id="id";

    SaviourDatabaseHelper(Context context)
    {
        super(context,db_name,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + tb_name + "(firstname text not null,lastname text not null,dateofbirth text not null,sex text,mob_num text not null,adrno text not null,licno text not null,id text unique)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS"+tb_name);
        onCreate(db);
    }

    boolean insertData(String name1,String name2,String d,String gen,String mob,String addrno,String licno)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(f_name,name1);
        contentValues.put(l_name,name2);
        contentValues.put(dob,d);
        contentValues.put(gender,gen);
        contentValues.put(mobile,mob);
        contentValues.put(aadhar_no,addrno);
        contentValues.put(licence_no,licno);
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

}