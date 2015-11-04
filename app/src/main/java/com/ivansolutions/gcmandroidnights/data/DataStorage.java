package com.ivansolutions.gcmandroidnights.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DataStorage {
    public static String getStringPreference(Context context, String key_pref) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key_pref, "");
    }

    public static boolean getBooleanPreference(Context context, String key_pref) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key_pref, false);
    }

    public static int getIntPreference(Context context, String key_pref) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key_pref, -1);
    }

    public static void savePreference(Context context, String clave, String valor) {

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferencias.edit();

        editor.putString(clave, valor);

        editor.apply();
    }

    public static void savePreference(Context context, String clave, boolean valor) {

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferencias.edit();

        editor.putBoolean(clave, valor);

        editor.apply();
    }

    public static void savePreference(Context context, String clave, int valor) {

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferencias.edit();

        editor.putInt(clave, valor);

        editor.apply();
    }

    public static void remove(Context context, String key){
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).commit();
    }

    public static boolean contains(Context context, String key){
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }
}