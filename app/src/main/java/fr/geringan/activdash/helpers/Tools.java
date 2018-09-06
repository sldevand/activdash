package fr.geringan.activdash.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Tools {

    public static void shortSnackbar(View v,String message){
        snackbar(v,message,Snackbar.LENGTH_SHORT);
    }
    public static void shortSnackbar(View v,int message){
        snackbar(v,message,Snackbar.LENGTH_SHORT);
    }

    public static void longSnackbar(View v,String message){
        snackbar(v,message,Snackbar.LENGTH_LONG);
    }
    public static void longSnackbar(View v,int message){
        snackbar(v,message,Snackbar.LENGTH_LONG);
    }


    private static void snackbar(View v,String message,int length){
        Snackbar.make(v,message,length).show();
    }

    private static void snackbar(View v,int message,int length){
        Snackbar.make(v,message,length).show();
    }


    public static void hideKeyboard(Activity a){
        View view = a.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void popupDialog(Context ctx,String message){

        Builder alert = new Builder(ctx);
        alert.setMessage(message);
        alert.show();
    }

}
