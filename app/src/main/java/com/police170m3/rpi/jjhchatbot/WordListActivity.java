package com.police170m3.rpi.jjhchatbot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class WordListActivity extends AppCompatActivity {
    String myWord[]={"안녕","~ 검색","~ 전화 입력해","배고파","오늘 날씨 어때","~ 날씨 알려줘","여긴 어디야","~ 근처 찾아봐","~까지 가는길 알려줘","잘지내니"
            ,"배터리 잔량","비가 오고 있어"
    };
    ArrayList<String> getMyImage = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_word_list);

        ListView listview ;
        WordListAdapter adapter;

        // Adapter 생성
        adapter = new WordListAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.word_list);
        listview.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.images_speaking),
                "안녕", "간단한 상호 작용");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.images_speaking),
                "잘 지내니", "인사 관련 대화");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.images_speaking),
                "배고파", "식사에 관련된 상호 작용");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.images_speaking),
                "비가 오고 있어", "날씨 관련 대화");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.images_battery),
                "배터리 잔량", "배터리 관련 대화");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.images_search),
                "~ 검색", "검색 관련");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.images_speaking),
                "~ 전화 입력해", "전화 번호를 입력합니다");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.weather_icon),
                "오늘 날씨 어때", "날씨 정보를 담은 화면을 보여줍니다");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.weather_icon),
                "~ 날씨 알려줘", "다른 도시의 날씨를 보여줍니다 (Ex: 서울)");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.landmark_icon),
                "~ 근처 찾아봐", "입력된 주변 Poi(Ex:편의점,은행)를 표시");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.landmark_icon),
                "~까지 가는길 알려줘", "목적지까지 길을 설정해줍니다");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.landmark_icon),
                "여긴 어디야", "자신의 위치를 표시해줍니다");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WordListActivity.this,WordListView.class);
                intent.putExtra("myWordListActivity",id);
                startActivity(intent);
            }
        });
    }
}
