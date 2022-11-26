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

import androidx.recyclerview.widget.RecyclerView;

import com.example.exceptionapp.R;
import com.example.exceptionapp.Room.Contact;
import com.example.exceptionapp.Room.DatabaseClient;
import com.example.exceptionapp.interfaces.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExceptionListAdapter extends RecyclerView.Adapter<ExceptionListAdapter.MyContactListViewHolder> {

    List<Contact> mainInfo;
    private ArrayList<Contact> arraylist;
    Context context;


    public ExceptionListAdapter(Context context, List<Contact> mainInfo) {
        this.mainInfo = mainInfo;
        this.context = context;
        this.arraylist = new ArrayList<Contact>();
        this.arraylist.addAll(mainInfo);
    }

    public class MyContactListViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewUserImage;
        TextView textViewShowName;
        TextView textViewPhoneNumber;
        ImageView imageViewDelete;

        public MyContactListViewHolder(View itemView) {
            super(itemView);

            textViewShowName = (TextView) itemView.findViewById(R.id.name);

            textViewPhoneNumber = (TextView) itemView.findViewById(R.id.no);
            imageViewUserImage = (ImageView) itemView.findViewById(R.id.pic);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.imageView_delete);


        }
    }

    @Override
    public MyContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_item_view, parent, false);
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


        if(contact.getChecked()) {

            holder.textViewShowName.setText(contact.getName());
            holder.textViewPhoneNumber.setText(contact.getPhone());
        }




        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        if (context instanceof RefreshRecyclerView) {
                            ((RefreshRecyclerView) context).refreshRecyclerView();
                        }


                    }
                }

                UpdateTask ut = new UpdateTask();
                ut.execute();
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
