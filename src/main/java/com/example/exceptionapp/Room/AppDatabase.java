package com.example.exceptionapp.Room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDao taskDao();

}