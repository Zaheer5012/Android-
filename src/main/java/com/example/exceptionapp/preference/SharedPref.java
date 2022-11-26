package com.example.exceptionapp.preference;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.exceptionapp.constants.Constants;

public class SharedPref {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    public SharedPref(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(Constants.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDbState(Boolean value){
        editor.putBoolean(Constants.PREF_CONTACT_DB_STATE, value);
        editor.commit();
    }

    public Boolean getDbState(){
        return pref.getBoolean(Constants.PREF_CONTACT_DB_STATE, false);
    }

    public void setMessage(String message){
        editor.putString(Constants.PREF_MESSAGE, message);
        editor.commit();
    }

    public String getMessage(){
        return pref.getString(Constants.PREF_MESSAGE, "");
    }

    public Boolean getMessageOptionStatus(){
        return pref.getBoolean(Constants.PREF_MESSAGE_OPTION, false);
    }

    public void setMessageOptionStatus(Boolean value){
        editor.putBoolean(Constants.PREF_MESSAGE_OPTION, value);
        editor.commit();
    }

    public Boolean getMuteStatus(){
        return pref.getBoolean(Constants.PREF_MUTE_STATUS, false);
    }

    public void setMuteStatus(Boolean value){
        editor.putBoolean(Constants.PREF_MUTE_STATUS, value);
        editor.commit();
    }
}
