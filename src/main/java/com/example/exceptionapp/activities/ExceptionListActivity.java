package com.example.exceptionapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.exceptionapp.R;
import com.example.exceptionapp.Room.Contact;
import com.example.exceptionapp.Room.DatabaseClient;
import com.example.exceptionapp.adapters.DatabaseAdapter;
import com.example.exceptionapp.adapters.SelectUser;
import com.example.exceptionapp.adapters.SelectUserAdapter;
import com.example.exceptionapp.databinding.ActivityExceptionListBinding;
import com.example.exceptionapp.databinding.ActivityHomeBinding;
import com.example.exceptionapp.preference.SharedPref;

import java.util.List;

public class ExceptionListActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityExceptionListBinding binding;
    DatabaseAdapter db;
    List<Contact> taskList;
    List<SelectUser> dbContactList;
    SelectUserAdapter contactsAdapter;
    SearchView search;
    SharedPref pref;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExceptionListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db  = new DatabaseAdapter(getApplicationContext());
        binding.imageViewBack.setOnClickListener(this);
        pref=new SharedPref(this);


        binding.imageViewSync.setOnClickListener(this);

        binding.searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textViewActionbarTitle.setVisibility(View.GONE);
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String stext) {
                contactsAdapter.filter(stext);
                return false;
            }
        });


        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                binding.textViewActionbarTitle.setVisibility(View.VISIBLE);
                return false;
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {

            if(pref.getDbState().equals("false")){
                setAllContactList();
                pref.setDbState(true);
            }else{

                getAllData();

            }
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
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<Contact> tasks) {
                super.onPostExecute(tasks);
//                TasksAdapter adapter = new TasksAdapter(MainActivity.this, tasks);
//                recyclerView.setAdapter(adapter);

                contactsAdapter = new SelectUserAdapter(ExceptionListActivity.this, taskList);
                binding.recyclerViewContactList.setLayoutManager(new LinearLayoutManager(ExceptionListActivity.this));
                binding.recyclerViewContactList.setAdapter(contactsAdapter);

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();

     //   taskList = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().taskDao().getAll();


    }



    private void setAllContactList() {
        new ContactLoader().execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id==R.id.imageView_back){
            super.onBackPressed();
        } else if(id==R.id.imageView_sync){
            setAllContactList();
          //  getAllData();
            Toast.makeText(this, "Contacts Updated", Toast.LENGTH_SHORT).show();

        }
    }

    public class ContactLoader extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            List<Contact> MHList = db.getData();
            return MHList;
        }

        @Override
        protected void onPostExecute(List<Contact> selectUsers) {
            getAllData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                setAllContactList();
            } else {
                Toast.makeText(this, "Please grant permission to display contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}