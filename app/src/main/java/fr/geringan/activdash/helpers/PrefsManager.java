package fr.geringan.activdash.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;

import fr.geringan.activdash.R;

public class PrefsManager {

    public static String baseAddress;
    public static String apiAdress;
    public static String apiDomain;
    public static String nodePort;
    public static String entryPointAddress;
    public static SharedPreferences sharedPreferences;

    public static void launch(Context context) {
        if (null != sharedPreferences) {
            return;
        }
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        baseAddress = sharedPreferences.getString(res.getString(R.string.key_base_url), "null");
        apiDomain = sharedPreferences.getString(res.getString(R.string.key_api_domain), "null");
        nodePort = sharedPreferences.getString(res.getString(R.string.key_node_port), "null");
        entryPointAddress = sharedPreferences.getString(res.getString(R.string.key_entrypoint_url), "null");
        apiAdress = baseAddress + "/" + apiDomain;
    }

    public static boolean areTherePrefs() {
        return !("null".equals(baseAddress) || "null".equals(apiDomain)
                || "null".equals(nodePort) || "null".equals(entryPointAddress)
        );
    }
}
