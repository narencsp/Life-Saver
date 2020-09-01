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

public class SaviourRegister extends Fragment {

    SaviourDatabaseHelper myDb;
    EditText e1, e2, e3, e6, e7, e8;
    RadioButton rb1, rb2;
    Button b1;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.saviour_register_layout, container, false);

        TextView head = (TextView) rootView.findViewById(R.id.text);
        head.setVisibility(View.GONE);

        myDb = new SaviourDatabaseHelper(getActivity());

        e1 = (EditText) rootView.findViewById(R.id.fname);
        e2 = (EditText) rootView.findViewById(R.id.lname);
        e3 = (EditText) rootView.findViewById(R.id.dob);
        e6 = (EditText) rootView.findViewById(R.id.mobno);
        e7 = (EditText) rootView.findViewById(R.id.adrno);
        e8 = (EditText) rootView.findViewById(R.id.licno);
        rb1 = (RadioButton) rootView.findViewById(R.id.male);
        rb2 = (RadioButton) rootView.findViewById(R.id.female);
        b1 = (Button) rootView.findViewById(R.id.sbmt);

        final Context context=getActivity();

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
                new DatePickerDialog(context, date,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                SharedPreferences.Editor editor = app_preferences.edit();

                if (e1.getText().toString().length() > 0 && e2.getText().toString().length() > 0 &&
                        e3.getText().toString().length() > 0 && e6.getText().toString().length() >= 10 &&
                        e7.getText().toString().length() > 0 && e8.getText().toString().length() > 0 && (rb1.isChecked() || rb2.isChecked()))
                {
                    if (rb1.isChecked())
                    {
                        boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                e3.getText().toString(), rb1.getText().toString(), e6.getText().toString(), e7.getText().toString(), e8.getText().toString());
                        if (inInserted) {
                            Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_LONG).show();

                            editor.putString("isFirstTime", "saviour");
                            editor.apply();

                            Intent intent = new Intent(getActivity(), SaviourMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);

                            getActivity().finish();
                        }
                        else
                            Toast.makeText(getActivity(), "Registration Not Successful", Toast.LENGTH_LONG).show();

                    }
                    else if (rb2.isChecked())
                    {
                        boolean inInserted = myDb.insertData(e1.getText().toString(), e2.getText().toString(),
                                e3.getText().toString(), rb2.getText().toString(), e6.getText().toString(), e7.getText().toString(), e8.getText().toString());
                        if (inInserted)
                        {
                            Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_LONG).show();

                            editor.putString("isFirstTime", "saviour");
                            editor.apply();

                            Intent intent = new Intent(getActivity(), SaviourMainActivity.class);
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