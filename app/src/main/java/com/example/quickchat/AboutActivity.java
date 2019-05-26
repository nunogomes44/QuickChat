package com.example.quickchat;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import mehdi.sakout.aboutpage.AboutPage;

import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar =findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String descrition = "The CRN is a group of software developers which has been in the marked since 2017." +
                "The group seeks to provide the best solutions for the well being of the client based on those of information Technology for process automation.";
        View  sobre = new AboutPage(this)
                .setImage(R.drawable.looo).setDescription(descrition)
                .addGroup("Connect with us")
                .addEmail("skygomes09@gmail.com")
                .addWebsite("http://google.com/")
                .addFacebook("Git hub")
                .addTwitter("medyo80")
                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.ideashower.readitlater.pro")
                .addGitHub("dart-ice")
                .addInstagram("github")
                .create();

        setContentView(sobre);

    }
}
