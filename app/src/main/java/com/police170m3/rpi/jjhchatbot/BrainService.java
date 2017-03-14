package com.police170m3.rpi.jjhchatbot;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by JaeHun Jung on 02/10/2017.
 */
@SuppressWarnings("MissingPermission")
public class BrainService extends Service {

    public static final String WEATHER_ACTION_QUESTION = "com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.Weather_Service.ACTION_QUESTION";
    public static final String ACTION_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.ACTION_QUESTION";
    public static final String ACTION_WEATHER_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.ACTION_WEATHER_QUESTION";
    public static final String ACTION_TMAP_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.ACTION_TMAP_QUESTION";
    public static final String ACTION_STOP = "com.police170m3.rpi.jjhchatbot.BrainService.ACTION_STOP";
    public static final String EXTRA_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.EXTRA_QUESTION";
    public static final String EXTRA_TMAP_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.EXTRA_TMAP_QUESTION";
    public static final String EXTRA_WEATHER_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.EXTRA_WEATHER_QUESTION";
    public static final String EXTRA_MYWORDQUESTIONS_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.EXTRA_MYWORDQUESTIONS_QUESTION";

    public static String[][] chatBotList={
            //인사 관련
            {"hi","hello","hola","안녕","안녕하세요","좋은 날이에요"},
            {"반가워요~","제 이름은 지니핑!","잘 지내고 있어요!","언제든지 대기중입니다","하실 말씀있나요?"},
            //검색 관련
            {"검색을 시작합니다.","찾아볼게요","바로 답해 드리죠!","잠시만요~","바로 찾을테니 기다려주세요.","검색중"},
            //질답 관련
            {"how are you?","요즘 잘 지내니?"},
            {"good","doing well"},
            //default-여기서 물결표시는 없애도록한다. 또는 물결표빼고 말하게한다
            {"잘 모르겠군요","무슨 말씀인지 잘 모르겠어요","지니핑은 외출중이니까 부르지마요~","그건 제 기능에 없는 말이랍니다~","버전업되면 좀 더 이해 가능할거에요 ^.^"},
            //검색 관련-길찾기
            {"길을 찾아보도록 하죠","길찾기 검색을 시작합니다.","찾아보도록 할께요"}
    };

    ArrayList<String> arrayList = new ArrayList<>();

    //chatBotList의 내용들을 랜덤으로 출력시키기위한 배열 자리의 변수
    int listhello =0;//인사 관련
    int listhello1 =1;//인사1 관련
    int listsearch =2;//검색 관련
    int listdefault =5;//디폴트 관련
    int listfindway =6;//길찾기 관련

