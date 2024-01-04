package com.example.userfinderandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        if (retrievedList != null && !retrievedList.isEmpty()) {
            emptyHistoryContainer.setVisibility(View.GONE);
            for (UserSearch user : retrievedList) {
                String fullName = user.getFullName();
                String username = user.getUsername();

                renderHistoryCard(fullName, username);
            }
        } else {
            System.out.println("STORAGE VAZIO");
            emptyHistoryContainer.setVisibility(View.VISIBLE);
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
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveConfirmationDialog(fullName, username);
            }
        });

        linearLayoutHistoryCard.addView(fullnameTextView);
        linearLayoutHistoryCard.addView(usernameTextView);
        linearLayoutHistoryCard.addView(removeButton);

        linearLayoutHistoryContainer.addView(linearLayoutHistoryCard);
        linearLayoutHistoryContainer.addView(renderSpacer(10));
    }

    private void showRemoveConfirmationDialog(final String fullName, final String username) {
        // Implemente um diálogo de confirmação (AlertDialog) aqui
        // Exiba uma mensagem perguntando se o usuário realmente deseja remover a pesquisa
        // Se confirmado, chame o método para remover e atualizar a interface do usuário
        new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Deseja remover esta pesquisa?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Remover a pesquisa da lista e atualizar a interface do usuário
                        removeFromHistoryList(fullName, username);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void removeFromHistoryList(String fullName, String username) {
        // Recuperar a lista atual do SharedPreferences
        List<UserSearch> userList = MyStorageManager.getObjectList(getApplicationContext());

        // Remover o item correspondente da lista
        UserSearch userToRemove = null;
        for (UserSearch user : userList) {
            if (user.getFullName().equals(fullName) && user.getUsername().equals(username)) {
                userToRemove = user;
                break;
            }
        }

        if (userToRemove != null) {
            userList.remove(userToRemove);

            // Salvar a lista atualizada no SharedPreferences
            MyStorageManager.saveObjectList(getApplicationContext(), userList);

            // Atualizar a interface do usuário
            recreate(); // Isso reinicia a atividade para refletir as alterações
        }
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