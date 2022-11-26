package com.example.exceptionapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.exceptionapp.R;
import com.example.exceptionapp.Room.Contact;
import com.example.exceptionapp.Room.DatabaseClient;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.MyContactListViewHolder> {

    List<Contact> mainInfo;
    private ArrayList<Contact> arraylist;
    Context context;


    public SelectUserAdapter(Context context, List<Contact> mainInfo) {
        this.mainInfo = mainInfo;
        this.context = context;
        this.arraylist = new ArrayList<Contact>();
        this.arraylist.addAll(mainInfo);
    }

    public class MyContactListViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewUserImage;
        TextView textViewShowName;
        TextView textViewPhoneNumber;
        CheckBox checkBoxSelectItem;

        public MyContactListViewHolder(View itemView) {
            super(itemView);

            textViewShowName = (TextView) itemView.findViewById(R.id.name);
            checkBoxSelectItem = (CheckBox) itemView.findViewById(R.id.check);
            textViewPhoneNumber = (TextView) itemView.findViewById(R.id.no);
            imageViewUserImage = (ImageView) itemView.findViewById(R.id.pic);


        }
    }

    @Override
    public MyContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_view, parent, false);
        MyContactListViewHolder holder = new MyContactListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyContactListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Contact contact = mainInfo.get(position);
//        String imagepath = mainInfo.get(position).getImagepath();
//        if (imagepath == null) {
//            Picasso.get().load(R.drawable.ic_user).into(holder.imageViewUserImage);
//        }else {
//            Picasso.get().load(imagepath).into(holder.imageViewUserImage);
//        }
        holder.textViewShowName.setText(contact.getName());
        holder.textViewPhoneNumber.setText(contact.getPhone());
        holder.checkBoxSelectItem.setChecked(contact.getChecked());

        Boolean check=contact.getChecked();
     //   Toast.makeText(context,String.valueOf(check), Toast.LENGTH_SHORT).show();


    holder.checkBoxSelectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox)view;
                if(!checkBox.isChecked()){

                        class UpdateTask extends AsyncTask<Void, Void, Void> {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                contact.setName(contact.getName());
                                contact.setPhone(contact.getPhone());
                                contact.setChecked(false);
                                DatabaseClient.getInstance(context).getAppDatabase()
                                        .taskDao()
                                        .update(contact);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                              //  Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show();

                            }
                        }

                        UpdateTask ut = new UpdateTask();
                        ut.execute();
                    }

                    else{

                        class UpdateTask extends AsyncTask<Void, Void, Void> {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                contact.setName(contact.getName());
                                contact.setPhone(contact.getPhone());
                                contact.setChecked(true);
                                DatabaseClient.getInstance(context).getAppDatabase()
                                        .taskDao()
                                        .update(contact);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                              //  Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show();

                            }
                        }

                        UpdateTask ut = new UpdateTask();
                        ut.execute();
                    }
    }

        });


    }

    @Override
    public int getItemCount() {
        return mainInfo.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mainInfo.clear();
        if (charText.length() == 0) {
            mainInfo.addAll(arraylist);
        } else {
            for (Contact wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mainInfo.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
