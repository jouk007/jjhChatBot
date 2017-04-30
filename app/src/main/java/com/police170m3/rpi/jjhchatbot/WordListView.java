package com.police170m3.rpi.jjhchatbot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

public class WordListView extends AppCompatActivity {

    RelativeLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_word_list_view);

        myLayout =(RelativeLayout)findViewById(R.id.activity_my_word_list_view);

        getMyView();
    }

    private void getMyView(){
        long id = getIntent().getLongExtra("myWordListActivity",0);
        switch ((int)id){
            case 0: myLayout.setBackgroundResource(R.drawable.greetings_yes);
                break;
            case 1: myLayout.setBackgroundResource(R.drawable.greetings_yes);
                break;
            case 2: myLayout.setBackgroundResource(R.drawable.question_meal_yes);
                break;
            case 3: myLayout.setBackgroundResource(R.drawable.situation_chat);
                break;
            case 4: myLayout.setBackgroundResource(R.drawable.battery);
                break;
            case 5: myLayout.setBackgroundResource(R.drawable.search);
                break;
            case 6: myLayout.setBackgroundResource(R.drawable.getphonenum);
                break;
            case 7: myLayout.setBackgroundResource(R.drawable.weather_mylocation1);
                break;
            case 8: myLayout.setBackgroundResource(R.drawable.weather_citylocation);
                break;
            case 9: myLayout.setBackgroundResource(R.drawable.tmap_getpoi);
                break;
            case 10: myLayout.setBackgroundResource(R.drawable.tmap_drawpath);
                break;
            case 11: myLayout.setBackgroundResource(R.drawable.tmap_mylocation);
                break;

            default:
                throw new RuntimeException("Not invalid case!");
        }
    }
}
