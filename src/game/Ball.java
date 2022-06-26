package game;

public class Ball {


    public static Richtung richtung;

    public static enum Richtung {oben, unten, rechtsOben, }
    public static int geschwindigkeit;
    public static int richtungsVektorX;
    public static int richtungsVektorY;
    public static int stuezVektorX;
    public static int stuezVektorY;

    Ball(){
        stuezVektorX = 46;
        stuezVektorY = 20;
        richtungsVektorX = 0;
        richtungsVektorY = 0;
        richtung = Richtung.unten;
    }
}
