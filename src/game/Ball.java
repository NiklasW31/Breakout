package game;


public class Ball {


    public static Richtung richtung;

    public static enum Richtung {oben, unten,}

    public static int geschwindigkeit;
    public static double richtungsVektorX;
    public static double richtungsVektorY;
    public static double stuezVektorX;
    public static double stuezVektorY;

    Ball(){
        stuezVektorX = 50;
        stuezVektorY = 20;
        richtungsVektorX = 0;
        richtungsVektorY = 0;
        richtung = Richtung.unten;
    }
}
