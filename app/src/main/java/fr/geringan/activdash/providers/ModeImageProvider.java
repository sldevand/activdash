package fr.geringan.activdash.providers;

import fr.geringan.activdash.R;

public class ModeImageProvider {
    public static ModeImage getModeImage(String nom) {

        int modeImg;
        int color;

        switch (nom) {
            case ModeImage.MODE_NIGHT:
                modeImg = R.drawable.ic_mode_nuit;
                color = R.color.colorPrimaryDark;
                break;
            case ModeImage.MODE_COMFORT:
                modeImg = R.drawable.ic_mode_confort;
                color = R.color.amber;
                break;
            case ModeImage.MODE_ECO:
                modeImg = R.drawable.ic_mode_eco;
                color = R.color.colorOk;
                break;
            case ModeImage.MODE_NO_FREEZE:
                modeImg = R.drawable.ic_hors_gel;
                color = R.color.coldBlue;
                break;
            default:
                modeImg = android.R.drawable.ic_menu_close_clear_cancel;
                color = R.color.almostBlack;
                break;
        }
        return new ModeImage(modeImg, color);
    }

}
