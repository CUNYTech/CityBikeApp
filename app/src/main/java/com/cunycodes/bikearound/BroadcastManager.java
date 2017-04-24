package com.cunycodes.bikearound;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Francely on 4/23/17.
 */

    public class BroadcastManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            int multipleDates = 0;
            String place = "";

            Calendar now = GregorianCalendar.getInstance();
            int day = now.get(Calendar.DAY_OF_MONTH);
            int month = now.get(Calendar.MONTH);
            int year = now.get(Calendar.YEAR);

            String current_date = day + "/" + (month + 1) + "/" + year;
            String users_date = "0";

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if((!MapsActivity.allEvents.isEmpty()) && MapsActivity.allEvents != null) {
                for(int i = 0; i < MapsActivity.allEvents.size(); i++) {
                    if((simpleDateFormat.parse(current_date).getTime()) == simpleDateFormat.parse(MapsActivity.allEvents.get(i).getDate()).getTime()  - 86400000) {
//                        Intent intent1 = new Intent(context, MapsActivity.class);
//                        createNotification(context,intent1, "Trip reminder", "body", "Your trip to " + MapsActivity.allEvents.get(i).getPlace() + " is tomorrow");

                        place += MapsActivity.allEvents.get(i).getPlace();
                        multipleDates++;

                    }
                }
            }

            if(multipleDates == 1) {
                Intent intent1 = new Intent(context, MapsActivity.class);
                createNotification(context,intent1, "Trip reminder", "body", "Your trip to " + place +" is tomorrow");

            } else if(multipleDates > 1) {
                Intent intent1 = new Intent(context, MapsActivity.class);
                createNotification(context,intent1, "Trip reminder", "body", "Your scheduled trips are tomorrow");

            }

//            if((simpleDateFormat.parse(current_date).getTime() - 86400000) == simpleDateFormat.parse(users_date).getTime()) {
//                Intent intent1 = new Intent(context, MapsActivity.class);
//                createNotification(context,intent1, "Trip reminder", "body", "Your scheduled trip is tomorrow");
//
//            }

            multipleDates = 0;
            place = "";


        } catch (Exception e) {
            Log.i("date", "error == " + e.getMessage());
        }
    }

    public void createNotification(Context context, Intent intent,CharSequence title, CharSequence ticker, CharSequence desc) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setContentText(desc);
        builder.setSmallIcon(R.mipmap.new_icon_launcher);
        builder.setContentIntent(p);

        Notification n = builder.build();

        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.mipmap.new_icon_launcher, n);

        try {
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, som);
            ringtone.play();

        } catch (Exception e) {

        }
    }
}

