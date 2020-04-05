package fr.geringan.activdash.providers;

public class ModeImage {
    static final String MODE_NIGHT = "Nuit";
    static final String MODE_ECO = "Eco";
    static final String MODE_COMFORT = "Confort";
    static final String MODE_NO_FREEZE = "Hors gel";

    private int img;
    private int color;

    ModeImage(int img, int color) {
        this.img = img;
        this.color = color;
    }

    public int getImg() {
        return img;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
