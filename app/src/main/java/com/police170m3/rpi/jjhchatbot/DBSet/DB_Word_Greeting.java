package com.police170m3.rpi.jjhchatbot.DBSet;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jaehun on 2017-04-22.
 */

public class DB_Word_Greeting {

    //DB에서 알맞는 텍스트를 가져오기위해 에딧텍스트의 문자열을 정리하는 클래스
    String[] resourceHello = {"안녕","지니핑","잘 지냈니","반가워"};

    private final Context myContext;
    ArrayList<String> getMyWord = new ArrayList<String>();

    public DB_Word_Greeting(Context mContext){
        this.myContext = mContext;
    }

    public ArrayList<String> setWordGreeting(String s){
        Log.d("DB_Word_Greeting","getStr: "+s);
        for (int i=0; i<resourceHello.length; i++){
            String word = resourceHello[i];
            if(s.contains(word)){
                s = "안녕";
                getMyWord.add(s);
            }
        }
        if (s.contains("까지 가는길 알려줘")){
            s = "까지 가는길 알려줘";
            Log.d("DB_Word_Greeting","setWordGreeting: "+s);
            getMyWord.add(s);
        }
        else if (s.contains("오늘 날씨 어때")){
            s = "오늘 날씨 어때";
            getMyWord.add(s);
        }
        else if (s.contains("근처 찾아봐")){
            s = "근처 찾아봐";
            Log.d("DB_Word_Greeting","setWordGreeting: "+s);
            getMyWord.add(s);
        }
        else if (s.contains("검색")){
            s= "검색";
            getMyWord.add(s);
        }
        else if (s.contains("여긴 어디야")){
            s= "여긴 어디야";
            getMyWord.add(s);
        }
        else if (s.contains("오늘 날씨 어때")){
            s= "오늘 날씨 어때";
            getMyWord.add(s);
        }
        else if (s.contains("날씨 알려줘")){
            s= "날씨 알려줘";
            getMyWord.add(s);
        }
        else if (s.contains("배고파")){
            s="배고파";
            getMyWord.add(s);
        }
        else if (s.contains("그래")){
            s="그래";
            getMyWord.add(s);
        }else if (s.contains("아니")){
            s="아니";
            getMyWord.add(s);
        }
        else if (s.equals("야")){
            s="야";
            getMyWord.add(s);
        }
        else if (s.contains("머하고있니")){
            s="머해";
            getMyWord.add(s);
        }
        else if (s.contains("머해")){
            s="머해";
            getMyWord.add(s);
        }
        else if (s.contains("머하고있어")){
            s="머해";
            getMyWord.add(s);
        }
        else if (s.contains("머하니")){
            s="머해";
            getMyWord.add(s);
        }
        else if (s.contains("좋아해")){
            s="을 좋아해";
            getMyWord.add(s);
        }
        else if (s.contains("보고있어")){
            s="을 좋아해";
            getMyWord.add(s);
        }
        else if (s.contains("관심있어")){
            s="을 좋아해";
            getMyWord.add(s);
        }
        else if (s.contains("배터리 잔량")){
            s="배터리 잔량";
            getMyWord.add(s);
        }
        else if (s.contains("충전")){
            s="충전해야겠어";
            getMyWord.add(s);
        }
        else if (s.contains("전화 입력해")){
            s="전화 입력해";
            getMyWord.add(s);
        }else{
            s="default";
            getMyWord.add(s);
        }
        Log.d("DB_Word_Greeting","setWordGreeting: "+s);
        return getMyWord;
    }
}
