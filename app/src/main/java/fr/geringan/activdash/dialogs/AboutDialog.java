package fr.geringan.activdash.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import fr.geringan.activdash.R;

public class AboutDialog extends DialogFragment {

    public static AboutDialog newInstance(String version) {
        AboutDialog fragment = new AboutDialog();
        Bundle args = new Bundle();
        args.putString("version", version);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle(getString(R.string.app_name));
        builderSingle.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());

        Bundle args = getArguments();
        if (null == args) {
            return builderSingle.create();
        }

        String version = args.getString("version");
        builderSingle.setMessage("Version = " + version);

        return builderSingle.create();
    }


}
