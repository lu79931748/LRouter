package com.lwf.lrouter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lwf.lrouter.R;
import com.lwf.router.annotations.Router;

@Router(path = "router://page-home")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}