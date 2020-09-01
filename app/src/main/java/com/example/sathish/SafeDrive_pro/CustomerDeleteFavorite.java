package com.example.sathish.SafeDrive_pro;

import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Sathish on 13-11-2016.
 */

public class CustomerDeleteFavorite extends Activity
{
    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;
    CustomerDatabaseHelper myDb;
    Boolean res=true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);

        TextView head=(TextView)findViewById(R.id.text);
        head.setText("Favorite List");

        ImageButton img=(ImageButton)findViewById(R.id.addcontact);
        img.setVisibility(View.GONE);

        myDb = new CustomerDatabaseHelper(this);

        mMatrixCursor = new MatrixCursor(new String[] { "_id", "name", "details" });

        mAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.contact_button, null, new String[] { "name", "details" }, new int[] { R.id.tv_name, R.id.tv_details }, 0);

        ListView lstContacts = (ListView) findViewById(R.id.lst_contacts);
        lstContacts.setAdapter(mAdapter);

        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();
        listViewContactsLoader.execute();

        if(!res)
        {
            Toast.makeText(CustomerDeleteFavorite.this, "No Contact found in CustomerFavorite", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            lstContacts.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    mMatrixCursor.moveToFirst();

                    while ((position--) != 0)
                        mMatrixCursor.moveToNext();

                    String number = mMatrixCursor.getString(2);

                    Integer deletedRows = myDb.delFav(number);
                    if (deletedRows > 0)
                    {
                        Toast.makeText(CustomerDeleteFavorite.this, "Account Deleted Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else
                        Toast.makeText(CustomerDeleteFavorite.this, "Account not found", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor>
    {
        @Override
        protected Cursor doInBackground(Void... params)
        {
            long contactId = 1000;
            Cursor contactsCursor = myDb.getAllFav();
            if (contactsCursor.getCount() != 0)
            {
                res=true;
                while (contactsCursor.moveToNext())
                {
                    contactId++;
                    String displayName = contactsCursor.getString(0);
                    String details = contactsCursor.getString(1);
                    mMatrixCursor.addRow(new Object[]{Long.toString(contactId), displayName, details});
                }
            }
            else
            {
                res=false;
                finish();
            }
            return mMatrixCursor;
        }
        @Override
        protected void onPostExecute(Cursor result)
        {
            mAdapter.swapCursor(result);
        }
    }
}