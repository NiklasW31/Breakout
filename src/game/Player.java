package game;

public class Player extends Breakout{
    static public int x;
    static public int y;
    static public int leben;
    static public int groese;

    Player(){
        x = 42;
        y = 42;
        leben = 3;
        groese = 11;
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
