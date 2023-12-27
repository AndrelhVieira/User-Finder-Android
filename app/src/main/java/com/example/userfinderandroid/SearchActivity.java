package com.example.userfinderandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {
    private TextView resultOfSearch;
    private EditText usernameToSearch;
    private ScrollView resultScrollView;

    private LinearLayoutCompat linearLayoutCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        usernameToSearch = findViewById(R.id.username_to_search);
        resultOfSearch = findViewById(R.id.result_of_search);
        resultScrollView = findViewById(R.id.result_scrollview);

        linearLayoutCompat = new LinearLayoutCompat(this);
        linearLayoutCompat.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutCompat.setElevation(8);
        linearLayoutCompat.setPadding(getDimensionsInDp(20), getDimensionsInDp(8), getDimensionsInDp(20), getDimensionsInDp(8));
        linearLayoutCompat.setBackgroundResource(R.drawable.border);
        linearLayoutCompat.setGravity(Gravity.CENTER);
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
    }

    public void onSearchButtonClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

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
                        stringBuilder.append(line).append("\n");
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

                String name = jsonResult.getString("name");
                String login = jsonResult.getString("login");
                String followers = jsonResult.getString("followers");
                String following = jsonResult.getString("following");
                String public_repos = jsonResult.getString("public_repos");

                String avatar_url = jsonResult.getString("avatar_url");

                Bitmap avatarImage = new LoadImageTask().execute(avatar_url).get();

                createUserCard(name, login, avatarImage, followers, following, public_repos);
                resultOfSearch.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
                resultOfSearch.setText("Error parsing GitHub API response.");
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class RepositoriesTask extends AsyncTask<String, Void, String> {
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
                        stringBuilder.append(line).append("\n");
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
                JSONArray jsonResult = new JSONArray(result);

                LinearLayoutCompat repos_cards = renderRepositoriesCard(jsonResult);
                linearLayoutCompat.addView(repos_cards);
            } catch (JSONException e) {
                e.printStackTrace();
                resultOfSearch.setText("Error parsing GitHub API response for repositories.");
            }
        }
    }

    class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private LinearLayoutCompat renderRepositoriesCard(JSONArray jsonArray) throws JSONException {
        LinearLayoutCompat linearLayoutReposCards = new LinearLayoutCompat(this);
        linearLayoutReposCards.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutReposCards.setPadding(0, getDimensionsInDp(16), 0, 0);
        linearLayoutReposCards.setOrientation(LinearLayoutCompat.VERTICAL);

        System.out.println("JSON ARRAY RESULT - " + jsonArray.length() + " - " + jsonArray);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String name = jsonObject.getString("name");
            String repo_url = jsonObject.getString("html_url");
            String language = jsonObject.getString("language");

            LinearLayoutCompat linearLayoutCompatRepo = new LinearLayoutCompat(this);
            linearLayoutCompatRepo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayoutCompatRepo.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            linearLayoutCompatRepo.setGravity(Gravity.CENTER);
            linearLayoutCompatRepo.setOrientation(LinearLayoutCompat.VERTICAL);
            linearLayoutCompatRepo.setPadding(getDimensionsInDp(10), getDimensionsInDp(20), getDimensionsInDp(10), getDimensionsInDp(20));


            TextView repo_name = new TextView(this);
            repo_name.setText(name);
            repo_name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            repo_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            repo_name.setTypeface(null, Typeface.BOLD);
            repo_name.setTextColor(ContextCompat.getColor(this, R.color.light));

            TextView repo_tech = new TextView(this);
            repo_tech.setText(language != "null" ? language : "Not Informated");
            repo_tech.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            repo_tech.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            repo_tech.setTextColor(ContextCompat.getColor(this, R.color.light));
            repo_tech.setPadding(0, getDimensionsInDp(10), 0, getDimensionsInDp(10));

            Button repo_button = new Button(this);
            repo_button.setText("LINK TO REPOSITORY");
            repo_button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            repo_button.setBackgroundResource(R.drawable.background);
            repo_button.setPadding(getDimensionsInDp(16), getDimensionsInDp(16), getDimensionsInDp(16), getDimensionsInDp(16));
            repo_button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            repo_button.setTypeface(null, Typeface.BOLD);

            // ADICIONAR LINK DO REPO NO CLIQUE DO BOTÃO


            linearLayoutCompatRepo.addView(repo_name);
            linearLayoutCompatRepo.addView(repo_tech);
            linearLayoutCompatRepo.addView(repo_button);
            linearLayoutReposCards.addView(linearLayoutCompatRepo);
            linearLayoutReposCards.addView(renderSpacer(10));
        }

        return linearLayoutReposCards;
    }

    private View renderSpacer(int value) {
        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionsInDp(value)));
        return view;
    }

    public void createUserCard(String name, String login, Bitmap avatar_url, String followers, String following, String public_repos) {
        TextView fullname = new TextView(this);
        fullname.setText(name);
        fullname.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fullname.setTextColor(ContextCompat.getColor(this, R.color.light));
        fullname.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        fullname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        fullname.setTypeface(null, Typeface.BOLD);
        fullname.setPadding(0, 0, 0, getDimensionsInDp(16));

        TextView username = new TextView(this);
        username.setText(login);
        username.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        username.setPadding(0, 0, 0, getDimensionsInDp(16));
        username.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        username.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        username.setTypeface(null, Typeface.BOLD);
        username.setTextColor(ContextCompat.getColor(this, R.color.light));

        LinearLayoutCompat linearLayoutProfilePic = new LinearLayoutCompat(this);
        linearLayoutProfilePic.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutProfilePic.setGravity(Gravity.CENTER);
        linearLayoutProfilePic.setOrientation(LinearLayoutCompat.HORIZONTAL);

        ShapeableImageView profilePic = new ShapeableImageView(this);
        ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, getDimensionsInDp(75)).build();
        profilePic.setShapeAppearanceModel(shapeAppearanceModel);
        profilePic.setImageBitmap(avatar_url);
        profilePic.setLayoutParams(new ViewGroup.LayoutParams(getDimensionsInDp(150), getDimensionsInDp(150)));

        linearLayoutProfilePic.addView(profilePic);

        LinearLayoutCompat linearLayoutCompatInfos = new LinearLayoutCompat(this);
        linearLayoutCompatInfos.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutCompatInfos.setPadding(0, getDimensionsInDp(16), 0, 0);

        LinearLayoutCompat linearLayoutCompatFollowers = new LinearLayoutCompat(this);
        linearLayoutCompatFollowers.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutCompatFollowers.setGravity(Gravity.CENTER);
        linearLayoutCompatFollowers.setOrientation(LinearLayoutCompat.VERTICAL);

        TextView followers_text_title = new TextView(this);
        followers_text_title.setText("Followers");
        followers_text_title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        followers_text_title.setPadding(0, 0, 0, getDimensionsInDp(8));
        followers_text_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        followers_text_title.setTextColor(ContextCompat.getColor(this, R.color.light));
        followers_text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        TextView followers_text_content = new TextView(this);
        followers_text_content.setText(followers);
        followers_text_content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        followers_text_content.setTypeface(null, Typeface.BOLD);
        followers_text_content.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        followers_text_content.setTextColor(ContextCompat.getColor(this, R.color.light));
        followers_text_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        LinearLayoutCompat linearLayoutCompatFollowing = new LinearLayoutCompat(this);
        linearLayoutCompatFollowing.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutCompatFollowing.setGravity(Gravity.CENTER);
        linearLayoutCompatFollowing.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompatFollowing.setPadding(getDimensionsInDp(32), 0, getDimensionsInDp(32), 0);

        TextView following_text_title = new TextView(this);
        following_text_title.setText("Following");
        following_text_title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        following_text_title.setPadding(0, 0, 0, getDimensionsInDp(8));
        following_text_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        following_text_title.setTextColor(ContextCompat.getColor(this, R.color.light));
        following_text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        TextView following_text_content = new TextView(this);
        following_text_content.setText(following);
        following_text_content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        following_text_content.setTypeface(null, Typeface.BOLD);
        following_text_content.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        following_text_content.setTextColor(ContextCompat.getColor(this, R.color.light));
        following_text_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        LinearLayoutCompat linearLayoutCompatPublicRepos = new LinearLayoutCompat(this);
        linearLayoutCompatPublicRepos.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutCompatPublicRepos.setGravity(Gravity.CENTER);
        linearLayoutCompatPublicRepos.setOrientation(LinearLayoutCompat.VERTICAL);

        TextView public_repos_text_title = new TextView(this);
        public_repos_text_title.setText("Public Repos");
        public_repos_text_title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        public_repos_text_title.setPadding(0, 0, 0, getDimensionsInDp(8));
        public_repos_text_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        public_repos_text_title.setTextColor(ContextCompat.getColor(this, R.color.light));
        public_repos_text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        TextView public_repos_text_content = new TextView(this);
        public_repos_text_content.setText(public_repos);
        public_repos_text_content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        public_repos_text_content.setTypeface(null, Typeface.BOLD);
        public_repos_text_content.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        public_repos_text_content.setTextColor(ContextCompat.getColor(this, R.color.light));
        public_repos_text_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        linearLayoutCompatFollowers.addView(followers_text_title);
        linearLayoutCompatFollowers.addView(followers_text_content);

        linearLayoutCompatFollowing.addView(following_text_title);
        linearLayoutCompatFollowing.addView(following_text_content);

        linearLayoutCompatPublicRepos.addView(public_repos_text_title);
        linearLayoutCompatPublicRepos.addView(public_repos_text_content);

        linearLayoutCompatInfos.addView(linearLayoutCompatFollowers);
        linearLayoutCompatInfos.addView(linearLayoutCompatFollowing);
        linearLayoutCompatInfos.addView(linearLayoutCompatPublicRepos);

        TextView repositories_title = new TextView(this);
        repositories_title.setText("Repositories");
        repositories_title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        repositories_title.setTypeface(null, Typeface.BOLD);
        repositories_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        repositories_title.setTextColor(ContextCompat.getColor(this, R.color.light));
        repositories_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        repositories_title.setPadding(0, getDimensionsInDp(32), 0, getDimensionsInDp(16));

        linearLayoutCompat.addView(fullname);
        linearLayoutCompat.addView(username);
        linearLayoutCompat.addView(linearLayoutProfilePic);
        linearLayoutCompat.addView(linearLayoutCompatInfos);

        resultScrollView.addView(linearLayoutCompat);

        renderRepositories();
    }

    private void renderRepositories() {
        String username = usernameToSearch.getText().toString().trim();
        String apiUrl = "https://api.github.com/users/" + username + "/repos";

        new RepositoriesTask().execute(apiUrl);
    }

    private int getDimensionsInDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
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