package com.example.sathish.SafeDrive_pro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Sathish on 09-03-2017.
 */

public class CustomerAddFavorite extends Activity
{
    String name="",number="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(CustomerAddFavorite.this, GetContact.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        boolean inInserted;
        CustomerDatabaseHelper myDb = new CustomerDatabaseHelper(this);

        if (resultCode == RESULT_OK && data != null)
        {
            name = data.getStringExtra("name");
            number = data.getStringExtra("number");
        }

        if(number.isEmpty())
        {
            Toast.makeText(CustomerAddFavorite.this, "Contact not added to CustomerFavorite", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            inInserted = myDb.favList(name, number);
            if (inInserted)
                Toast.makeText(CustomerAddFavorite.this, "Contact added to CustomerFavorite", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(CustomerAddFavorite.this, "Contact already found in CustomerFavorite", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
