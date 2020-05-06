package com.amwallace.moviedirectory.Util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Prefs {
    //to hold last used search term
    SharedPreferences sharedPreferences;

    public Prefs(Activity activity) {
        this.sharedPreferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void setSearch(String search){
        //commit search term to memory
        sharedPreferences.edit().putString("search", search).commit();
    }

    public String getSearch(){
        //return saved search string, or "John Wick" as default
        return sharedPreferences.getString("search", "John+Wick");
    }
}
