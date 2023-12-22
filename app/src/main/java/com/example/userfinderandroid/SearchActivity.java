package com.example.userfinderandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {
    private TextView resultOfSearch;
    private EditText usernameToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        usernameToSearch = findViewById(R.id.username_to_search);
        resultOfSearch = findViewById(R.id.result_of_search);
    }

    public void onSearchButtonClick(View view) {
        String username = usernameToSearch.getText().toString().trim();

        if (!username.isEmpty()) {
            String apiUrl = "https://api.github.com/users/" + username;

            new GithubApiTask().execute(apiUrl);
        } else {
            resultOfSearch.setText("Please enter a GitHub username.");
        }
    }

    class GithubApiTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String result = "";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    bufferedReader.close();
                    result = stringBuilder.toString();


                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);

                String login = jsonResult.getString("login");
                String name = jsonResult.getString("name");
                String bio = jsonResult.getString("bio");

                String displayText = "Username: " + login + " " + name + " " + bio;
                resultOfSearch.setText(displayText);
            } catch (JSONException e) {
                e.printStackTrace();
                resultOfSearch.setText("Error parsing GitHub API response.");
            }
        }
    }

    public void navigateToHistoryScreen(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void navigateToAboutScreen(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}