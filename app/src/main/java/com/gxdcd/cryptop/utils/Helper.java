package com.gxdcd.cryptop.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;

import com.gxdcd.cryptop.R;

public class Helper {

    // если интернет отсутствует, работа приложения невозможна
    // желательно в дальнейшем добавить пользователю страницу
    // или элемент управления на данный случай
    public static boolean networkIsNotAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean networkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!networkAvailable) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            String message = activity.getResources().getString(R.string.NoNetwork);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getResources().getString(R.string.OK), null);
            builder.create().show();
            return true;
        }
        return false;
    }
}
