package fr.geringan.activdash.providers;

import fr.geringan.activdash.R;

public class BoilerStateProvider {
    public static BoilerState getBoilerState(int etat) {
        String etatStr;
        int color;

        switch (etat) {
            case BoilerState.BOILER_STATE_OFF:
                color = R.color.colorAccent;
                etatStr = BoilerState.BOILER_OFF;
                break;
            case BoilerState.BOILER_STATE_ON:
                color = R.color.colorOk;
                etatStr = BoilerState.BOILER_ON;
                break;
            default:
                color = R.color.almostBlack;
                etatStr = BoilerState.BOILER_UNDEFINED;
                break;
        }
        return new BoilerState(etatStr, color);
    }
}