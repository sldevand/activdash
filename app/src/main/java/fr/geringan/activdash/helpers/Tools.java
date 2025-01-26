package fr.geringan.activdash.helpers;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Tools {

    public static void shortSnackbar(View v, String message) {
        snackbar(v, message, Snackbar.LENGTH_SHORT);
    }

    public static void shortSnackbar(View v, int message) {
        snackbar(v, message, Snackbar.LENGTH_SHORT);
    }

    public static void longSnackbar(View v, String message) {
        snackbar(v, message, Snackbar.LENGTH_LONG);
    }

    public static void longSnackbar(View v, int message) {
        snackbar(v, message, Snackbar.LENGTH_LONG);
    }

    private static void snackbar(View v, String message, int length) {
        Snackbar.make(v, message, length).show();
    }

    private static void snackbar(View v, int message, int length) {
        Snackbar.make(v, message, length).show();
    }
}
