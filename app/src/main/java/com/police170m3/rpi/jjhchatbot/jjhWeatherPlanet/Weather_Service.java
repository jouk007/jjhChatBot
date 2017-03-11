package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.police170m3.rpi.jjhchatbot.BrainService;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.Util.Utils;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data.JSONWeatherParser;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data.WeatherHttpClient;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model.Weather;

/**
 * Created by Jaehun on 2017-03-09.
 */

@SuppressWarnings("MissingPermission")
public class Weather_Service extends Service{

    private LocationManager locationManager;
    private LocationListener myLocation;

    double lat = 0;
    double lon = 0;

    public static final String EXTRA_WEATHER_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.EXTRA_WEATHER_QUESTION";

    Weather weather = new Weather();

    @Override
    public void onCreate() {
        super.onCreate();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocation = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //tv.append("\n " + location.getLongitude() + " " + location.getLatitude());
                Log.d("test", "onLocationChanged, location:" + location);
                lat = location.getLatitude();
                lon = location.getLongitude();
                locationManager.removeUpdates(myLocation);
                new WeatherTask1().execute(Utils.apiRequest(String.valueOf(lat), String.valueOf(lon)));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        setMylocayion();
    }

    //처음부터 내 위치를 표시하기
    private void setMylocayion(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                myLocation);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                myLocation);

        locationManager.requestLocationUpdates("gps", 5000, 0, myLocation);
        //new WeatherTask1().execute(Utils.apiRequest(String.valueOf(lat), String.valueOf(lon)));
    }

    //--- WeatherTask Start ---//
    private class WeatherTask1 extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... strings) {
            String data = ((new WeatherHttpClient()).getWeatherData1(strings[0]));

            // Catch error if a city is entered with a space e.g. "Fort Carson" or "Denver, US" to avoid fatal error
            // The space should already be removed at showInputDialog but this error causes the app to need to be reinstalled to work again so double protection
            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (Exception NullWeatherData) {
                Log.e("Error Parsing: ", "City has space in text");
            }
            return weather;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            float humidity = weather.currentCondition.getHumidity();
            Intent brainIntent = new Intent(Weather_Service.this, BrainService.class);
            brainIntent.setAction(BrainService.ACTION_WEATHER_QUESTION);
            brainIntent.putExtra(EXTRA_WEATHER_QUESTION, humidity);
            startService(brainIntent);
            Log.d("Weather_Service","humidity: "+humidity);
        }
    }
    //--- WeatherTask End ---//

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    class MyBinder extends Binder {
        Weather_Service getService() { // 서비스 객체를 리턴
            return Weather_Service.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
