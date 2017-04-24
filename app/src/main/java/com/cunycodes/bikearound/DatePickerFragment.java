package com.cunycodes.bikearound;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(getActivity(), this, year, month, day);
        picker.getDatePicker().setMinDate(calendar.getTime().getTime());

        return picker;
    }

    @Override
    public int getTheme() {
        return R.style.DialogAnimation;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        int position = ((PlanActivity) getActivity()).getPosition();

        if (position != -1){
            EventPlan plan = ((PlanActivity) getActivity()).adapter.getPlan(position);
            month++;
            String date = formatDate(year, month, dayOfMonth);
            plan.setDate(date);
            ((PlanActivity) getActivity()).updatePlanDate(plan);
            Log.d("DatePicker", "Date Updated");
        } else {
            month++;
            Log.d("DatePicker", "" + year + "" + month + "" + dayOfMonth);
            String date = formatDate(year, month, dayOfMonth);
            ((PlanActivity) getActivity()).setDate(date);


            TimePickerFragment fragment = new TimePickerFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            DialogFragment fragment1 = (DialogFragment) fm.findFragmentByTag("datePicker");
            fmt.remove(fragment1);
            fmt.addToBackStack(null);
            fmt.add(fragment, "timePicker");
            fmt.commit();

            fragment1.getDialog().dismiss();
            fragment1.getDialog().cancel();

            Toast.makeText(getActivity(), "Date changed", Toast.LENGTH_SHORT).show();
        }
    }

    public String formatDate(int y, int m, int d){
        StringBuilder date = new StringBuilder("");
        String nd = String.valueOf(d);
        String nm = String.valueOf(m);
        if (nd.length() == 1){
            nd = "0"+nd;
        }
        if (nm.length() == 1){
            nm = "0"+nm;
        }

        date.append(nd).append("/").append(nm).append("/").append(String.valueOf(y));

        return String.valueOf(date);
    }

}

