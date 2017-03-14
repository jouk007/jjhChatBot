package com.police170m3.rpi.jjhchatbot.jjhTmapView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.police170m3.rpi.jjhchatbot.R;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jaehun on 2017-03-03.
 */

public class TmapViewActivity extends Activity implements TMapGpsManager.onLocationChangedCallback {

    private TMapView tMapView;

    private Context mContext;

    private TMapGpsManager gps = null;
    private ProgressDialog dlg;

    ArrayList<String> mArrayMarkerID;
    private static int mMarkerID;

    private boolean m_bTrafficeMode = false;

    private LocationManager locationManager;
    private LocationListener locationListener;

    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmapview);

        mContext = this;

        mArrayMarkerID = new ArrayList<String>();
        mMarkerID = 0;

        RelativeLayout relativeLayout = new RelativeLayout(this);
        tMapView = new TMapView(this);

        tMapView.setSKPMapApiKey("a5c9afa4-c69a-37ab-8da5-d20a4a3176b5");

        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setCompassMode(true);
        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(16);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        gps = new TMapGpsManager(this);// 생성자 함수 정의
        gps.setMinTime(1000);//현재 위치를 찾을 최소 시간 (밀리초)
        gps.setMinDistance(5);//현재 위치를 갱신할 최소 거리
        gps.setProvider(gps.NETWORK_PROVIDER);//현재위치를 찾을 방법(네트워크)

        gps.OpenGps();//네트워크 위치 탐색 허용

        relativeLayout.addView(tMapView);
        setContentView(relativeLayout);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        m_bTrafficeMode = false;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("test", "onLocationChanged, location:" + location);
                lat = location.getLatitude();
                lon = location.getLongitude();

                getAddressInfo(lat, lon);
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

    }

    @Override
    public void onLocationChange(Location location) {
        TMapPoint tpoint = gps.getLocation();
        tMapView.setCenterPoint(tpoint.getLongitude(), tpoint.getLatitude());
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        lat = location.getLatitude();
        lon = location.getLongitude();
        TMapMarkerItem tItem = new TMapMarkerItem();
        Log.e("onLocationChange", "lat: " + lat + "lon: " + lon);
        tItem.setTMapPoint(tpoint);
        getAddressInfo(lat, lon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // 사용자의 위치정보를 불러오는 함수
    public void getAddressInfo(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        final TMapMarkerItem tItem = new TMapMarkerItem();
        final TMapPoint point = new TMapPoint(latitude, longitude);

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
                TMapData tmapdata = new TMapData();

                if (tMapView.isValidTMapPoint(point)) {
                    tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String strAddress) {
                            Bitmap bitmap = null;
                            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mypoi1_icon);
                            LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                            tItem.setCalloutSubTitle("" + strAddress);
                            tItem.setTMapPoint(point);
                            tItem.setCalloutTitle("내 위치");
                            tItem.setVisible(tItem.VISIBLE);
                            tMapView.addMarkerItem("위치", tItem);
                            tItem.setIcon(bitmap);
                            tItem.setCanShowCallout(true);

                        }
                    });
                }
                Log.d("getAddressInfo", " Address: " + labelAddressResult);
                Log.d("getAddressInfo", " Address: " + addressResult);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}