package com.example.sathish.SafeDrive_pro;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sathish on 23-11-2016.
 */

public class CustomerRegister extends Fragment
{

    CustomerDatabaseHelper myDb;
    EditText e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,em;
    RadioButton rb1,rb2;
    Button b1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.customer_register_layout, container, false);

        TextView head=(TextView)rootView.findViewById(R.id.text);
        head.setVisibility(View.GONE);

        myDb = new CustomerDatabaseHelper(getActivity());

        e1=(EditText)rootView.findViewById(R.id.fname);
        e2=(EditText)rootView.findViewById(R.id.lname);
        e3=(EditText)rootView.findViewById(R.id.dob);
        e4=(EditText)rootView.findViewById(R.id.blood);
        e5=(EditText)rootView.findViewById(R.id.abtu);
        e6=(EditText)rootView.findViewById(R.id.mobno);
        em=(EditText)rootView.findViewById(R.id.email);
        e7=(EditText)rootView.findViewById(R.id.adrno);
        e8=(EditText)rootView.findViewById(R.id.eno1);
        e9=(EditText)rootView.findViewById(R.id.eno2);
        e10=(EditText)rootView.findViewById(R.id.eno3);
        rb1=(RadioButton)rootView.findViewById(R.id.male);
        rb2=(RadioButton)rootView.findViewById(R.id.female);
        b1=(Button)rootView.findViewById(R.id.sbmt);

        final Context content=getActivity();

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
                new DatePickerDialog(content, date,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = app_preferences.edit();

                if (e1.getText().toString().length()>0 && e2.getText().toString().length()>0 && e3.getText().toString().length()>0 && e4.getText().toString().length()>0 && e5.getText().toString().length()>0 && e6.getText().toString().length()==10 && e7.getText().toString().length()>0 && e8.getText().toString().length()==10 && e9.getText().toString().length()==10 && e10.getText().toString().length()==10 && (rb1.isChecked()||rb2.isChecked()) )
                {
                    if (rb1.isChecked())
                    {
                        boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                e3.getText().toString(), e4.getText().toString(), rb1.getText().toString(), e5.getText().toString(), e6.getText().toString(),em.getText().toString(), e7.getText().toString(), e8.getText().toString(), e9.getText().toString(),e10.getText().toString());
                        if (inInserted)
                        {
                            Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_LONG).show();

                            editor.putString("isFirstTime","consumer");
                            editor.apply();

                            Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);

                            getActivity().finish();
                        }
                        else
                            Toast.makeText(getActivity(), "Registration Not Successful", Toast.LENGTH_LONG).show();
                    }
                    else if(rb2.isChecked())
                    {
                        boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                e3.getText().toString(), e4.getText().toString(), rb2.getText().toString(), e5.getText().toString(),em.getText().toString(), e6.getText().toString(), e7.getText().toString(), e8.getText().toString(), e9.getText().toString(),e10.getText().toString());
                        if (inInserted)
                        {
                            Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_LONG).show();

                            editor.putString("isFirstTime","consumer");
                            editor.apply();

                            Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);

                            getActivity().finish();
                        }
                        else
                            Toast.makeText(getActivity(), "Registration Not Successful", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Fill in all the data", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }
}