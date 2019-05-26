package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RegisterActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RegisterPhoneFragment registerPhone = new RegisterPhoneFragment();
    private RegisterEmailFragment registerEmail = new RegisterEmailFragment();

    private BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.login_using_phone);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public void abrirLogin(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeCustomAnimation(getApplicationContext(), R.anim.zoom_in, R.anim.zoom_out);
        ActivityCompat.startActivity(RegisterActivity.this, intent,  activityOptionsCompat.toBundle());
        //startActivity(intent);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.login_using_phone:
                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.fade_in, R.anim.fade_in).
                        replace(R.id.container, registerPhone).commit();
                return true;

            case R.id.login_using_email:
                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.anim.fade_in, R.anim.fade_in).
                        replace(R.id.container, registerEmail).commit();
                return true;
        }
        return false;
    }
}
