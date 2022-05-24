package com.example.inai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inai.myActivities.LoginActivity;
import com.example.inai.myActivities.SignUpActivity;

public class SplashActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottieAnimationView = findViewById(R.id.lottie);

        lottieAnimationView.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
        int SPLASH_DISPLAY_LENGTH = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}