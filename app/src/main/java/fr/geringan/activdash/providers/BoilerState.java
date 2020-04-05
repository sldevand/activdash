package fr.geringan.activdash.providers;

public class BoilerState {
    static final String BOILER_ON = "On";
    static final String BOILER_OFF = "Off";
    static final String BOILER_UNDEFINED = "?";
    static final int BOILER_STATE_ON = 1;
    static final int BOILER_STATE_OFF = 0;
    private String etat;
    private int color;

    BoilerState(String etat, int color) {
        this.etat = etat;
        this.color = color;
    }

    public String getEtat() {
        return etat;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}