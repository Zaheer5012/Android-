package com.example.exceptionapp.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.exceptionapp.Room.Contact;
import com.example.exceptionapp.Room.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    Cursor getPhoneNumber;
    ContentResolver resolver ;

    Context context;

    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public List<Contact> getData() {
        List<Contact> data = new ArrayList<>();

        getPhoneNumber = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (getPhoneNumber != null) {
            Log.e("count", "" + getPhoneNumber.getCount());
            if (getPhoneNumber.getCount() == 0) {
           //     Toast.makeText(context, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
            }

            while (getPhoneNumber.moveToNext()) {
                @SuppressLint("Range") String id = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                @SuppressLint("Range") String name = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //String EmailAddr = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                @SuppressLint("Range") String image_thumb = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

//                Contact selectUser = new Contact();
//             //   selectUser.setImagepath(image_thumb);
//                selectUser.setName(name);
//                selectUser.setPhone(phoneNumber);
//             //   selectUser.setCheckedBox(false);
//              //  selectUser.setEmail(id);
//                data.add(selectUser);


                class SaveTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {

                        //creating a task
                        Contact contact = new Contact();
                        contact.setName(name);
                        contact.setPhone(phoneNumber);
                        contact.setChecked(false);
                        data.add(contact);
                        //adding to database
                        List<Contact> contactList= DatabaseClient.getInstance(context).getAppDatabase().taskDao().checkContactAlreadyExist(name);

                        if(contactList.size()>0){


                        }
                        else {
                            DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                                    .taskDao()
                                    .insert(contact);
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
//                                Bundle bundle= new Bundle();
//                                bundle.putString("PriceAlerts","Crypto Alert PriceAlerts");
//                                firebaseAnalytics.logEvent("PriceAlerts",bundle);
//                                Toast.makeText(getApplicationContext(), "Alert Saved", Toast.LENGTH_LONG).show();
//                                finish();
                    }
                }

                SaveTask st = new SaveTask();
                st.execute();

            }
        } else {
            Log.e("Cursor close 1", "----");
        }

        getPhoneNumber.close();

        return data;
    }
}
