package com.police170m3.rpi.jjhchatbot;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.police170m3.rpi.jjhchatbot.DBSet.DBMyWordHelper;
import com.police170m3.rpi.jjhchatbot.DBSet.DB_Word_Greeting;
import com.police170m3.rpi.jjhchatbot.jjhTmapView.TmapDrawpassActivity;
import com.police170m3.rpi.jjhchatbot.jjhTmapView.TmapPoiActivity;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.WeatherActivity;
import com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.WeatherCitiesActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.ButterKnife;

@SuppressWarnings("MissingPermission")
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,AsyncResponse{

    @BindString(R.string.mapSearch) String mapSearch;
    @BindString(R.string.mapPoi)String mapPoi;
    @BindString(R.string.search)String Search;
    @BindString(R.string.situation_cook_yes)String situation_yes;
    @BindString(R.string.situation_cook_no)String situation_no;
    @BindString(R.string.weatherToday)String weatherToday;
    @BindString(R.string.weathercity)String weatherCity;
    @BindString(R.string.callPhone)String callPhone;
    @BindString(R.string.situation_cook_answer)String situationWords;
    @BindArray(R.array.search) String[] searchWords;
    @BindArray(R.array.citiesKr)String[] WeatherKr;
    @BindArray(R.array.citiesUs)String[] WeatherUs;

    public final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    DB_Word_Greeting db_word_greeting = new DB_Word_Greeting(MainActivity.this);
    private DBMyWordHelper dbMyWordHelper;

    private static ChatArrayAdapter adapter;

    private ResponseReceiver mMessageReceiver;
    private ResponseReceiverSub mMessageReceiverSub;
    private Handler mHandler;
    private Runnable mRunnable;
    private TextToSpeech mTTS;

    private EditText chatEditText;
    private ListView chatListView;

    String question;
    String getPhonenum;

    ArrayList<String> chatList = new ArrayList<>();
    ArrayList<String> SetChatList = new ArrayList<>();
    ArrayList<String> WordLogList = new ArrayList<>();
    // get myword from created
    ArrayList<HashMap<String, String>> getMyWordsHash;
    HashMap<String, String> resultp = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startRepeat();
        mTTS = new TextToSpeech(this,this);
        dbMyWordHelper = new DBMyWordHelper(getApplicationContext(), "setmyword.db",null,1);

        if (savedInstanceState == null) {
            Log.d("MainActivity", "onCreate savedInstanceState null");
            adapter = new ChatArrayAdapter(getApplicationContext());
        }
        ButterKnife.bind(this);
        chatEditText = (EditText)findViewById(R.id.chat_editText);
        chatListView = (ListView)findViewById(R.id.chat_listView);

        chatListView.setAdapter(adapter);

        chatEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    question = chatEditText.getText().toString();
                    adapter.add(new ChatMessage(false, question));
                    chatEditText.setText("");

                    mHandler = new Handler();

                    getMyDbPHP(question);
                    return true;
                }

                return false;
            }
        });

        // 키보드 셋 가림
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //에딧텍스트에 입력된 값을 서버에 넘기고 DB값과 일치하면 그에 맞는 답변 텍스트를 받는다
    public void getMyDbPHP(String question){
        String s = question;
        chatList = db_word_greeting.setWordGreeting(s);

        getMyWordsHash = dbMyWordHelper.getMyWord();
        String getStr = chatList.get(0);

        SetChatList.add(s);
        HashMap<String,String> postData = new HashMap<String,String>();
        postData.put("mobile","android");
        postData.put("txtQuestion", getStr);

        for (HashMap getData : getMyWordsHash) {
            resultp = getData;
        }

        String getMyWords = resultp.get(Constants.myword);
        String getMyAnswers = resultp.get(Constants.myanswer);
        if (question.contains(getMyWords)){
            //안드로이드 내부 DB에 입력된 텍스트
            Log.d("MainActivity","getMyWordDb: "+getMyWords);
            Intent brainIntent = new Intent(MainActivity.this, BrainService.class);
            brainIntent.setAction(BrainService.ACTION_QUESTION);
            brainIntent.putExtra(BrainService.EXTRA_QUESTION, getMyAnswers);
            Log.d("MainActivity","processFinish: "+getMyAnswers);
            startService(brainIntent);
        }else if (question.contains(getStr)){
            Log.d("MainActivity","question: "+question);
            //DB 서버에 입력된 텍스트
            PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData, this);
            task.execute("http://192.168.1.54/getchatword.php");
            //각종 예외처리를 한번에 묶어서 표시
            task.setEachExceptionsHandler(new EachExceptionsHandler() {
                @Override
                public void handleIOException(IOException e) {

                }

                @Override
                public void handleMalformedURLException(MalformedURLException e) {

                }

                @Override
                public void handleProtocolException(ProtocolException e) {

                }

                @Override
                public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {

                }
            });
        }else {
            //DB에 입력된 텍스트가 없을때
            PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData, this);
            task.execute("http://192.168.1.54/getchatword.php");
            task.setEachExceptionsHandler(new EachExceptionsHandler() {
                @Override
                public void handleIOException(IOException e) {

                }

                @Override
                public void handleMalformedURLException(MalformedURLException e) {

                }

                @Override
                public void handleProtocolException(ProtocolException e) {

                }

                @Override
                public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {

                }
            });
        }
        chatList.clear();
    }

    //받아온 답변 텍스트를 BrainService 클래스에 넘겨서 반대편 채팅창에 출력되게끔 한다
    @Override
    public void processFinish(String s) {
        Intent brainIntent = new Intent(MainActivity.this, BrainService.class);
        brainIntent.setAction(BrainService.ACTION_QUESTION);
        brainIntent.putExtra(BrainService.EXTRA_QUESTION, s);
        Log.d("MainActivity","processFinish: "+s);
        startService(brainIntent);
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
        }else if (id == R.id.set_my_word){
            showSetMyWordDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    // 인텐트 서비스로부터 받아온 리시버값에 따라 명령을 수행한다
    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        // 다음 오버라이드에 받아온 값에 알맞는 명령을 수행시켜줄 코드를 입력한다
        @Override
        public void onReceive(Context context, Intent intent) {
            //답변 호출
            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_ANSWER)) {
                String answer = intent.getStringExtra(Constants.EXTRA_BRAIN_ANSWER);

                for (int i =0; i < SetChatList.size(); i++){
                    Log.e("MainActivity", "onStartCommand() SetChatList:" + SetChatList.get(i));
                }
                String getAnswer = question;

                Log.e("BrainService", "onStartCommand() onReceive:" + answer);
                adapter.add(new ChatMessage(true, answer));
                adapter.notifyDataSetChanged();
                mTTS.speak(answer, TextToSpeech.QUEUE_FLUSH, null);

                // String-상호작용-배고파
                if (answer.contains(situationWords)){
                    WordLogList.add(situationWords);
                }
                if (answer.contains(situation_yes)){
                    if (WordLogList.contains(situationWords)){
                        String grm = getResources().getString(R.string.takecuisine);
                        Intent intentcuisine = new Intent(Intent.ACTION_WEB_SEARCH);
                        intentcuisine.putExtra(SearchManager.QUERY, grm);
                        startActivity(intentcuisine);
                        WordLogList.clear();
                    }
                }else if (answer.contains(situation_no)){
                    if (WordLogList.contains(situationWords)){
                        String grm = getResources().getString(R.string.searchcuisine);
                        Intent intentcuisine = new Intent(MainActivity.this, TmapPoiActivity.class);
                        intentcuisine.putExtra(TmapPoiActivity.EXTRA_QUESTION, grm);
                        startActivity(intentcuisine);
                        WordLogList.clear();
                    }
                }

                // AnswerList
                for (int i=0; i < searchWords.length; i++) {
                    String answerList = searchWords[i];
                    if (answer.contains(answerList)){
                        if (getAnswer.contains(mapSearch)){
                            // String-까지 가는길 알려줘
                            String grm = getAnswer.substring(0,getAnswer.indexOf(mapSearch));
                            Intent intentWay = new Intent(MainActivity.this, TmapDrawpassActivity.class);
                            intentWay.putExtra(TmapDrawpassActivity.EXTRA_QUESTION, grm);
                            startActivity(intentWay);
                            Log.d("processFinish","getStr: "+grm);
                        }
                        if (getAnswer.contains(mapPoi)){
                            // String-근처 찾아봐
                            String grm = getAnswer.substring(0,getAnswer.indexOf(mapPoi));
                            Log.d("question","grm1: "+grm.length());
                            Intent intentPoi = new Intent(MainActivity.this, TmapPoiActivity.class);
                            intentPoi.putExtra(TmapPoiActivity.EXTRA_QUESTION, grm.trim());
                            startActivity(intentPoi);
                        }
                        if (getAnswer.contains(Search)){
                            // String-검색
                            String grm = getAnswer.substring(0,getAnswer.indexOf(Search));
                            Log.d("processFinish","getStr: "+grm);
                            Intent intentSearch= new Intent(Intent.ACTION_WEB_SEARCH);
                            intentSearch.putExtra(SearchManager.QUERY, grm);
                            startActivity(intentSearch);
                        }
                        if (getAnswer.contains(weatherToday)){
                            //String-오늘 날씨 어때
                            mRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                                    startActivity(intent);
                                }
                            };
                            mHandler = new Handler();
                            mHandler.postDelayed(mRunnable, 1500);
                        }
                        if (getAnswer.contains(weatherCity)){
                            //String-날씨 알려줘
                            for (int k = 0 ; k <= WeatherKr.length - 1; k++){
                                if (getAnswer.contains(WeatherKr[k])){
                                    String grm = getAnswer.replace(WeatherKr[k],WeatherUs[k]);
                                    Log.d("question","citiesKr:" +grm);
                                    final String grm1 = grm.substring(0,grm.indexOf(weatherCity));
                                    Log.d("question","citiesKr:" +grm1.length());

                                    mRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("question","citiesKr:" +grm1.length());
                                            Intent brainIntent1 = new Intent(MainActivity.this, WeatherCitiesActivity.class);
                                            brainIntent1.putExtra(WeatherCitiesActivity.EXTRA_QUESTION, grm1.trim());
                                            startActivity(brainIntent1);
                                        }
                                    };
                                    mHandler = new Handler();
                                    mHandler.postDelayed(mRunnable, 1000);
                                }
                            }
                        }
                        if(getAnswer.contains(callPhone)){
                            //String-전화 입력해
                            String grm = getAnswer.substring(0,getAnswer.indexOf(callPhone));
                            getContactList(grm);
                            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getPhonenum));
                            startActivity(intent);
                        }
                    }
                    SetChatList.clear();
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
                //mTTS.speak(answer1, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    private void startRepeat(){
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
    private ArrayList<Contact> getContactList(String s) {
        String setMyname = s.trim();
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

    //옵션-말 가르치기
    private void showSetMyWordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Teach Words");
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_setmyword,null);
        final EditText wordInput = (EditText)dialogView.findViewById(R.id.setwordedt);
        final EditText answerInput = (EditText)dialogView.findViewById(R.id.setansedt);
        builder.setView(dialogView);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String getWord = wordInput.getText().toString();
                String getAnswer = answerInput.getText().toString();
                dbMyWordHelper.insert(getWord,getAnswer);
            }
        });
        builder.show();
    }

}