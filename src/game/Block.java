package game;


import com.googlecode.lanterna.TextColor;

public class Block {
    int x;
    int y;
    public Pixel[][] spielfeld;
    //typ 3 soll unzerstörbar sein
    enum Typ{typ1, typ2, typ3}

    Typ typ;
    boolean visibility = true;
    TextColor blockColorTyp1 = TextColor.Indexed.fromRGB(255,185,15);
    TextColor blockColorTyp2 = TextColor.Indexed.fromRGB(0,0,205);
    TextColor blockColorTyp3 = TextColor.Indexed.fromRGB(105,105,105);
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
        visibility();
        if(visibility){
            for(int i = 0; i < 11; i++){
                switch (typ) {
                    case typ1 -> spielfeld[x + i][y].backColor = blockColorTyp1;
                    case typ2 -> spielfeld[x + i][y].backColor = blockColorTyp2;
                    case typ3 -> spielfeld[x + i][y].backColor = blockColorTyp3;
                }
            }
        }

    }
    public void visibility(){
        if(hits == 0){
            visibility = false;
        }
    }
}
