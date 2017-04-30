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

/**
 * Created by JaeHun Jung on 02/10/2017.
 */
@SuppressWarnings("MissingPermission")
public class BrainService extends Service {

    public static final String ACTION_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.ACTION_QUESTION";
    public static final String ACTION_WEATHER_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.ACTION_WEATHER_QUESTION";
    public static final String ACTION_STOP = "com.police170m3.rpi.jjhchatbot.BrainService.ACTION_STOP";
    public static final String EXTRA_QUESTION = "com.police170m3.rpi.jjhchatbot.BrainService.EXTRA_QUESTION";

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
    // 서버에서 받아온 DB 텍스트를 반대편 채팅창으로 넘기기위한 단순 로직
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
            String question = intent.getStringExtra(EXTRA_QUESTION);
            Log.d("BrainService","onStartCommand: "+question);

            if (question.contains("지금 독서 중이에요")){
                question = "지금 독서 중이에요";

                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String str2 = "어떤 책 좋아하시나요?";
                        Intent localIntent1 =
                                new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                                        // Puts the answer into the Intent
                                        .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, str2);
                        // Broadcasts the Intent to receivers in this app.
                        LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
                    }
                };
                mHandler = new Handler();
                mHandler.postDelayed(mRunnable, 2000);
            }else if (question.contains("저도 읽어봤으면 좋겠네요")){
                question = "저도 읽어봤으면 좋겠네요";
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String str2 = "언젠가 한번 보도록 할게요";
                        Intent localIntent1 =
                                new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER_SUB)
                                        // Puts the answer into the Intent
                                        .putExtra(Constants.EXTRA_BRAIN_ANSWER_SUB, str2);
                        // Broadcasts the Intent to receivers in this app.
                        LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent1);
                    }
                };
                mHandler = new Handler();
                mHandler.postDelayed(mRunnable, 2000);
            }
            if (question.contains("잔량은")){
                Intent batteryStatus = getApplicationContext().registerReceiver(null, new
                        IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                final int ratio = (level * 100) / scale;
                if (ratio >= 80) {
                    question = "잔량은: " + ratio + "% 입니다.\n" + "충전이 거의 다 완료된것 같아요";
                } else {
                    question = "잔량은: " + ratio + "% 입니다.\n" + "충전이 좀 필요하겠군요";
                }
            }else if (question.contains("충전")){
                Intent batteryStatus = getApplicationContext().registerReceiver(null, new
                        IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                final int ratio = (level * 100) / scale;
                if (ratio >= 80) {
                    question = "충전을 많이하면 배터리 수명이 줄어드니 조금만 해주세요";
                } else {
                    question = "충전이 완료될때까지 대기하겠습니다.";
                }
            }

            //taskWords.getMyTaskWords(question);
            Intent localIntent =
                    new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER)
                            // Puts the answer into the Intent
                            .putExtra(Constants.EXTRA_BRAIN_ANSWER, question);
            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent);
            return Service.START_STICKY;
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
