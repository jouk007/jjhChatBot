package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.Util;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaehun on 2017-02-02.
 */

public class Utils {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String METRIC_URL = "&units=metric";
    public static final String IMPERIAL_URL = "&units=imperial";
    public static final String ICON_URL = "http://openweathermap.org/img/w/";
    public static final String APP_ID = "&appid=2c7a43fdf1ddab77f670a56c6c482c8b";
    public static final String API_LINK = "http://api.openweathermap.org/data/2.5/weather";
    public static String API_KEY = "2c7a43fdf1ddab77f670a56c6c482c8b";

    @NonNull
    public static String apiRequest(String lat, String lon){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric",lat,lon,API_KEY));
        return sb.toString();
    }

    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException{
        JSONObject jObj = jsonObject.getJSONObject(tagName);
        return jObj;
    }

    public static String getString(String tagName, JSONObject jsonObject) throws JSONException{
        return jsonObject.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jsonObject) throws JSONException{
        return (float) jsonObject.getDouble(tagName);
    }

    public static double getDouble(String tagName, JSONObject jsonObject) throws JSONException{
        return(float) jsonObject.getDouble(tagName);
    }

    public static int getInt(String tagName, JSONObject jsonObject) throws JSONException{
        return jsonObject.getInt(tagName);
    }
}
