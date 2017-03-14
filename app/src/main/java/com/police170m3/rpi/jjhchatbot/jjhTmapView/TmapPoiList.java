package com.police170m3.rpi.jjhchatbot.jjhTmapView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.police170m3.rpi.jjhchatbot.R;

import java.util.Locale;

public class TmapPoiList extends AppCompatActivity {

    TextToSpeech mTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap__poi__list);

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
        ListView listview ;
        TmapPoiListAdapter adapter;

        // Adapter 생성
        adapter = new TmapPoiListAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.word_list);
        listview.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.restaurant_icon),
                "TV맛집", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.cafe_icon),
                "카페", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.restaurant_icon),
                "한식", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.restaurant_icon),
                "중식", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.restaurant_icon),
                "일식", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.restaurant_icon),
                "양식", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.restaurant_icon),
                "패밀리레스토랑", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.restaurant_icon),
                "전문음식점", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.fastfood_icon),
                "피자", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.fastfood_icon),
                "치킨", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.cafe_icon),
                "디저트", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.fastfood_icon),
                "패스트푸드", "음식점");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.movie_icon),
                "영화관", "문화시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.singer_icon),
                "노래방", "문화시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.pc_icon),
                "PC방", "여가시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.concert_icon),
                "공연장", "여가시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.culture_icon),
                "문화시설", "문화시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.golf_icon),
                "스크린골프장", "여가시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.phamacy_icon),
                "약국", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.hospital_icon),
                "내과", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.hospital_icon),
                "소아과", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.hospital_icon),
                "외과", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.hospital_icon),
                "치과", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.hospital_icon),
                "안과", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.stethoscope_icon),
                "의원", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.stethoscope_icon),
                "보건소", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.stethoscope_icon),
                "한의원", "의료시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bus_icon),
                "버스", "교통시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.subway_icon),
                "지하철", "교통시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.gasstation_icon),
                "주유소", "차량관련시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.gasstation_icon),
                "충전소", "차량관련시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.parking_icon),
                "주차장", "차량관련시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.garage_icon),
                "정비소", "차량관련시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.wifi_icon),
                "T와이파이존", "편의시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bathhouse_icon),
                "목욕탕", "편의시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.hotel_icon),
                "숙박", "편의시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.shopping_icon),
                "쇼핑", "편의시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.government_icon),
                "관공서", "관공서");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.landmark_icon),
                "주요시설물", "랜드마크");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bank_icon),
                "은행", "은행");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bank_icon),
                "ATM", "은행");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.shopping_icon),
                "편의점", "편의시설");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.hairsalon_icon),
                "이미용실", "편의시설");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                TmapPoiListItem item = (TmapPoiListItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle();
                String answer = "다음엔 "+titleStr+"근처 찾아봐로 말하시면됩니다 검색을 시작합니다";
                Log.d("Tmap_Poi_List","onItemClick:"+titleStr);
                mTTS.speak(answer, TextToSpeech.QUEUE_FLUSH, null);

                Intent brainIntent1 = new Intent(TmapPoiList.this, TmapPoiActivity.class);
                brainIntent1.putExtra(TmapPoiActivity.EXTRA_QUESTION, titleStr);
                startActivity(brainIntent1);
                finish();
                // TODO : use item data.
            }
        }) ;
    }
}