    private Handler mHandler;
    private Runnable mRunnable;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BrainService","onCreate()");

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Intent loadingIntent =
                new Intent(Constants.BROADCAST_ACTION_BRAIN_STATUS)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTRA_BRAIN_STATUS, Constants.STATUS_BRAIN_LOADING);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(loadingIntent);

        if (intent==null){
            Log.d("BrainService","onStartCommand() null");
            return Service.START_STICKY;
        }

        String action = intent.getAction();

        if(action.equalsIgnoreCase(ACTION_QUESTION)){
            int hrand=(int)(Math.random()*chatBotList[listhello].length);       //인사 관련 리스트
            int h1rand=(int)(Math.random()*chatBotList[listsearch].length);     //인사1 관련 리스트
            int drand=(int)(Math.random()*chatBotList[listdefault].length);     //디폴트 관련 리스트
            int fwrand=(int)(Math.random()*chatBotList[listfindway].length);    //길찾기 관련 리스트
            String question = intent.getStringExtra(EXTRA_QUESTION);
            if(question!=null){
                Log.d("BrainService","onStartCommand() question:"+question);
                String answer = "";
                if (question.contains("안녕")){
                    answer = chatBotList[listhello][hrand];
                    mRunnable = new Runnable() {
                        String answerSub = "";
                        @Override
                        public void run() {
                            answerSub = "잘지내고 있었어요?";
                            Log.d("BrainService","onStartCommand() question:"+answerSub);
                            Intent localIntent1 =
                                    new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                                            // Puts the answer into the Intent
                                            .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, answerSub);
                            // Broadcasts the Intent to receivers in this app.
                            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
                            arrayList.add(answerSub);
                        }
                    };
                    mHandler = new Handler();
                    mHandler.postDelayed(mRunnable, 2000);
                    Log.d("BrainService","onStartCommand() question:"+chatBotList[listhello][hrand]);
                }else if (question.contains("잘 지내니")){
                    answer = chatBotList[listhello1][hrand];
                }
                else if(question.contains("검색")){
                    answer = chatBotList[listsearch][h1rand];
                    Log.d("BrainService","onStartCommand() question:"+chatBotList[listsearch][h1rand]);
                } else if(question.contains("배고파")){
                    answer = "배 고프시다면 식당으로 가실래요?";
                    arrayList.add(answer);
                    Log.e("BrainService","arrayList: " + arrayList);
                }else if(question.contains("날씨가 좋아")){
                    //이미지 가져오기
                    answer = "오늘 날씨가 좋으면 이거랑 비슷하겠네요?";
                }else if(question.contains("오늘 날씨 어때")){
                    answer = chatBotList[listsearch][h1rand];
                }else if(question.contains("날씨 알려줘")){
                    answer = chatBotList[listsearch][h1rand];
                }else if(question.contains("아니")){
                    //아니라고 했을때 여러가지 상황을 입력한다
                    //식사 상황에 관련된것 입력
                    Log.e("BrainService","arrayList: " + arrayList);
                    if (arrayList.contains("배 고프시다면 식당으로 가실래요?")){
                        answer = "요리해서 드실려면 제가 추천해드릴 목록이 있어요";
                        arrayList.add(answer);
                    }else if (arrayList.contains("잘지내고 있었어요?")){
                        answer = "무슨 일 있으셨나 보네요";
                        mRunnable = new Runnable() {
                            String answerSub = "";
                            @Override
                            public void run() {
                                answerSub = "힘내라고 하고싶지만 제가 도울 수 있는건 없어서 아쉬워요";
                                Log.d("BrainService","onStartCommand() question:"+answerSub);
                                Intent localIntent1 =
                                        new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                                                // Puts the answer into the Intent
                                                .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, answerSub);
                                // Broadcasts the Intent to receivers in this app.
                                LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
                                arrayList.add(answerSub);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 2000);
                    }
                }else if(question.contains("그래")){
                    //긍정대답의 상호작용을 입력
                    //식사 상황에 관련된것 입력
                    Log.e("BrainService","arrayList: " + arrayList);
                    if (arrayList.contains("배 고프시다면 식당으로 가실래요?")){
                        answer = "그럼 주변 식당 목록을 보여드릴께요";
                        arrayList.add(answer);
                    }else if (arrayList.contains("잘지내고 있었어요?")){
                        answer = "저도 요즘 잘 지내지만 아직은 배울게 많아요";
                        mRunnable = new Runnable() {
                            String answerSub = "";
                            @Override
                            public void run() {
                                answerSub = "하실 명령 있으면 말씀하세요";
                                Log.d("BrainService","onStartCommand() question:"+answerSub);
                                Intent localIntent1 =
                                        new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                                                // Puts the answer into the Intent
                                                .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, answerSub);
                                // Broadcasts the Intent to receivers in this app.
                                LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 3800);
                        arrayList.clear();
                    }
                }else if (question.contains("고마워")){
                    if (arrayList.contains("요리해서 드실려면 제가 추천해드릴 목록이 있어요")){
                        answer = "제가 해야할 일이랍니다 ^.^";
                        arrayList.clear();
                        Log.e("BrainService","arrayList: " + arrayList);
                    }else if(arrayList.contains("그럼 주변 식당 목록을 보여드릴께요")){
                        answer = "제가 해야할 일이랍니다 ^.^";
                        arrayList.clear();
                    }else if (arrayList.contains("까지 가는길 알려줘")){
                        answer = "제가 해야할 일이랍니다 ^.^";
                        arrayList.clear();
                    }else if (arrayList.contains("근처 찾아봐")){
                        answer = "제가 해야할 일이랍니다 ^.^";
                        arrayList.clear();
                    }else if (arrayList.contains("주인님도 운동하시면 몸이 좋아지실거에요 ^.^")){
                        answer = "그럼 기다려 주세요 제가 운동법을 찾아볼게요";
                        arrayList.clear();
                    }else if (arrayList.contains("힘내라고 하고싶지만 제가 도울 수 있는건 없어서 아쉬워요")){
                        answer = "그럼 시작해 볼까요!";
                        mRunnable = new Runnable() {
                            String answerSub = "";
                            @Override
                            public void run() {
                                answerSub = "하실 명령 있으면 말씀하세요";
                                Log.d("BrainService","onStartCommand() question:"+answerSub);
                                Intent localIntent1 =
                                        new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                                                // Puts the answer into the Intent
                                                .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, answerSub);
                                // Broadcasts the Intent to receivers in this app.
                                LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
                            }
                        };
                        mHandler = new Handler();
                        mHandler.postDelayed(mRunnable, 2000);
                        arrayList.clear();
                    }else{
                        answer = "제가 한 일이 뭔지는 모르겠지만 고마워요";
                    }
                }else if (question.contains("여긴 어디야")){
                    answer = chatBotList[listsearch][h1rand];
                }else if (question.contains("근처 찾아봐")){
                    answer = chatBotList[listfindway][fwrand];
                    arrayList.add(answer);
                }else if (question.contains("까지 가는길 알려줘")){
                    answer = chatBotList[listfindway][fwrand];
                    arrayList.add(answer);
                }else if (question.contains("전화 입력해")){
                    answer = "전화 번호 입력합니다";
                }
                // 해당 명령에 없는 말을 할때
                else{
                    answer = chatBotList[listdefault][drand];
                }
                //배터리 관련 내용
                if (question.contains("배터리 잔량")){
                    Intent batteryStatus = getApplicationContext().registerReceiver(null,new
                            IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                    final int ratio = (level * 100) / scale;

                    if (ratio>=80){
                        answer = "잔량은: "+ratio+"% 입니다.\n"+"충전이 거의 다 완료된것 같아요";
                        arrayList.add("충전이 거의 다 완료된것 같아요");
                        Log.d("answer","arrayList: "+arrayList);
                    }else{
                        answer = "잔량은: "+ratio+"% 입니다.\n"+"충전이 좀 필요하겠군요";
                        arrayList.add("충전이 좀 필요하겠군요");
                        Log.d("answer","arrayList: "+arrayList);
                    }
                }else if(question.contains("충전 해야겠네")){
                    if (arrayList.contains("충전이 좀 필요하겠군요")){
                        answer = "저도 배고파 질려고해요 주인님";
                        arrayList.add(answer);
                    }else if (arrayList.contains("충전이 거의 다 완료된것 같아요")){
                        answer = "글쎄요 전 다이어트가 좀 필요하겠네요\n"+"로봇이라서 체형은 그대로지만 말이죠";
                        arrayList.add("로봇이라서 체형은 그대로지만 말이죠");

                    }
                }else if (question.contains("알겠어 보채지마")){
                    if (arrayList.contains("저도 배고파 질려고해요 주인님")) {
                        answer = "전기는 급속충전해줘요 ^.^";
                        arrayList.clear();
                    }
                }else if (question.contains("내가 다이어트 필요한것 같은데")){
                    if (arrayList.contains("로봇이라서 체형은 그대로지만 말이죠")) {
                        answer = "주인님도 운동하시면 몸이 좋아지실거에요 ^.^";
                        arrayList.add(answer);
                    }
                }
                //날씨 상호작용 대사
                if (question.contains("비가 오고 있어")){
                    answer = "비가 오나 확인 해보죠";
                }else if(question.contains("아닌가 보네 미안")){
                    if (arrayList.contains("습도가 낮은데요 정말로 비가 오는거 맞나요?")) {
                        answer = "절 속이지 마요 -.-";
                        arrayList.add(answer);
                    }
                }else if(question.contains("내말 맞지")){
                    if (arrayList.contains("습도가 좀 높은걸보니 비가 오나보네요")) {
                        answer = "그럼요 절 주인님을 믿어요";
                        arrayList.clear();
                    }
                }else if (question.contains("미안해")){
                    answer = "알아요 장난이라는거";
                    mRunnable = new Runnable() {
                        String answerSub = "";
                        @Override
                        public void run() {
                            answerSub = "다음엔 그러지 않으면 되요";
                            Log.d("BrainService","onStartCommand() question:"+answerSub);
                            Intent localIntent1 =
                                    new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                                            // Puts the answer into the Intent
                                            .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, answerSub);
                            // Broadcasts the Intent to receivers in this app.
                            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
                        }
                    };
                    mHandler = new Handler();
                    mHandler.postDelayed(mRunnable, 2000);
                    arrayList.clear();
                }

                //question End
                Intent localIntent =
                        new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER)
                                // Puts the answer into the Intent
                                .putExtra(Constants.EXTRA_BRAIN_ANSWER, answer);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent);
            }

            return Service.START_STICKY;
        }else if(action.equalsIgnoreCase(ACTION_WEATHER_QUESTION)) {
            //Tmap Poi에서의 예외 처리
            String answerSub = "";
            float humidity = intent.getFloatExtra(EXTRA_WEATHER_QUESTION,0.0f);
            if (humidity<=70){
                answerSub= "습도가 낮은데요 정말로 비가 오는거 맞나요?";
                Log.d("BrainService","onStartCommand() humidity:"+humidity);
                Log.d("BrainService","onStartCommand() answerSub:"+answerSub);
                arrayList.add(answerSub);
            }else{
                answerSub="습도가 좀 높은걸보니 비가 오나보네요";
                arrayList.add(answerSub);
            }
            //question End
            Intent localIntent1 =
                    new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                            // Puts the answer into the Intent
                            .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, answerSub);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
        }

        if(action.equalsIgnoreCase(ACTION_STOP)){
            Log.d("BrainService","onStartCommand() ACTION_STOP");
            stopForeground(true);
            stopSelf();
            return Service.START_NOT_STICKY;
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        //return null;
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BrainService", "onDestroy()");
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BrainService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BrainService.this;
        }
    }
}
