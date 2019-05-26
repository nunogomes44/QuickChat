package com.example.quickchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;



public class SplashActivity extends AppCompatActivity {

    private ImageView imageView, imageViewLogin, imageViewRegister;
    private Animation bganim;
    private Button textViewLogin, textViewRegister;
    private TextView textView;
    private Animation frombottom, fromback;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);

        fromback = AnimationUtils.loadAnimation(this, R.anim.fromback);


        imageView = findViewById(R.id.imageView);

        textView = findViewById(R.id.textViewSlogan);

        bganim = AnimationUtils.loadAnimation(this, R.anim.bganim);


        linearLayout = findViewById(R.id.linearLayout);


        imageView.animate().translationY(-400).setDuration(3000).setStartDelay(3000);
        

        linearLayout.startAnimation(frombottom);
        textView.startAnimation(fromback);
    }

    public void abrirCadastro(View view){
        Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeCustomAnimation(getApplicationContext(), R.anim.zoom_in, R.anim.zoom_out);
        ActivityCompat.startActivity(SplashActivity.this, intent,  activityOptionsCompat.toBundle());

    }

    public void abrirLogin(View view){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeCustomAnimation(getApplicationContext(), R.anim.zoom_in, R.anim.zoom_out);
        ActivityCompat.startActivity(SplashActivity.this, intent,  activityOptionsCompat.toBundle());
        //startActivity(intent);
    }
}
