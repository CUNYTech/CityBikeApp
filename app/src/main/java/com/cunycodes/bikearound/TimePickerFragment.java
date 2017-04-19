package com.cunycodes.bikearound;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    @Override
    public int getTheme() {
        return R.style.DialogAnimation;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        int position = ((PlanActivity) getActivity()).getPosition();

        if (position != -1){
            EventPlan plan = ((PlanActivity) getActivity()).adapter.getPlan(position);
            String time = formatTime(String.valueOf(hourOfDay), String.valueOf(minute));
            plan.setTime(time);
            ((PlanActivity) getActivity()).updatePlanDate(plan);
            Log.d("TimePicker", "Time Updated");
        } else {

            Log.d("TimePicker", "" + hourOfDay + ":" + minute);
            String time = formatTime(String.valueOf(hourOfDay), String.valueOf(minute));
            ((PlanActivity) getActivity()).setTime(time);

            PlaceFragment fragment = new PlaceFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fmt = fm.beginTransaction();
            DialogFragment fragment1 = (DialogFragment) fm.findFragmentByTag("timePicker");
            fmt.remove(fragment1);
            fmt.addToBackStack(null);
            fmt.add(fragment, "placeFragment");
            fmt.commit();

            fragment1.getDialog().dismiss();
            fragment1.getDialog().cancel();

            Toast.makeText(getActivity(), "Time changed", Toast.LENGTH_SHORT).show();
        }
    }

    public String formatTime(String hour, String minute){
        String time = hour+":"+minute;
        String newTime="";
        try{
            final SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm");
            final Date date = dateFormat.parse(time);
            newTime = new SimpleDateFormat("h:mm aa").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }
}
