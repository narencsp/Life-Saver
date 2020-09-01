package com.example.sathish.SafeDrive_pro;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sathish on 13-11-2016.
 */

public class SaviourUpdateReg extends Activity
{

    SaviourDatabaseHelper myDb;
    EditText e1,e2,e3,e6,e7,e8;
    RadioButton rb1,rb2;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.saviour_register_layout);

        myDb = new SaviourDatabaseHelper(this);

        e1 = (EditText) findViewById(R.id.fname);
        e2 = (EditText) findViewById(R.id.lname);
        e3 = (EditText) findViewById(R.id.dob);
        e6 = (EditText) findViewById(R.id.mobno);
        e7 = (EditText) findViewById(R.id.adrno);
        e8 = (EditText) findViewById(R.id.licno);
        rb1 = (RadioButton) findViewById(R.id.male);
        rb2 = (RadioButton) findViewById(R.id.female);
        b1 = (Button) findViewById(R.id.sbmt);

        Cursor res = myDb.getAllData();

        if (res.getCount() != 0)
        {
            while (res.moveToNext())
            {
                e1.setText(res.getString(0));
                e2.setText(res.getString(1));
                e3.setText(res.getString(2));
                if (res.getString(3).contains("Female"))
                {
                    rb1.setChecked(false);
                    rb2.setChecked(true);
                }
                else
                {
                    rb1.setChecked(true);
                    rb2.setChecked(false);
                }
                e6.setText(res.getString(4));
                e7.setText(res.getString(5));
                e8.setText(res.getString(6));
            }
        }

        final Context context = this;

        e3.setOnClickListener(new View.OnClickListener()
        {
            Calendar myCalendar = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

                private void updateLabel()
                {
                    String myFormat = "MM/dd/yy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    e3.setText(sdf.format(myCalendar.getTime()));
                }

            };

            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void addData(View view)
    {
        b1.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (e1.getText().toString().length()>0 && e2.getText().toString().length()>0 &&
                                e3.getText().toString().length()>0 && e6.getText().toString().length()>=10 &&
                                e7.getText().toString().length()>0 && e8.getText().toString().length()>0 && (rb1.isChecked()||rb2.isChecked()))
                        {
                            if (rb1.isChecked())
                            {
                                boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                        e3.getText().toString(), rb1.getText().toString(), e6.getText().toString(), e7.getText().toString(), e8.getText().toString());
                                if (inInserted)
                                {
                                    Toast.makeText(SaviourUpdateReg.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else
                                    Toast.makeText(SaviourUpdateReg.this, "Registration Not Successful", Toast.LENGTH_LONG).show();

                            }
                            else if(rb2.isChecked())
                            {
                                boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                        e3.getText().toString(), rb2.getText().toString(), e6.getText().toString(), e7.getText().toString(), e8.getText().toString());
                                if (inInserted)
                                {
                                    Toast.makeText(SaviourUpdateReg.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else
                                    Toast.makeText(SaviourUpdateReg.this, "Registration Not Successful", Toast.LENGTH_LONG).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(SaviourUpdateReg.this, "Fill in all the data", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}