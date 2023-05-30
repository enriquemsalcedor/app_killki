package com.killki.app_killki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public Button btn_sos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Intent i = new Intent(this, SosActivity.class );
        startActivity(i);

    }





}