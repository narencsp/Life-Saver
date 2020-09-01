package com.example.sathish.SafeDrive_pro;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * Created by Sathish on 08-03-2017.
 */

public class GetContact extends Activity
{
    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;
    ListViewContactsLoader listViewContactsLoader;
    ListView lstContacts;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);

        mMatrixCursor = new MatrixCursor(new String[] { "_id", "name", "details" });

        mAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.contact_button, null, new String[] { "name", "details" }, new int[] { R.id.tv_name, R.id.tv_details }, 0);

        lstContacts = (ListView) findViewById(R.id.lst_contacts);
        lstContacts.setAdapter(mAdapter);

        listViewContactsLoader = new ListViewContactsLoader();
        listViewContactsLoader.execute();

        lstContacts.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mMatrixCursor.moveToFirst();
                while((position--)!=0)
                    mMatrixCursor.moveToNext();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", mMatrixCursor.getString(1));
                resultIntent.putExtra("number", mMatrixCursor.getString(2));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        ImageButton img=(ImageButton)findViewById(R.id.addcontact);

        img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                startActivity(intent);
            }
        });
    }

    /** An AsyncTask class to retrieve and load listview with contacts */
    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor>
    {
        @Override
        protected Cursor doInBackground(Void... params)
        {
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            Cursor contactsCursor = getContentResolver().query(contactsUri, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

            if (contactsCursor.moveToFirst())
            {
                do {
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    Cursor dataCursor = getContentResolver().query(dataUri, null, ContactsContract.Data.CONTACT_ID + "=" + contactId, null, null);

                    String displayName;
                    String homePhone = "";
                    String mobilePhone = "";
                    String workPhone = "";

                    if (dataCursor.moveToFirst())
                    {

                        displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                        do
                        {

                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE))
                            {
                                switch (dataCursor.getInt(dataCursor.getColumnIndex("data2")))
                                {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        homePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        workPhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                }
                            }
                        } while (dataCursor.moveToNext());

                        String details = "";

                        if (homePhone != null && !homePhone.equals(""))
                            details = homePhone + "\n";
                        if (mobilePhone != null && !mobilePhone.equals(""))
                            details += mobilePhone + "\n";
                        if (workPhone != null && !workPhone.equals(""))
                            details += workPhone + "\n";

                        if(!details.equals(""))
                            mMatrixCursor.addRow(new Object[]{Long.toString(contactId), displayName, details});
                    }
                } while (contactsCursor.moveToNext());
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
