package com.example.userfinderandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private LinearLayoutCompat emptyHistoryContainer;
    private ScrollView historyScrollView;
    private LinearLayoutCompat linearLayoutHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyScrollView = findViewById(R.id.history_scrollview);
        emptyHistoryContainer = findViewById(R.id.empty_history_container);

        linearLayoutHistoryContainer = new LinearLayoutCompat(this);
        linearLayoutHistoryContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutHistoryContainer.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutHistoryContainer.setPadding(getDimensionsInDp(10), getDimensionsInDp(16), getDimensionsInDp(10), getDimensionsInDp(16));

        // Para recuperar a lista de objetos do storage
        List<UserSearch> retrievedList = MyStorageManager.getObjectList(getApplicationContext());
        System.out.println("retrievedList - " + retrievedList.toString());

        if (retrievedList != null && !retrievedList.isEmpty()) {
            historyScrollView.removeAllViews();
            for (UserSearch user : retrievedList) {
                String fullName = user.getFullName();
                String username = user.getUsername();

                renderHistoryCard(fullName, username);
            }
        } else {
            System.out.println("STORAGE VAZIO");
        }
        historyScrollView.addView(linearLayoutHistoryContainer);
    }

    private void renderHistoryCard(String fullName, String username) {
        LinearLayoutCompat linearLayoutHistoryCard = new LinearLayoutCompat(this);
        linearLayoutHistoryCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutHistoryCard.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
        linearLayoutHistoryCard.setPadding(getDimensionsInDp(10), getDimensionsInDp(10), getDimensionsInDp(10), getDimensionsInDp(10));
        linearLayoutHistoryCard.setOrientation(LinearLayoutCompat.VERTICAL);

        TextView fullnameTextView = new TextView(this);
        fullnameTextView.setText(fullName);
        fullnameTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fullnameTextView.setTypeface(null, Typeface.BOLD);
        fullnameTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        fullnameTextView.setTextColor(ContextCompat.getColor(this, R.color.light));
        fullnameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        TextView usernameTextView = new TextView(this);
        usernameTextView.setText(username);
        usernameTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        usernameTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        usernameTextView.setTextColor(ContextCompat.getColor(this, R.color.light));
        usernameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        usernameTextView.setPadding(0, getDimensionsInDp(10), 0, getDimensionsInDp(16));

        Button removeButton = new Button(this);
        removeButton.setText("REMOVE SEARCH");
        removeButton.setTypeface(null, Typeface.BOLD);
        removeButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        removeButton.setTextColor(ContextCompat.getColor(this, R.color.light));
        removeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        removeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary));

        linearLayoutHistoryCard.addView(fullnameTextView);
        linearLayoutHistoryCard.addView(usernameTextView);
        linearLayoutHistoryCard.addView(removeButton);

        linearLayoutHistoryContainer.addView(linearLayoutHistoryCard);
        linearLayoutHistoryContainer.addView(renderSpacer(10));
    }

    private View renderSpacer(int value) {
        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionsInDp(value)));
        return view;
    }

    private int getDimensionsInDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public void navigateToSearchScreen(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void navigateToAboutScreen(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}