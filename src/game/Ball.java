package game;

public class Ball {

    public enum Richtung {oben, unten}
    public static int geschwindigkeit;
    public static int richtungsVektorX;
    public static int richtungsVektorY;
    public static int stuezVektorX;
    public static int stuezVektorY;

    Ball(){
        stuezVektorX = 40;
        stuezVektorY = 20;
        richtungsVektorX = 0;
        richtungsVektorY = -1;
    }
}
