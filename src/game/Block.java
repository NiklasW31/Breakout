package game;


import com.googlecode.lanterna.TextColor;

public class Block {
    int x;
    int y;
    Pixel[][] spielfeld;
    enum Typ{typ1, typ2, typ3}
    Typ typ;
    boolean visibility;


    Block(int x, int y, Pixel[][] spielfeld, Typ typ){
        this.x = x;
        this.y = y;
        this.spielfeld = spielfeld;
        this.typ = typ;
    }

    public void drawBlock(){
        for(int i = 0; i < 11; i++){
            spielfeld[x + i][y].backColor = TextColor.Indexed.fromRGB(44, 244, 22);
        }
    }
    public void visibility(){

    }
}
