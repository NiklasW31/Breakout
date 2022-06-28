package game;

public class Player extends Breakout{
    static public int x;
    static public int y;
    static public int leben;
    static public int groese;
    static public int highscore;

    Player(){
        x = 45;
        y = 42;
        leben = 3;
        groese = 12;
        highscore = 1900;
    }
    public static int gety(){
        return y;
    }
    public static int getX(){
        return x;
    }

    public static int getGroese() {
        return groese;
    }

    public static int getLeben() {
        return leben;
    }
    public static void setx(int x){
        Player.x = x;
    }

    public static void setGroese(int groese) {
        Player.groese = groese;
    }

    public static void setLeben(int leben) {
        Player.leben = leben;
    }
}
