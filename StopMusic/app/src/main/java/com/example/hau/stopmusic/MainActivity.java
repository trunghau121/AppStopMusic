package com.example.hau.stopmusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    public Intent intentService = null;
    private long time = 0;
    String strtime = "";
    public TextView txtTime;
    private TimePickerDialog timePickerDialog;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.btcustom).setOnClickListener(this);

        txtTime = (TextView) findViewById(R.id.txtTime);
        createTimerPicker();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        if (savedInstanceState != null && savedInstanceState.getString("timeset") != null
                && savedInstanceState.getLong("time") > 0) {
            time = savedInstanceState.getLong("time");
            strtime = savedInstanceState.getString("timeset");
            txtTime.setText("Thời gian tắt nhạc :" + strtime);
        } else {
            intentService = new Intent(MainActivity.this, NotificationService.class);
            intentService.putExtra("time", (long) 0);
            startService(intentService);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (time > 0) {
                    showDialog(strtime);
                } else {
                    Toast.makeText(this, "Bạn chưa chọn thời gian tắt nhạc !", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.stop:
                Toast.makeText(this, "Hủy tắt nhạc !", Toast.LENGTH_LONG).show();
                if (intentService != null) {
                    stopService(intentService);
                    canceNotification();
                }
                txtTime.setText(getString(R.string.hello_world));
                strtime = "";
                time = 0;
                break;
            case R.id.button:
                setTime(0, 30);
                break;
            case R.id.button2:
                setTime(1, 0);
                break;
            case R.id.button3:
                setTime(2, 0);
                break;
            case R.id.button4:
                setTime(3, 0);
                break;
            case R.id.button5:
                setTime(1, 30);
                break;
            case R.id.btcustom:
                if (timePickerDialog != null) {
                    timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
                }
                break;
            default:
                break;
        }

    }

    public void showDialog(String content) {
        new MaterialDialog.Builder(this)
                .title("THỜI GIAN TẮT NHẠC")
                .titleGravity(GravityEnum.CENTER)
                .titleColor(getResources().getColor(R.color.color_shadow_button))
                .content(content)
                .contentGravity(GravityEnum.CENTER)
                .positiveText("Cài đặt").negativeText("Hủy")
                .contentColor(Color.BLACK)
                .positiveColor(getResources().getColor(R.color.color_shadow_button))
                .negativeColor(Color.GRAY)
                .backgroundColor(Color.WHITE)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (intentService != null) {
                            stopService(intentService);
                        }
                        intentService = new Intent(MainActivity.this, NotificationService.class);
                        intentService.putExtra("time", time);
                        startService(intentService);
                        Toast.makeText(getApplicationContext(), "Cài đặt thành công!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (strtime.length() > 0 && time > 0) {
            outState.putString("timeset", strtime);
            outState.putLong("time", time);
        } else {
            outState.putString("timeset", "");
            outState.putLong("time", 0);
        }
    }

    public void createTimerPicker() {
        Calendar now = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(MainActivity.this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), true);
    }

    public void canceNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationService.NOTIFICATION_ID);
    }

    public long getTimeSeconds(int hour, int minute) {
        return (hour * (60 * 60)) + (minute * 60);
    }

    public void setTime(int hour, int minute) {
        strtime = hour + " giờ " + minute + " phút";
        time = getTimeSeconds(hour, minute);
        txtTime.setText("Thời gian tắt nhạc: " + strtime);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        setTime(hourOfDay, minute);
    }
}
