package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("MissingPermission")
public class WeatherActivity extends AppCompatActivity {
    public static final String CLEAR_SKY = "clear sky";

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private TextView tv;
    private RelativeLayout myLayout;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationListener myLocation;

    int MY_PERMISSION = 0;

    double lat = 0;
    double lon = 0;

    public static final String EXTRA_QUESTION = "com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.WeatherActivity.EXTRA_QUESTION";

    Weather weather = new Weather();
    Weather weather1 = new Weather();

    private CurrentCondition currentCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityName = (TextView) findViewById(R.id.cityText);
        iconView = (ImageView) findViewById(R.id.weatherIcon);
        temp = (TextView) findViewById(R.id.tempText);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidityText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.sunriseText);
        sunset = (TextView) findViewById(R.id.sunsetText);
        updated = (TextView) findViewById(R.id.updateText);
        tv = (TextView) findViewById(R.id.textView2);
        myLayout = (RelativeLayout) findViewById(R.id.activity_main);

        CityPreference cityPreference = new CityPreference(WeatherActivity.this);

        /*
        String getMyCity = getIntent().getStringExtra(EXTRA_QUESTION);
        Log.e("WeatherActivity", "getMyCity: " + getMyCity);
        renderWeatherData(getMyCity);
        */

        // API 23 버전이상에서 새로 업데이트 된 권한 요구 함수
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        myLocation = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //tv.append("\n " + location.getLongitude() + " " + location.getLatitude());
                Log.d("test", "onLocationChanged, location:" + location);
                lat = location.getLatitude();
                lon = location.getLongitude();
                locationManager.removeUpdates(myLocation);
                getAddressInfo(lat, lon);
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
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        setMylocayion();
        configureButton();
    }

    private void configureButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            return;
        }
    }
    //--- OnCreate End ---//

    //처음부터 내 위치를 표시하기
    private void setMylocayion(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                myLocation);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                myLocation);
        locationManager.requestLocationUpdates("gps", 5000, 0, myLocation);
    }

    //APT 23버전 이상부터 권한 요구사항이 바뀌었으므로 개발시 참고
    /*@Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);
        }
        locationManager.removeUpdates(mLocationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, mLocationListener);
    }*/


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
            //temp.setText((int) (((weather.temperature.getTemp() * 9) / 5) + 32)+" °C");
            wind.setText("" + weather.wind.getSpeed() + " m/s");
            description.setText("" + weather.currentCondition.getDescription());
            humidity.setText("" + weather.currentCondition.getHumidity() + "%");
            pressure.setText("" + weather.currentCondition.getPressure() + " hPa");
            sunrise.setText("Sunrise: " + sunriseTime);
            sunset.setText("Sunset: " + sunsetTime);
            updated.setText("Last Updated: " + updatedTime);

            Log.d("onPostExecute", "Cloudiness: " + weather.currentCondition.getDescription());

            // Set iconView using the url code given in the XML file
            new DownloadImage().execute(Utils.ICON_URL + weather.currentCondition.getIcon() + ".png");

            //날씨 상태에 따라 배경이미지 변경
            if (weather.currentCondition.getIcon().equals("01d")) {
                myLayout.setBackgroundResource(R.drawable.dayclearsky);
                description.setText("맑음");
            } else if (weather.currentCondition.getIcon().equals("02d")) {
                //myLayout.setBackgroundResource(R.drawable.dayfewcloud);
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
            } else if (weather.currentCondition.getIcon().equals("50n")) {
                myLayout.setBackgroundResource(R.drawable.nighthaze);
                description.setText("구름 중간");
            }
        }
    }
        //--- WeatherTask End ---//

        //--- Change city Dialog Start ---//
        private void showChangeCityDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
            builder.setTitle("Change City");
            //openWeatherMap 도시 이름 입력에 따라 각 지역 날시 파싱-영어로만 가능
            final EditText cityInput = new EditText(WeatherActivity.this);
            cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
            cityInput.setHint("Enter city (Seattle) or zip code (80501)");
            builder.setView(cityInput);
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CityPreference cityPreference = new CityPreference(WeatherActivity.this);
                    cityPreference.setCity(cityInput.getText().toString());

                    // Remove Spaces in city string to avoid fatal error
                    String newCity = cityPreference.getCity().replace(" ", "");

                    //renderWeatherData(newCity);

                }
            });
            builder.show();
        }
        //--- Change city Dialog END ---//

        //--- Change city Dialog Start ---//
        private void showChangeUnitsDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
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


        // 사용자의 위치정보를 불러오는 함수
        public void getAddressInfo(double latitude, double longitude) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                if (addressList != null && addressList.size() > 0) {
                    String addressResult = addressList.get(0).toString();

                    String labelAddressResult = "";
                    for (int i = 0; i <= addressList.get(0).getMaxAddressLineIndex(); i++) {
                        if (i != addressList.get(0).getMaxAddressLineIndex())
                            labelAddressResult += addressList.get(0).getAddressLine(i) + ", ";
                        else
                            labelAddressResult += addressList.get(0).getAddressLine(i);

                    }
                    tv.setText(labelAddressResult);
                    Log.d("getAddressInfo", " Address: " + labelAddressResult);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }