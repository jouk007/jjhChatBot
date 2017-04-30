package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.police170m3.rpi.jjhchatbot.R;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.Util.Utils;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data.CityPreference;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data.JSONWeatherParser;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.data.WeatherHttpClient;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model.CurrentCondition;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model.Weather;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherCitiesActivity extends AppCompatActivity {
    public static final String CLEAR_SKY = "clear sky";

    @BindView(R.id.cityText)TextView cityName;
    @BindView(R.id.weatherIcon)ImageView iconView;
    @BindView(R.id.tempText)TextView temp;
    @BindView(R.id.cloudText)TextView description;
    @BindView(R.id.humidityText)TextView humidity;
    @BindView(R.id.pressureText)TextView pressure;
    @BindView(R.id.windText)TextView wind;
    @BindView(R.id.sunriseText)TextView sunrise;
    @BindView(R.id.sunsetText)TextView sunset;
    @BindView(R.id.updateText)TextView updated;
    @BindView(R.id.textView2)TextView tv;
    @BindView(R.id.activity_main)RelativeLayout myLayout;

    public static final String EXTRA_QUESTION = "com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.WeatherActivity.EXTRA_QUESTION";

    Weather weather = new Weather();
    Weather weather1 = new Weather();

    private CurrentCondition currentCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ButterKnife.bind(this);

        String getMyCity = getIntent().getStringExtra(EXTRA_QUESTION);
        Log.e("WeatherActivity", "getMyCity: " + getMyCity);
        renderWeatherData(getMyCity);
    }
    //--- OnCreate End ---//

    public void renderWeatherData(String city) {
        WeatherCitiesActivity.WeatherTask weatherTask = new WeatherCitiesActivity.WeatherTask();
        weatherTask.execute(city +"&units=metric");
    }

    //--- DownloadImage START ---//
    // code found at -- https://stackoverflow.com/questions/8423987/download-image-for-imageview-on-android
    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image) {
            //x.setImageDrawable(image);
            //Sets the Icon as the image that's been downloaded
            iconView.setImageDrawable(image);
        }

        /**
         * Actually download the Image from the _url
         *
         * @param _url
         * @return
         */
        private Drawable downloadImage(String _url) {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();

            /*
             * THIS IS NOT NEEDED
             *
             * YOU TRY TO CREATE AN ACTUAL IMAGE HERE, BY WRITING
             * TO A NEW FILE
             * YOU ONLY NEED TO READ THE INPUTSTREAM
             * AND CONVERT THAT TO A BITMAP
            out = new BufferedOutputStream(new FileOutputStream("testImage.jpg"));
            int i;
             while ((i = in.read()) != -1) {
                 out.write(i);
             }
             out.close();
             in.close();
             */

                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }
            return null;
        }
    }
    //--- DownloadImage End ---//

    private class WeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... strings) {
            String stream = null;
            String data = ((new WeatherHttpClient()).getWeatherData(strings[0]));

            // Catch error if a city is entered with a space e.g. "Fort Carson" or "Denver, US" to avoid fatal error
            // The space should already be removed at showInputDialog but this error causes the app to need to be reinstalled to work again so double protection
            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (Exception NullWeatherData) {
                Log.e("Error Parsing: ", "City has space in text");
            }
            if (weather == null) {
                showChangeCityDialog();
            }
            if (weather1 == null) {
                showChangeCityDialog();
            }
            return weather;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            //Convert times into readable formats
            //get unix times and multiply by 1000 to get proper length since it converts down to milliseconds
            long unixSunrise = weather.place.getSunrise() * 1000;
            long unixSunset = weather.place.getSunset() * 1000;
            long unixUpdated = weather.place.getLastupdate() * 1000;
            //do the conversion
            java.util.Date sunriseDate = new java.util.Date(unixSunrise);
            java.util.Date sunsetDate = new java.util.Date(unixSunset);
            java.util.Date updatedDate = new java.util.Date(unixUpdated);
            //Strip away everything but the time in 24hr time (use hh in place of kk or 12hour clock)
            //need to change sunset and rise times to time zone for where the location is not my local timezone
            String sunriseTime = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", sunriseDate));
            String sunsetTime = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", sunsetDate));
            String updatedTime = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", updatedDate));

            // Set Text for all items
            cityName.setText(weather.place.getCity() + ", " + weather.place.getCountry());
            temp.setText(weather.temperature.getTemp() + " °C");
            wind.setText("" + weather.wind.getSpeed() + " m/s");
            description.setText("" + weather.currentCondition.getDescription());
            humidity.setText("" + weather.currentCondition.getHumidity() + "%");
            pressure.setText("" + weather.currentCondition.getPressure() + " hPa");
            sunrise.setText("Sunrise: " + sunriseTime);
            sunset.setText("Sunset: " + sunsetTime);
            updated.setText("Last Updated: " + updatedTime);

            Log.d("onPostExecute", "Cloudiness: " + weather.currentCondition.getDescription());

            if (weather.currentCondition.getDescription() == CLEAR_SKY) {
                Log.d("onPostExecute", "Cloudiness: True clear sky");
                //한글로 변경시켜 등록시키기
            }

            // Set iconView using the url code given in the XML file
            new WeatherCitiesActivity.DownloadImage().execute(Utils.ICON_URL + weather.currentCondition.getIcon() + ".png");
            Log.d("onPostExecute", "getIcon: " + weather.currentCondition.getIcon());

            //날씨 상태에 따라 배경이미지 변경
            if (weather.currentCondition.getIcon().equals("01d")) {
                myLayout.setBackgroundResource(R.drawable.dayclearsky);
                description.setText("맑음");
            } else if (weather.currentCondition.getIcon().equals("02d")) {
                // myLayout.setBackgroundResource(R.drawable.dayfewcloud);
                description.setText("구름 조금");
            } else if (weather.currentCondition.getIcon().equals("01n")) {
                myLayout.setBackgroundResource(R.drawable.spaceclearsky);
                description.setText("맑음");
            } else if (weather.currentCondition.getIcon().equals("02n")) {
                myLayout.setBackgroundResource(R.drawable.nightfewcloud);
                description.setText("구름 조금");
            } else if (weather.currentCondition.getIcon().equals("03n")) {
                myLayout.setBackgroundResource(R.drawable.nightcloud);
                description.setText("구름 중간");
            }else if (weather.currentCondition.getIcon().equals("50n")) {
                myLayout.setBackgroundResource(R.drawable.nighthaze);
                description.setText("얇은 안개");
            }else if (weather.currentCondition.getIcon().equals("10n")) {
                myLayout.setBackgroundResource(R.drawable.nightrain);
                description.setText("비 약간");
            }
        }
    }

    //--- Change city Dialog Start ---//
    private void showChangeCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherCitiesActivity.this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(WeatherCitiesActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Enter city (Seattle) or zip code (80501)");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(WeatherCitiesActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                // Remove Spaces in city string to avoid fatal error
                String newCity = cityPreference.getCity().replace(" ", "");

                renderWeatherData(newCity);

                //한글로 입력할때 영어로 변환시키기
            }
        });
        builder.show();
    }
    //--- Change city Dialog END ---//

    //--- Change city Dialog Start ---//
    private void showChangeUnitsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherCitiesActivity.this);
        builder.setTitle("Change Units");
    }
    //--- Change city Dialog END ---//


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.change_cityId) {
            showChangeCityDialog();
        }
        if (id == R.id.change_unitsId) {
            showChangeUnitsDialog();
        }
        return super.onOptionsItemSelected(item);
    }
}
