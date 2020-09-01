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

public class CustomerUpdateReg extends Activity
{

    CustomerDatabaseHelper myDb;
    EditText e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,em;
    RadioButton rb1,rb2;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customer_register_layout);

        myDb = new CustomerDatabaseHelper(this);

        e1=(EditText)findViewById(R.id.fname);
        e2=(EditText)findViewById(R.id.lname);
        e3=(EditText)findViewById(R.id.dob);
        e4=(EditText)findViewById(R.id.blood);
        e5=(EditText)findViewById(R.id.abtu);
        e6=(EditText)findViewById(R.id.mobno);
        em=(EditText)findViewById(R.id.email);
        e7=(EditText)findViewById(R.id.adrno);
        e8=(EditText)findViewById(R.id.eno1);
        e9=(EditText)findViewById(R.id.eno2);
        e10=(EditText)findViewById(R.id.eno3);
        rb1=(RadioButton)findViewById(R.id.male);
        rb2=(RadioButton)findViewById(R.id.female);
        b1=(Button)findViewById(R.id.sbmt);

        Cursor res = myDb.getAllData();

        if (res.getCount() != 0)
        {
            while (res.moveToNext())
            {
                e1.setText(res.getString(0));
                e2.setText(res.getString(1));
                e3.setText(res.getString(2));
                e4.setText(res.getString(3));
                if(res.getString(4).contains("Female"))
                {
                    rb1.setChecked(false);
                    rb2.setChecked(true);
                }
                else
                {
                    rb1.setChecked(true);
                    rb2.setChecked(false);
                }
                e5.setText(res.getString(5));
                e6.setText(res.getString(6));
                em.setText(res.getString(7));
                e7.setText(res.getString(8));
                e8.setText(res.getString(9));
                e9.setText(res.getString(10));
                e10.setText(res.getString(11));
            }
        }


        final Context context = this;

        e3.setOnClickListener(new View.OnClickListener() {
            Calendar myCalendar = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

                private void updateLabel() {

                    String myFormat = "MM/dd/yy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    e3.setText(sdf.format(myCalendar.getTime()));
                }

            };

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        e4.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

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
                                e3.getText().toString().length()>0 && e4.getText().toString().length()>0 &&
                                e5.getText().toString().length()>0 && e6.getText().toString().length()>0 &&
                                em.getText().toString().length()>0 && e7.getText().toString().length()>0 &&
                                e8.getText().toString().length()>0 && e9.getText().toString().length()>0 && (rb1.isChecked()||rb2.isChecked()))
                        {
                            if (rb1.isChecked())
                            {
                                boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                        e3.getText().toString(), e4.getText().toString(), rb1.getText().toString(),
                                        e5.getText().toString(), e6.getText().toString(), em.getText().toString(),
                                        e7.getText().toString(), e8.getText().toString(), e9.getText().toString(),e10.getText().toString());
                                if (inInserted)
                                {
                                    Toast.makeText(CustomerUpdateReg.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else
                                    Toast.makeText(CustomerUpdateReg.this, "Registration Not Successful", Toast.LENGTH_LONG).show();

                            }
                            else if(rb2.isChecked())
                            {
                                boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                        e3.getText().toString(), e4.getText().toString(), rb2.getText().toString(),
                                        e5.getText().toString(), e6.getText().toString(), em.getText().toString(),
                                        e7.getText().toString(), e8.getText().toString(), e9.getText().toString(),e10.getText().toString());
                                if (inInserted)
                                {
                                    Toast.makeText(CustomerUpdateReg.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else
                                    Toast.makeText(CustomerUpdateReg.this, "Registration Not Successful", Toast.LENGTH_LONG).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(CustomerUpdateReg.this, "Fill in all the data", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}