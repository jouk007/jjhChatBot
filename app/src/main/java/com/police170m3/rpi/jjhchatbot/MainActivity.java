package com.police170m3.rpi.jjhchatbot;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.police170m3.rpi.jjhchatbot.jjhTmapView.TmapDrawpassActivity;
import com.police170m3.rpi.jjhchatbot.jjhTmapView.TmapPoiActivity;
import com.police170m3.rpi.jjhchatbot.jjhTmapView.TmapViewActivity;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.WeatherActivity;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.WeatherCitiesActivity;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.WeatherService;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("MissingPermission")
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    public final int VOICE_RECOGNITION_REQUEST_CODE = 1234;


    private ListView chatListView;
    private static ChatArrayAdapter adapter;
    private EditText chatEditText;

    private ResponseReceiver mMessageReceiver;
    private ResponseReceiverSub mMessageReceiverSub;
    private Handler mHandler;
    private Runnable mRunnable;
    private TextToSpeech mTTS;

    String question;
    String getPhonenum;

    public static String getPhoneName;

    //날씨 파싱용 문자열
    String[] citiesKr = {"서울","대구","대전","부산","광주","의정부","마산","청주","공주"
            ,"전주","고성","속초","강릉","의성","울진","울산","구미","순천","목포","제주","인천"};

    String[] citiesUs = {"Seoul","Daegu","Daejeon","Busan","Gwangju"
            ,"Vijongbu","Masan","Cheongju","Kongju","Jeonju","Kosong","Sogcho"
            , "Kang-neung","Eisen","Ulchin","Ulsan","Kumi","Sunchun","Moppo","Jeju","Incheon"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRepeat();
        mTTS = new TextToSpeech(this,this);

        if (savedInstanceState == null) {
            Log.d("MainActivity", "onCreate savedInstanceState null");
            adapter = new ChatArrayAdapter(getApplicationContext());
            //adapter ChatArrayAdapter 호출
            //BrainLoggerDialog 호출
        }

        chatListView = (ListView) findViewById(R.id.chat_listView);
        chatListView.setAdapter(adapter);

        chatEditText = (EditText) findViewById(R.id.chat_editText);
        chatEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    question = chatEditText.getText().toString();
                    adapter.add(new ChatMessage(false, question));
                    chatEditText.setText("");

                    mHandler = new Handler();

                    if (question.contains("검색")){
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                String grm = question.substring(0,question.indexOf("검색"));
                                Log.e("test", "grm: "+ grm);
                                Intent intent= new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, grm);
                                startActivity(intent);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 1500);
                    }else if(question.contains("배고파")){
                        Log.e("test", "배고파:");
                        if (question.contains("배 고프시다면 식당으로 가실래요?")){
                            Log.e("test", "그래!");
                        }
                    }else if (question.contains("오늘 날씨 어때")){
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                                startActivity(intent);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 1500);
                    }else if (question.contains("날씨 알려줘")) {
                        //Openweathermap은 한글을 지원하지않기에 한글을 입력하면 영어 문자로 변환 한뒤 파싱 해온다
                        for (int i = 0 ; i <= citiesKr.length - 1; i++){
                            if (question.contains(citiesKr[i])){
                                String grm = question.replace(citiesKr[i],citiesUs[i]);
                                Log.d("question","citiesKr:" +grm);
                                final String grm1 = grm.substring(0,grm.length()-7);
                                Log.d("question","citiesKr:" +grm1.length());

                                mRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("question","citiesKr:" +grm1.length());
                                        Intent brainIntent1 = new Intent(MainActivity.this, WeatherCitiesActivity.class);
                                        brainIntent1.putExtra(WeatherCitiesActivity.EXTRA_QUESTION, grm1);
                                        startActivity(brainIntent1);
                                    }
                                };
                                mHandler = new Handler();
                                mHandler.postDelayed(mRunnable, 1000);

                            }
                        }
                    }else if (question.contains("여긴 어디야")){
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, TmapViewActivity.class);
                                startActivity(intent);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 1500);
                    }else if (question.contains("근처 찾아봐")){
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                String grm = question.substring(0,question.length()-7);
                                Log.d("question","grm1: "+grm.length());
                                Intent brainIntent1 = new Intent(MainActivity.this, TmapPoiActivity.class);
                                brainIntent1.putExtra(TmapPoiActivity.EXTRA_QUESTION, grm);
                                startActivity(brainIntent1);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 1000);
                   }else if (question.contains("까지 가는길 알려줘")){
                        mRunnable = new Runnable() {
                            @Override
                            public void run() {
                                String grm1 = question.substring(0,question.indexOf("까"));
                                Intent brainIntent1 = new Intent(MainActivity.this, TmapDrawpassActivity.class);
                                brainIntent1.putExtra(TmapDrawpassActivity.EXTRA_QUESTION, grm1);
                                startActivity(brainIntent1);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 1000);
                    }else if (question.contains("비가 오고 있어")){
                        Intent weatherIntent = new Intent(MainActivity.this, WeatherService.class);
                        startService(weatherIntent);
                    }

                    Intent brainIntent = new Intent(MainActivity.this, BrainService.class);
                    brainIntent.setAction(BrainService.ACTION_QUESTION);
                    brainIntent.putExtra(BrainService.EXTRA_QUESTION, question);
                    startService(brainIntent);
                    return true;
                }

                return false;
            }
        });


        // 키보드 셋 가림
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages.-리시브 메세지를 받기위한 mMessageReceiver 등록자
        IntentFilter intentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION_BRAIN_STATUS);
        intentFilter.addAction(Constants.BROADCAST_ACTION_BRAIN_ANSWER);
        intentFilter.addAction(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB);
        intentFilter.addAction(Constants.BROADCAST_ACTION_WEATHER_ANSWER);
        intentFilter.addAction(Constants.BROADCAST_ACTION_LOGGER);

        mMessageReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, intentFilter);
        mMessageReceiverSub = new ResponseReceiverSub();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiverSub, intentFilter);

    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverSub);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.jjh_questions) {
            Intent intent = new Intent(MainActivity.this, WordListActivity.class);
            startActivity(intent);
        }else if (id == R.id.jjh_explain) {
            Intent intent = new Intent(MainActivity.this, ChabotExplainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            //답변 호출
            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_ANSWER)) {
                String answer = intent.getStringExtra(Constants.EXTRA_BRAIN_ANSWER);

                Log.e("BrainService", "onStartCommand() onReceive:" + answer);
                adapter.add(new ChatMessage(true, answer));
                adapter.notifyDataSetChanged();
                mTTS.speak(answer, TextToSpeech.QUEUE_FLUSH, null);

                if (answer.contains("요리해서 드실려면 제가 추천해드릴 목록이 있어요")) {
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String grm = "추천요리";
                            Intent intent1 = new Intent(Intent.ACTION_WEB_SEARCH);
                            intent1.putExtra(SearchManager.QUERY, grm);
                            startActivity(intent1);
                        }
                    };
                    mHandler = new Handler();
                    mHandler.postDelayed(mRunnable, 1000);
                } else if (answer.contains("그럼 주변 식당 목록을 보여드릴께요")) {
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            //전문음식점 말고도 패스트푸드,치킨 음식 관련들은 전부다 입력시키도록한다.
                            String grm = "전문음식점";
                            Intent brainIntent1 = new Intent(MainActivity.this, TmapPoiActivity.class);
                            brainIntent1.putExtra(TmapPoiActivity.EXTRA_QUESTION, grm);
                            startActivity(brainIntent1);
                        }
                    };
                    mHandler = new Handler();
                    mHandler.postDelayed(mRunnable, 1000);
                } else if (answer.contains("전화 번호 입력합니다")) {
                    getContactList();
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getPhonenum));
                    Log.d("전화 걸기", "번호: " + intent);
                    startActivity(intent);
                } else if (answer.contains("그럼 기다려 주세요 제가 운동법을 찾아볼게요")) {
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String grm = "헬스운동법";
                            Intent intent1 = new Intent(Intent.ACTION_WEB_SEARCH);
                            intent1.putExtra(SearchManager.QUERY, grm);
                            startActivity(intent1);
                        }
                    };
                    mHandler = new Handler();
                    mHandler.postDelayed(mRunnable, 1000);
                }
            }
        }
    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiverSub extends BroadcastReceiver {

        private ResponseReceiverSub() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            //답변 호출_2차 답변
            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)) {
                String answer1 = intent.getStringExtra(Constants.EXTRA_BRAIN_ANSWER_SUB);

                Log.d("onReceive","answer1: "+answer1);
                Log.e("BrainService", "onStartCommand() onReceive:" + answer1);
                adapter.add(new ChatMessage(true, answer1));
                adapter.notifyDataSetChanged();
                mTTS.speak(answer1, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    void startRepeat(){
        // prepare the TTS to repeat chosen words
        Intent checkTTSIntent = new Intent();
        // check TTS data
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        // start the checking Intent - will retrieve result in onActivityResult
        startActivityForResult(checkTTSIntent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    /**
     * onInit fires when TTS initializes
     */
    public void onInit(int initStatus){
        //if successful, setlocale
        if (initStatus == TextToSpeech.SUCCESS){
            mTTS.setLanguage(Locale.KOREAN);
            // ***choose your own locale
            // here***
        }
    }

    //전화부 가져오기
    private ArrayList<Contact> getContactList() {
        String setMyname = question.substring(0,question.length()-7);
        Log.d("ContactsList","setMyname: "+setMyname);

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = managedQuery(uri, projection, null,
                selectionArgs, sortOrder);

        ArrayList<Contact> contactlist = new ArrayList<Contact>();

        if (contactCursor.moveToFirst()) {
            do {
                String phonenumber = contactCursor.getString(1).replaceAll("-",
                        "");
                if (phonenumber.length() == 10) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 6) + "-"
                            + phonenumber.substring(6);
                } else if (phonenumber.length() > 8) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 7) + "-"
                            + phonenumber.substring(7);
                }

                Contact acontact = new Contact();
                acontact.setPhotoid(contactCursor.getLong(0));
                acontact.setPhonenum(phonenumber);
                acontact.setName(contactCursor.getString(2));

                contactlist.add(acontact);
                String equalName = acontact.getName();
                if (setMyname.equals(equalName)){
                    getPhonenum = acontact.getPhonenum();
                    Log.d("getContactList()","myNum: "+acontact.getPhonenum()+"myName: "+acontact.getName());
                }
            } while (contactCursor.moveToNext());
        }
        return contactlist;
    }

}