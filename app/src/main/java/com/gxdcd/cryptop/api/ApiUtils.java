package com.gxdcd.cryptop.api;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.io.InputStream;
import java.util.Scanner;

public class ApiUtils {

    public static class Watcher implements SharedPreferences.OnSharedPreferenceChangeListener {
        Action<String> ready;
        String key;

        public Watcher(String key, Action<String> ready) {
            this.key = key;
            this.ready = ready;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String changedPreferenceKey) {
            if (this.key.equals(changedPreferenceKey)) {
                String value = sharedPreferences.getString(this.key, "");
                if (value != null && !"".equals(value)) {
                    ready.execute(value);
                }
            }
        }
    }

    public static void registerWatcher(Context context, Watcher watcher){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.registerOnSharedPreferenceChangeListener(watcher);
    }

    public static String getPreferenceValue(Context context, String name, String defaultValue) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(name, defaultValue);
    }

    // Преобразуем потоковые данные от сервера в строку json
    public static String ConvertStreamToJson(InputStream stream) {
        // https://stackoverflow.com/questions/6829801/httpurlconnection-setconnecttimeout-has-no-effect
        // можно использовать что-то похожее с [StringBuilder answer] но код со сканнером короче и проще
        Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
