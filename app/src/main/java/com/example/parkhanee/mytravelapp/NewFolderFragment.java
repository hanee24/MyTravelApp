package com.example.parkhanee.mytravelapp;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;


public class NewFolderFragment extends Fragment {

    String TAG = "NewFolderFragment";
    public static EditText et_start, et_end;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_folder,container,false);
        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        et_start = (EditText) view.findViewById(R.id.date_start);
        et_end = (EditText) view.findViewById(R.id.date_end);

        ImageButton btn_start = (ImageButton)view.findViewById(R.id.pick_start_date);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        ImageButton btn_end = (ImageButton) view.findViewById(R.id.pick_end_date);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        Boolean start;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            start = getArguments().getBoolean("start_date");
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String str_month,str_day;
            str_month = String.valueOf(month+1);
            str_day = String.valueOf(day);
            if (month+1<10){
                str_month = "0"+str_month;
            }
            if (day<10){
               str_day = "0"+str_day;
            }
            String date = String.valueOf(year) + " - "+str_month+" - "+str_day;

            if (start){
                et_start.setText(date); 
            }else{
                et_end.setText(date);
            }
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        if (v.getId() == R.id.pick_start_date){
            args.putBoolean("start_date",true);
        }else {
            if (v.getId() != R.id.pick_end_date){
                Log.d(TAG, "showDatePickerDialog: check parameter View");
            }
            args.putBoolean("start_date",false);
        }

        newFragment.setArguments(args);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
