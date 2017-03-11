package com.police170m3.rpi.jjhchatbot.jjhTmapView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.police170m3.rpi.jjhchatbot.Constants;
import com.police170m3.rpi.jjhchatbot.R;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tmap_Poi_Activity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{
    private TMapView tMapView;

    public final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private TextToSpeech mTTS;

    public static double wayDistance = 0;

    private Context mContext;

    private TMapGpsManager gps = null;
    private ProgressDialog dlg;

    ArrayList<String> mArrayMarkerID;
    private static int 	mMarkerID;

    private boolean m_bTrafficeMode = false;

    private LocationManager locationManager;
    private LocationListener locationListener;

    double lat,lon;



    private Handler mHandler;
    private Runnable mRunnable;

    public static TMapPoint point1;

    public static final String EXTRA_QUESTION = "com.police170m3.rpi.jjhchatbot.jjhTmapView.Tmap_Poi_Activity.EXTRA_QUESTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmapview);

         mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    mTTS.setLanguage(Locale.KOREAN);
                    // ***choose your own locale
                    // here***
                }
            }
        });

        mContext = this;

        mArrayMarkerID	= new ArrayList<String>();
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

        //setlocationthread(); //쓰레드를 통해 현재 위치 탐색을 하는 함수 실행

        relativeLayout.addView(tMapView);
        setContentView(relativeLayout);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        m_bTrafficeMode = false;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("test", "onLocationChanged, location:" + location);
                lat = location.getLatitude();
                lon = location.getLongitude();

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
        */
    }

    @Override
    public void onLocationChange(Location location){
        TMapPoint tpoint = gps.getLocation();
        tMapView.setCenterPoint(tpoint.getLongitude(),tpoint.getLatitude());
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());

        lat = location.getLatitude();
        lon = location.getLongitude();
        TMapMarkerItem tItem = new TMapMarkerItem();

        Log.e("onLocationChange","lat: "+lat+"lon: "+lon);
        point1 = gps.getLocation();
        getAroundBizPoi(point1);
        Log.e("onLocationChange","lat: "+point1);
        tItem.setTMapPoint(tpoint);
        gps.CloseGps();
        getAddressInfo(lat,lon);
        //프로그래스다이얼로그 추가시켜서 로딩 표시하기
    }

    /**
     * setTrafficeInfo
     * 실시간 교통정보를 표출여부를 설정한다.
     */
    public void setTrafficeInfo() {
        m_bTrafficeMode = !m_bTrafficeMode;
        tMapView.setTrafficInfo(m_bTrafficeMode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION_BRAIN_STATUS);
        intentFilter.addAction(Constants.BROADCAST_ACTION_LOGGER);
    }

    /**
     * getAroundBizPoi
     * 업종별 주변검색 POI 데이터를 요청한다.
     */
        public void getAroundBizPoi(final TMapPoint point1) {
            TMapData tmapdata = new TMapData();

            String getMyPoi = getIntent().getStringExtra(EXTRA_QUESTION);
            String getMyPoi1 = "편의점";

            Log.e("Tmap_Poi_Activity", "getMyPoi: " + getMyPoi);

            Log.e("getAroundBizPoi()","point1:" + point1);
            tmapdata.findAroundNamePOI(point1, getMyPoi, 1, 99, new TMapData.FindAroundNamePOIListenerCallback() {
                @Override
                public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                    try{
                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem item = poiItem.get(i);
                            final TMapMarkerItem tItem = new TMapMarkerItem();
                            double wayDistance = item.getDistance(point1);
                            int wayDistanceInt = (int)wayDistance;
                            Bitmap bitmap = null;


                            bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.otherpoi_icon);

                            com.police170m3.rpi.jjhchatbot.jjhTmapView.LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                    + item.getPOIAddress().replace("null", ""));

                            String str = item.getPOIID().toString();
                            tItem.setTMapPoint(item.getPOIPoint());
                            tItem.setVisible(tItem.VISIBLE);
                            tMapView.addMarkerItem(str,tItem);

                            tItem.setCalloutTitle(item.getPOIName());
                            tItem.setCalloutSubTitle("거리: "+wayDistanceInt+"M");
                            tItem.setCanShowCallout(true);
                            tItem.setIcon(bitmap);

                        }

                    }catch(NullPointerException e){
                        Log.e("getAroundBizPoi","잘못된 Poi내용을 입력하셨습니다.");
                        //나중에 잘못된 정보를 입력할때 적을 수 있는 리스트를 보여주고 여기서 입력하거나 클릭시 이동
                        //또는 글자를 바꿈으로서 내용이 이해되게 하도록한다.

                        Intent mainIntent = new Intent(Tmap_Poi_Activity.this, Tmap_Poi_List.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();

                        String answer = "제가 찾을 수 없는 이름입니다 \n여기 리스트에서 골라주시겠어요?";

                        // Broadcasts the Intent to receivers in this app.
                        mTTS.speak(answer, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });

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
                            bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.mypoi1_icon);
                            LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                            tItem.setCalloutSubTitle("" + strAddress);
                            tItem.setTMapPoint(point);
                            tItem.setCalloutTitle("내 위치");
                            tItem.setVisible(tItem.VISIBLE);
                            tMapView.addMarkerItem("위치",tItem);
                            tItem.setIcon(bitmap);
                            tItem.setCanShowCallout(true);

                        }
                    });
                }
                Log.d("getAddressInfo", " Address: " + labelAddressResult);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
