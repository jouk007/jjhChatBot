package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data;

import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.Util.Utils;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model.Place;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jaehun on 2017-02-03.
 */

public class JSONWeatherParser {

    public static Weather getWeather(String data){
        Weather weather = new Weather();

        //create JsonObject from data 01:13:52
        try{
            JSONObject jsonObject = new JSONObject(data);

            Place place = new Place();

            JSONObject coorObj = Utils.getObject("coord", jsonObject);
            place.setLat(Utils.getFloat("lat", coorObj));
            place.setLng(Utils.getFloat("lon", coorObj));

            //Get the sys obj(from JSON API key)
            JSONObject sysObj = Utils.getObject("sys", jsonObject);
            place.setCountry(Utils.getString("country", sysObj));
            place.setLastupdate(Utils.getInt("dt", jsonObject));
            place.setSunrise(Utils.getInt("sunrise", sysObj));
            place.setSunset(Utils.getInt("sunset", sysObj));

            //Get other place info
            place.setCity(Utils.getString("name", jsonObject));
            place.setLastupdate(Utils.getInt("dt", jsonObject));

            //set weather.place info
            weather.place = place;

            //Get weather object
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(Utils.getInt("id", jsonWeather));
            weather.currentCondition.setDescription(Utils.getString("description", jsonWeather));
            weather.currentCondition.setCondition(Utils.getString("main", jsonWeather));
            weather.currentCondition.setIcon(Utils.getString("icon", jsonWeather));

            // Get Main object
            JSONObject mainObj = Utils.getObject("main", jsonObject);
            //weather.currentCondition.setCondition(Utils.getString("main", jsonWeather));
            weather.temperature.setTemp(Utils.getDouble("temp", mainObj));
            weather.temperature.setMaxTemp(Utils.getFloat("temp_max", mainObj));
            weather.temperature.setMinTemp(Utils.getFloat("temp_min", mainObj));
            weather.currentCondition.setHumidity(Utils.getFloat("humidity", mainObj));
            weather.currentCondition.setPressure(Utils.getFloat("pressure", mainObj));

            JSONObject windObj = Utils.getObject("wind", jsonObject);
            weather.wind.setSpeed(Utils.getFloat("speed", windObj));
            weather.wind.getDegree(Utils.getFloat("deg", windObj));

            JSONObject cloudObj = Utils.getObject("clouds", jsonObject);
            weather.clouds.setPrecipitation(Utils.getInt("all", cloudObj));

            return weather;

        }catch (JSONException e){
            e.printStackTrace();

        }
        return null;
    }
}
