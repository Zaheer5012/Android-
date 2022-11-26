package com.example.exceptionapp.receiver;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exceptionapp.Room.Contact;
import com.example.exceptionapp.Room.DatabaseClient;
import com.example.exceptionapp.activities.HomeActivity;
import com.example.exceptionapp.adapters.ExceptionListAdapter;
import com.example.exceptionapp.preference.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class IncomingCallBroadcastReceiver extends BroadcastReceiver {
    private static WindowManager windowManager;
    @SuppressLint("StaticFieldLeak")
    private static ViewGroup windowLayout;
    List<Contact> taskListChecked;
    List<Contact> taskListUnChecked;
    private static final float WINDOW_WIDTH_RATIO = 0.8f;
    private WindowManager.LayoutParams params;
    private float x;
    private float y;
    String storedPhone;
    SharedPref pref;
    Boolean check;
    AudioManager audioManager;
    @Override
    public void onReceive(Context context, Intent intent) {


        //   audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        //     AudioManager audioManager = (AudioManager)HomeActivity.this.getSystemService(Context.AUDIO_SERVICE);



        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (number != null) {
                if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                        .equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    showWindow(context, number);
                } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                        .equals(TelephonyManager.EXTRA_STATE_IDLE) ||
                        intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                        .equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        pref = new SharedPref(context);
                        if(pref.getMuteStatus()) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                           // sendSMS("","",context);
                        }
                        pref.setMuteStatus(false);
                }
            }
        }
    }

    private void showWindow(final Context context, String phone) {
//        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        windowLayout = (ViewGroup) View.inflate(context, R.layout.window_call_info, null);
//        getLayoutParams();
//        setOnTouchListener();
//
//        TextView numberTextView = windowLayout.findViewById(R.id.number);
//        numberTextView.setText(phone);
//        Button cancelButton = windowLayout.findViewById(R.id.cancel);
//        cancelButton.setOnClickListener(view -> closeWindow());
//
//        windowManager.addView(windowLayout, params);
        Toast.makeText(context, "Incoming Call Testing", Toast.LENGTH_SHORT).show();


        getAllData(context,phone);
        getUnSelectedContactList(context,phone);







    }

    private void getLayoutParams() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getWindowsTypeParameter(),
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        params.format = 1;
        params.width = getWindowWidth();
    }

    private int getWindowsTypeParameter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        return WindowManager.LayoutParams.TYPE_PHONE;
    }

    private int getWindowWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return (int) (WINDOW_WIDTH_RATIO * (double) metrics.widthPixels);
    }

    private void setOnTouchListener() {
        windowLayout.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateWindowLayoutParams(event);
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                default:
                    break;
            }
            return false;
        });
    }

    private void updateWindowLayoutParams(MotionEvent event) {
        params.x = params.x - (int) (x - event.getRawX());
        params.y = params.y - (int) (y - event.getRawY());
        windowManager.updateViewLayout(windowLayout, params);
        x = event.getRawX();
        y = event.getRawY();
    }

    private void closeWindow() {
        if (windowLayout != null) {
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }

    private void getAllData(Context context,String phone) {
        class GetTasks extends AsyncTask<Void, Void, List<Contact>> {

            @Override
            protected List<Contact> doInBackground(Void... voids) {
                taskListChecked = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .taskDao()
                        .getCheckedUsers();
                return taskListChecked;
            }

            @Override
            protected void onPostExecute(List<Contact> tasks) {
                super.onPostExecute(tasks);
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                pref=new SharedPref(context);
                for (Contact contact : taskListChecked) {

                  storedPhone=contact.getPhone().replaceAll("[()\\s-]+", "");

                    switch( audioManager.getRingerMode() ){
                        case AudioManager.RINGER_MODE_SILENT:

                         Boolean matchPhoneNumber=  PhoneNumberUtils.compare(phone,storedPhone);

                            if (matchPhoneNumber) {
                                pref.setMuteStatus(true);

                                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                             //   audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume/5, AudioManager.FLAG_PLAY_SOUND);


                                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                Ringtone r = RingtoneManager.getRingtone(context, uri);
                                r.play();

                                //   Toast.makeText(context, "Phone Matched In list"+phone+" "+storedPhone.toString(), Toast.LENGTH_SHORT).show();
                            }

                        case AudioManager.RINGER_MODE_VIBRATE:
                            break;
                    }
                }

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();

        //   taskList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().taskDao().getAll();
    }

    private void getUnSelectedContactList(Context context,String phone) {
        class GetTasks extends AsyncTask<Void, Void, List<Contact>> {

            @Override
            protected List<Contact> doInBackground(Void... voids) {
                taskListUnChecked = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .taskDao()
                        .getUnCheckedUsers();
                return taskListUnChecked;
            }

            @Override
            protected void onPostExecute(List<Contact> tasks) {
                super.onPostExecute(tasks);
                check=false;
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                pref=new SharedPref(context);
                for (Contact contact : taskListUnChecked) {

                    storedPhone=contact.getPhone().replaceAll("[()\\s-]+", "");

                    switch( audioManager.getRingerMode() ){
                        case AudioManager.RINGER_MODE_SILENT:

                            Boolean matchPhoneNumber=  PhoneNumberUtils.compare(phone,storedPhone);

                            if (matchPhoneNumber) {
                                // pref.setMuteStatus(true);
                                //audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                if(!check){
                                    check=true;

                                Toast.makeText(context, "Phone Matched In list for sending sms", Toast.LENGTH_SHORT).show();
                               if(pref.getMessageOptionStatus()) {
                                   sendSMS(phone, pref.getMessage(), context);
                               }
                            }
                            }

                        case AudioManager.RINGER_MODE_VIBRATE:
                            break;
                    }
                }

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();

        //   taskList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().taskDao().getAll();
    }

    private void sendSMS(String phoneNumber, String message,Context context) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

        PendingIntent pendingIntent = null;
        PendingIntent sentPI;
        PendingIntent deliveredPI;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

             sentPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(context, SmsSentReceiver.class), PendingIntent.FLAG_MUTABLE);
             deliveredPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(context, SmsDeliveredReceiver.class), PendingIntent.FLAG_MUTABLE);


        }
        else
        {
             sentPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(context, SmsSentReceiver.class), PendingIntent.FLAG_ONE_SHOT);
             deliveredPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(context, SmsDeliveredReceiver.class), PendingIntent.FLAG_ONE_SHOT);
        }



        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
            }
            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                    sentPendingIntents, deliveredPendingIntents);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(context, "Sms Sending Failed",Toast.LENGTH_SHORT).show();
            Toast.makeText(context, e.getMessage().toString(),Toast.LENGTH_SHORT).show();

        }
    }
}
