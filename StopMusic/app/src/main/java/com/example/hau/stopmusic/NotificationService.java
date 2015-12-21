package com.example.hau.stopmusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.logging.LogRecord;

/**
 * Created by HAU on 9/28/2015.
 */
public class NotificationService extends Service {
    long time = 0;
    private Handler progressBarHandler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        long value =intent.getLongExtra("time", 0);
        if(value > 0){
            time =value;
            this.startForeground();
        }
        return START_STICKY;

    }

    private void startForeground() {
        startForeground(NOTIFICATION_ID, getMyActivityNotification("" + getDurationTimer(time)));
        updataProgress();

    }
    private void updataProgress() {
        // TODO Auto-generated method stub
        time--;
        progressBarHandler.postDelayed(mUpdateTimeTask, 1000);
    }

    private Runnable mUpdateTimeTask = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            time--;
            if(time > -1){
                updateNotification(""+getDurationTimer(time));
                progressBarHandler.postDelayed(this, 1000);
            }else{
                updateNotification("Đã tắt nhạc !");
                killMusic();
                progressBarHandler.removeCallbacks(mUpdateTimeTask);
            }

        }
    };
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
    }

    private Notification getMyActivityNotification(String text) {

        CharSequence title =getText(R.string.title_notification);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        return new Notification.Builder(this).setContentTitle(title)
                .setContentText(text).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent).getNotification();
    }
    public String getDurationTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";

        int hours = (int)( milliseconds / (60*60));
        int minutes = (int)(milliseconds % (60*60)) / (60);
        int seconds = (int) ((milliseconds % (60*60)) % (60));
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}
        if(minutes < 10){
            minutesString = "0" + minutes;
        }else{
            minutesString = "" + minutes;}
        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


    private void updateNotification(String dem) {
        String text = "Thời gian còn lại: "+dem;

        Notification notification = getMyActivityNotification(text);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
    public void killMusic(){
        long eventtime = SystemClock.uptimeMillis();
        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime,
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(eventtime, eventtime,
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_STOP, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        sendOrderedBroadcast(upIntent, null);
    }

}
