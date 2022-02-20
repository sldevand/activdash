package fr.geringan.activdash.menu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import fr.geringan.activdash.R;
import fr.geringan.activdash.activities.SettingsActivity;
import fr.geringan.activdash.dialogs.AboutDialog;

public class OptionsItems {

    protected FragmentManager fragmentManager;
    protected Context context;

    public OptionsItems(FragmentManager fragmentManager, Context context) {
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) throws PackageManager.NameNotFoundException {
        if (R.id.action_settings == item.getItemId()) {
            Intent settingsIntent = new Intent(context, SettingsActivity.class);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);
            return true;
        }

        if (R.id.action_about == item.getItemId()) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                AboutDialog dialog = AboutDialog.newInstance(packageInfo.versionName);
                dialog.show(this.fragmentManager, "About");
            } catch (PackageManager.NameNotFoundException e) {
                throw new PackageManager.NameNotFoundException(context.getString(R.string.no_version_found));
            }
            return true;
        }

        return false;
    }
}
