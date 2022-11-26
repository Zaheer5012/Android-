package com.example.exceptionapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.exceptionapp.R;
import com.example.exceptionapp.Room.Contact;
import com.example.exceptionapp.Room.DatabaseClient;
import com.example.exceptionapp.adapters.ExceptionListAdapter;
import com.example.exceptionapp.adapters.SelectUserAdapter;
import com.example.exceptionapp.databinding.ActivityHomeBinding;
import com.example.exceptionapp.interfaces.RefreshRecyclerView;
import com.example.exceptionapp.permission.RuntimePermissionRequester;
import com.example.exceptionapp.preference.SharedPref;
import com.example.exceptionapp.receiver.SmsDeliveredReceiver;
import com.example.exceptionapp.receiver.SmsSentReceiver;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener , RefreshRecyclerView {
    List<Contact> taskList;
    private ActivityHomeBinding binding;
    AlertDialog.Builder dialogBuilder;
    ExceptionListAdapter contactsAdapter;
    SharedPref pref;
    private RuntimePermissionRequester runtimePermissionRequester;

    private final String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        runtimePermissionRequester = new RuntimePermissionRequester(this);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ExceptionListActivity.class));
            }
        });

        pref= new SharedPref(this);

        binding.appBarHome.contentHome.imageViewDrawer.setOnClickListener(this);
        binding.appBarHome.contentHome.imageViewMessage.setOnClickListener(this);
        getAllData();

        AudioManager audioManager = (AudioManager)HomeActivity.this.getSystemService(Context.AUDIO_SERVICE);
     //   audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

   //     AudioManager audioManager = (AudioManager)HomeActivity.this.getSystemService(Context.AUDIO_SERVICE);


        if(pref.getMessageOptionStatus()){
            binding.appBarHome.contentHome.imageViewMessage.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);

        }else{
            binding.appBarHome.contentHome.imageViewMessage.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        if (!runtimePermissionRequester.checkSelfPermissions(permissions)) {
            runtimePermissionRequester.requestPermissions();
        }





        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }else{
            switch( audioManager.getRingerMode() ){
                case AudioManager.RINGER_MODE_NORMAL:
                    pref.setMuteStatus(false);
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    pref.setMuteStatus(true);
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    break;
            }

        }
     //   sendSMS("+923218315927","Hello How are you ",this);
    }



    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.imageView_drawer){
            binding.drawerLayout.open();
        } else if(id==R.id.imageView_message){


            dialogBuilder = new AlertDialog.Builder(this,R.style.MyAlertDialogTheme);
// ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_dialog, null);
            dialogBuilder.setView(dialogView);




            Button BtnEnable =  dialogView.findViewById(R.id.button_enable);
            Button BtnDisable =  dialogView.findViewById(R.id.button_disable);
            EditText editText_message =  dialogView.findViewById(R.id.editText_message);
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            editText_message.setText(pref.getMessage());

            editText_message.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {

                    pref.setMessage(s.toString());

                }
            });


            BtnEnable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!editText_message.getText().toString().equals("")) {
                        Toast.makeText(HomeActivity.this, "Enable", Toast.LENGTH_SHORT).show();
                        binding.appBarHome.contentHome.imageViewMessage.setColorFilter(ContextCompat.getColor(HomeActivity.this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
                        pref.setMessageOptionStatus(true);
                    }else{
                        Toast.makeText(HomeActivity.this, "Please Enter Message", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            BtnDisable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeActivity.this, "Disable", Toast.LENGTH_SHORT).show();

                    pref.setMessageOptionStatus(false);
                    binding.appBarHome.contentHome.imageViewMessage.setColorFilter(ContextCompat.getColor(HomeActivity.this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
            });


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllData();
        if(pref.getMessageOptionStatus()){
            binding.appBarHome.contentHome.imageViewMessage.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);

        }else{
            binding.appBarHome.contentHome.imageViewMessage.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.close();
        } else{
            super.onBackPressed();
        }
    }

    private void getAllData() {
        class GetTasks extends AsyncTask<Void, Void, List<Contact>> {

            @Override
            protected List<Contact> doInBackground(Void... voids) {
                taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getCheckedUsers();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<Contact> tasks) {
                super.onPostExecute(tasks);

                    contactsAdapter = new ExceptionListAdapter(HomeActivity.this, taskList);
                    binding.appBarHome.contentHome.recyclerViewExceptionList.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    binding.appBarHome.contentHome.recyclerViewExceptionList.setAdapter(contactsAdapter);

                if(taskList.size()>0) {
                    binding.appBarHome.contentHome.textViewNoContact.setVisibility(View.GONE);

                } else{
                    binding.appBarHome.contentHome.textViewNoContact.setVisibility(View.VISIBLE);
                }

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();

        //   taskList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().taskDao().getAll();
    }

    @Override
    public void refreshRecyclerView() {
        getAllData();
    }

//    private void sendSMS(String phoneNumber, String message,Context context) {
//        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
//        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
//        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
//                new Intent(context, SmsSentReceiver.class), 0);
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
//                new Intent(context, SmsDeliveredReceiver.class), 0);
//        try {
//            SmsManager sms = SmsManager.getDefault();
//            ArrayList<String> mSMSMessage = sms.divideMessage(message);
//            for (int i = 0; i < mSMSMessage.size(); i++) {
//                sentPendingIntents.add(i, sentPI);
//                deliveredPendingIntents.add(i, deliveredPI);
//            }
//            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
//                    sentPendingIntents, deliveredPendingIntents);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            Toast.makeText(context, "Sms Sending Failed",Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, e.getMessage().toString(),Toast.LENGTH_SHORT).show();
//
//        }
//
//    }
}