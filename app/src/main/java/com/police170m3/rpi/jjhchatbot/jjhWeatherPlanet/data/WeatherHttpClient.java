package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data;

import android.util.Log;

import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.Util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jaehun on 2017-02-02.
 */

public class WeatherHttpClient {

    public String getWeatherData(String place){
        HttpURLConnection connection;
        HttpURLConnection conn = null;
        InputStream inputStream;
        try {
            // 가끔씩 디버깅할때마다 URL 주소가 제대로 불러오지 못하는 경우가 있으므로 오류 발생 할때마다 적절하게 변경 요구
            //URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + place + "&appid=2c7a43fdf1ddab77f670a56c6c482c8b");
            //conn = (HttpURLConnection) url.openConnection();
            connection = (HttpURLConnection) (new URL(Utils.BASE_URL + place + Utils.APP_ID)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.connect();
            Log.d("test", "stream: " + connection);

            //Read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getWeatherData1(String place){
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) (new URL(Utils.API_LINK + place )).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoInput(true);
            connection.connect();
            Log.d("test", "stream: " + connection);

            //Read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
