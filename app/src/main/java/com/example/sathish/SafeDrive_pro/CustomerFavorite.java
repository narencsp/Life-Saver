package com.example.sathish.SafeDrive_pro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sathish on 13-11-2016.
 */

public class CustomerFavorite extends Activity
{
    CustomerDatabaseHelper myDb;
    Button b1,b2,b3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_layout);

        checkAndRequestPermissions();

        myDb = new CustomerDatabaseHelper(this);
        b1=(Button)findViewById(R.id.addFav);
        b2=(Button)findViewById(R.id.delFav);
        b3=(Button)findViewById(R.id.viewFav);
        addFav();
        delFav();
        viewFav();
    }

    public void addFav()
    {
        b1.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(CustomerFavorite.this, CustomerAddFavorite.class);
                    startActivity(intent);
                }
            }
        );
    }

    public void delFav()
    {
        b2.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(CustomerFavorite.this, CustomerDeleteFavorite.class);
                    startActivity(intent);
                }
            }
        );
    }

    public void viewFav()
    {
        b3.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Cursor res = myDb.getAllFav();
                        if (res.getCount() == 0)
                        {
                            showMessage("Error", "Nothing Found");
                            return;
                        }

                        StringBuilder buffer = new StringBuilder();
                        while (res.moveToNext())
                        {
                            buffer.append("Name: " + res.getString(0) + "\n");
                            buffer.append("Number: " + res.getString(1) + "\n\n");
                        }
                        showMessage("CustomerFavorite List", buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title,String Message)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }


    boolean checkAndRequestPermissions()
    {
        int cont = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cont != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }
}
