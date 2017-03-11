package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Jaehun on 2017-02-04.
 */

public class CityPreference {
    SharedPreferences preferences;

    public CityPreference(Activity activity){
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return preferences.getString("city", "Denver");
    }

    public void setCity(String city) {
        preferences.edit().putString("city", city).commit();
    }
}
