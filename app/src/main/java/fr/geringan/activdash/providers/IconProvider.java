package fr.geringan.activdash.providers;

import fr.geringan.activdash.R;

public class IconProvider {
    public static Integer getIconFromName(String text) {
        switch (text.toLowerCase()) {
            case "tv":
                return R.mipmap.ic_tv;
            case "film":
                return R.mipmap.ic_movie;
            case "coucher":
                return R.mipmap.ic_bed;
            case "off":
                return R.mipmap.ic_power_off;
            default:
                return R.mipmap.ic_play;
        }
    }
}
