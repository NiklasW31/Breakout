package game;


import com.googlecode.lanterna.TextColor;

public class Block {
    int x;
    int y;
    Pixel[][] spielfeld;
    enum Typ{typ1, typ2, typ3}

    Typ typ;
    boolean visibility;
    int hits;


    Block(int x, int y, Pixel[][] spielfeld, Typ typ){
        this.x = x;
        this.y = y;
        this.spielfeld = spielfeld;
        this.typ = typ;

        switch (typ){
            case typ1:
                hits = 1;
                break;
            case typ2:
                hits = 2;
                break;
            case typ3:
                hits = -1;
                break;

        }
    }

    public void drawBlock(){
        if(visibility = true){
            for(int i = 0; i < 11; i++){
                spielfeld[x + i][y].backColor = TextColor.Indexed.fromRGB(44, 244, 22);
            }
        }

    }
    public void visibility(){
        if(hits == 0){
            visibility = false;
        }
    }
}
