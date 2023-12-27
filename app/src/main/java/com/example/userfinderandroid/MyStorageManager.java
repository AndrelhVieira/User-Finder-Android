package com.example.userfinderandroid;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.userfinderandroid.UserSearch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyStorageManager {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_OBJECT_LIST = "objectList";

    // Método para armazenar a lista de objetos
    public static void saveObjectList(Context context, List<UserSearch> objectList) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(objectList);
        editor.putString(KEY_OBJECT_LIST, json);
        editor.apply();
    }

    // Método para recuperar a lista de objetos
    public static List<UserSearch> getObjectList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_OBJECT_LIST, null);
        Type type = new TypeToken<ArrayList<UserSearch>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}
