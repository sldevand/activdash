package fr.geringan.activdash.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

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
        builderSingle.setIcon(android.R.drawable.ic_dialog_info);
        builderSingle.setTitle(getString(R.string.about_app));
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
