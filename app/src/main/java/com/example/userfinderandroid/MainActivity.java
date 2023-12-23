package com.example.userfinderandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // Sample comment to test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void navigateToSearchScreen(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

}