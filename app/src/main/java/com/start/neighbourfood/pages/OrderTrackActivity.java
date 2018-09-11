package com.start.neighbourfood.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.NFUtils;
import com.start.neighbourfood.services.Config;

public class OrderTrackActivity extends BaseActivity {

    private TextView timer;
    private long StartTime;
    private Handler handler;
    private boolean mStopHandler = false;
    public Runnable runnable = new Runnable() {

        public void run() {

            int Seconds, Minutes;

            long updateTime = SystemClock.uptimeMillis() - StartTime;

            Seconds = (int) (updateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            timer.setText(String.format("%02d:%02d", Minutes,Seconds));

            if (!mStopHandler) {
                handler.postDelayed(this, 1000);
            }
        }

    };
    private Animation animFadein;
    private ImageView orderConfirmImg, foodPreparedImg;
    private TextView preparedFoodText, orderConfirmText;
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

            if (NFUtils.isOrderAcceptanceNotification(message)){
                setUIForOrderConfirmation();
            }
            else if (NFUtils.isFoodPrepared(message)){
                setUIForFoodPrepared();
            }


            //do other stuff here
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_track);
        Toolbar toolbar = findViewById(R.id.toolbar);
        timer = findViewById(R.id.timer);
        setSupportActionBar(toolbar);
        StartTime = SystemClock.uptimeMillis();
        orderConfirmImg = findViewById(R.id.confirm_img);
        foodPreparedImg = findViewById(R.id.foodPrepared_img);
        preparedFoodText = findViewById(R.id.foodPrepared_txt);
        orderConfirmText = findViewById(R.id.confirm_text);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler = new Handler();
        handler.post(runnable);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);

    }

    @Override
    public void onPause() {
        super.onPause();
        saveStringInSharedPreference("startTime",String.valueOf(StartTime));
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        String time = getFromSharedPreference("startTime");
        if (time != null) {
            StartTime = Long.parseLong(time);
        }
        registerReceiver(mMessageReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    private void setUIForFoodPrepared() {
        //setUIForOrderConfirmation();
        findViewById(R.id.finish_layout).setVisibility(View.VISIBLE);
        foodPreparedImg.setImageResource(R.drawable.green_tick);
        preparedFoodText.setText("Food prepared");
    }

    private void setUIForOrderConfirmation() {
        orderConfirmImg.setImageResource(R.drawable.green_tick);
        orderConfirmText.setText("Member accepted the food order");
        findViewById(R.id.food_prepared_layout).setVisibility(View.VISIBLE);
        foodPreparedImg.setImageResource(R.drawable.wait);
        preparedFoodText.setText("Food is being prepared");
    }


}
