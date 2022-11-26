package com.example.exceptionapp.Room;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM Contact ORDER BY name")
    List<Contact> getAll();

    @Query("SELECT * FROM Contact Where checked=1")
    List<Contact> getCheckedUsers();


    @Query("SELECT * FROM Contact Where checked=0")
    List<Contact> getUnCheckedUsers();


    @Query("SELECT * FROM Contact Where name=(:name)")
    List<Contact> checkContactAlreadyExist(String name);

    @Insert
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Update
    void update(Contact contact);

}