package game;

import java.io.*;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.Indexed;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;

import static game.Player.*;

public class Breakout {

	public static int spielfeldHoehe = 50;
	public static int spielfeldBreite = 100;
	public static Pixel[][] spielfeld;
	public static TextColor DefaultBackColor = TextColor.ANSI.BLACK;
	public static TextColor DefaultTextColor = TextColor.ANSI.WHITE;
	public static boolean gamesOn = true;
	public static KeyType selectlevel;
	public static Block[] blocks;
	public static boolean levelStart = true;
	public static HashMap<Integer, String> blockHM = new HashMap<Integer, String>();
	public static int localHighscore;
	public static String localName;
	public static int level;
	public static String[] strArray = new String[3];

	public static String split[];
	public static String username = "";




	public static void main(String[] args) throws IOException {

		getHighscore();

		// Spielfeld mit "Pixeln" wird angelegt
		// jedes Pixel kann einen char, eine Hintergrundfarbe und eine Textfarbe haben
		spielfeld = new Pixel[spielfeldBreite][spielfeldHoehe];
		for (int i = 0; i < spielfeld.length; i++) {
			for (int j = 0; j < spielfeld[i].length; j++) {
				spielfeld[i][j] = new Pixel();
			}
		}

		// Fenster (Terminal) erstellen und anzeigen
		TerminalFactory factory = new DefaultTerminalFactory()
				.setInitialTerminalSize(new TerminalSize(spielfeldBreite, spielfeldHoehe));
		Terminal terminal = factory.createTerminal();
		terminal.setCursorVisible(false);




		// "Application-loop" - kehrt immer wieder zum Startbildschirm zur??ck
		// wird beim Startbildschirm ESCAPE gedr??ckt wird die Anwendung beendet

		while (true) {
			// zeigt simple Startseite an, die mit "Enter" oder "Escape" verlassen wird
			showStartseite(terminal);

			// "Game-loop" wird hier ausgef??hrt
			double geschwindigkeit = 30;
			runGame(geschwindigkeit, terminal);
			
			// GAME OVER hier hinzuf??gen

		}


	}

	private static void runGame(double geschwindigkeit, Terminal terminal) throws IOException {

		// initiale Spieleinstellungen
		//Richtung richtung = Richtung.Rechts;
		int posX = spielfeldBreite / 2;
		int posY = spielfeldHoehe / 2;

		// Startfarbe
		int r = 3, g = 33, b = 66;

		Player player = new Player();
		Ball ball = new Ball();
		createLevel();


		// Spiel in "Dauerschleife" (game loop)

		inner:
		while (gamesOn) {

			ClearSpielfeld();

			draw();
			moveBall();
			collisionPlayer();
			collisionBoarder();
			collisionBlock();
			gameOver();
			levelCleared();

			//spielfeld[42][41].backColor = Indexed.fromRGB(144, 44, 22);

			// Hintergrundfarbe mit RGB (ACHTUNG 6x6x6 Color Cube)
			// siehe TextColor Klasse in Lanterna

			// obere Zeile einf??rben
			for (int i = 0; i < spielfeldBreite; i++) {
				spielfeld[i][0].backColor = Indexed.fromRGB(r, g, b);
			}

			// untere Zeile einf??rben
			for (int i = 0; i < spielfeldBreite; i++) {
				spielfeld[i][spielfeldHoehe - 1].backColor = Indexed.fromRGB(r, g, b);
			}

			// linke Spalte einf??rben
			for (int i = 0; i < spielfeldHoehe; i++) {
				spielfeld[0][i].backColor = Indexed.fromRGB(r, g, b);
				spielfeld[1][i].backColor = Indexed.fromRGB(r, g, b);
			}

			// rechte Spalte einf??rben
			for (int i = 0; i < spielfeldHoehe; i++) {
				spielfeld[spielfeldBreite - 2][i].backColor = Indexed.fromRGB(r, g, b);
				spielfeld[spielfeldBreite - 1][i].backColor = Indexed.fromRGB(r, g, b);
			}

			// Farbe ver??ndern 
			// (Modulo wird verwendet, weil die Zahlen nur zwischen 0-255 liegen d??rfen
			r += 3;
			g += 3;
			b += 3;
			r %= 256;
			g %= 256;
			b %= 256;

			// Tastatureinggabe wird gelesen
			//KeyStroke eingabe = terminal.readInput(); 	// stopped und wartet auf Eingabe
			KeyStroke eingabe = terminal.pollInput(); 		// l??uft weiter, auch wenn keine Eingabe erfolgt ist 
			if (eingabe != null) {

				// wenn die linke Pfeiltaste gedr??ckt wird
				if (eingabe.getKeyType().equals(KeyType.ArrowLeft)) {
					// kann nicht in entgegen gesetzte Richtung laufen (z.B. Snake)

						
						// wenn der Spielfeld verlassen wird, dann ...
						if (Player.x > 2) {
							Player.x -= 2;
						}


				}

				// wenn die rechte Pfeiltaste gedr??ckt wird
				if (eingabe.getKeyType().equals(KeyType.ArrowRight)) {


						if (Player.x + Player.groese < spielfeldBreite - 2) {
							Player.x += 2;
						}

				}


				// wenn ESC Taste gedr??ckt wird
				if (eingabe.getKeyType().equals(KeyType.Escape)) {
					// Spielfeld leerr??umen f??r den GAME OVER / Startbildschirm
					ClearSpielfeld();
					WriteSpielfeld(terminal);
					break;
				}
			}


			try {
				// zeichnet das gesamte Spielfeld auf einmal
				WriteSpielfeld(terminal);
				// kurzer "Schlaf", es kann hier mit der Verzoegerung die
				// Spielgeschwindigkeit eingestellt werden
				Thread.sleep((int) geschwindigkeit);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void showStartseite(Terminal terminal) throws IOException {

		terminal.clearScreen();

		// Startseite mit Text
		// der Text wird hier direkt in das Terminal geschrieben und ncht in das Spielfeld
		terminal.setCursorPosition(6, 6);
		Write(" ______     ______     ______     ______     __  __     ______     __  __     ______  ", terminal);
		terminal.setCursorPosition(6, 7);
		Write("/\\  == \\   /\\  == \\   /\\  ___\\   /\\  __ \\   /\\ \\/ /    /\\  __ \\   /\\ \\/\\ \\   /\\__  _\\ ", terminal);
		terminal.setCursorPosition(6, 8);
		Write("\\ \\  __<   \\ \\  __<   \\ \\  __\\   \\ \\  __ \\  \\ \\  _\"-.  \\ \\ \\/\\ \\  \\ \\ \\_\\ \\  \\/_/\\ \\/ ", terminal);
		terminal.setCursorPosition(6, 9);
		Write(" \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_____\\    \\ \\_\\ ", terminal);
		terminal.setCursorPosition(6, 10);
		Write("  \\/_____/   \\/_/ /_/   \\/_____/   \\/_/\\/_/   \\/_/\\/_/   \\/_____/   \\/_____/     \\/_/ ", terminal);
		terminal.setCursorPosition(6, 11);
		Write("                                                                                      ", terminal);

		// Cursor auf Position bewegen
		terminal.setCursorPosition(6, 14);
		Write("Willkommen im Spiel", terminal); // Text schreiben
		terminal.setCursorPosition(6, 15);
		Write("Dr??cke F1, um das erste Level zu Spielen.", terminal);
		terminal.setCursorPosition(6, 16);
		Write("Dr??cke F2, um das zweite Level zu Spielen.", terminal);
		terminal.setCursorPosition(6, 17);
		Write("Dr??cke F3, um das dritte Level zu Spielen.", terminal);
		terminal.setCursorPosition(6, 18);
		Write("Dr??cke ESCAPE, um das Spiel zu verlassen.", terminal);
		terminal.setCursorPosition(6,25 );
		Write("Highscore Level 1: " + strArray[0], terminal);
		terminal.setCursorPosition(6,26 );
		Write("Highscore Level 2: "+strArray[1], terminal);
		terminal.setCursorPosition(6,27 );
		Write("Highscore Level 3: "+ strArray[2], terminal);

		// Texte im Terminal anzeigen
		terminal.flush();

		if (gamesOn == false){
			showGameOver(terminal);
		}

		// Eingabe abwarten
		while (true) {

			// Tastatureinggabe wird gelesen
			KeyStroke eingabe = terminal.readInput();
			if (eingabe != null) {

				 //System.out.println(eingabe); // zur Kontrolle kann eingebene
				// Taste angezeigt werden


				// wenn die Taste ENTER gedruckt wird
				//if (eingabe.getKeyType().equals(KeyType.Enter)) {

					// Startbildschirm wird beendet / while Schleife wird 
					// unterbrochen (GAME-Loop wird danach gestartet - siehe main-Methode 
				//	break;
				//}
				// wenn die Taste ESC gedr??ckt wird, beendet sich das Programm
				if (eingabe.getKeyType().equals(KeyType.Escape)) {
					System.exit(0);
				}

				if(eingabe.getKeyType().equals(KeyType.F1)) {
					selectlevel = KeyType.F1;
					level = 1;
					leben = 3;
					localHighscore = 0;
					gamesOn = true;
					getlevelhighscore();
					 break ;
				}
				if(eingabe.getKeyType().equals(KeyType.F2)){
					selectlevel = KeyType.F2;
					level = 2;
					leben = 3;
					localHighscore = 0;
					gamesOn = true;
					getlevelhighscore();
					break;
				}
				if(eingabe.getKeyType().equals(KeyType.F3)){
					selectlevel = KeyType.F3;
					level = 3;
					leben = 3;
					localHighscore = 0;
					gamesOn = true;
					getlevelhighscore();
					break;
				}
			}
		}
	}

	// Diese Methode hilft einen String zu "Zeichnen"
	private static void Write(String print, Terminal terminal) throws IOException {
		char[] printToChar = print.toCharArray();
		for (int i = 0; i < print.length(); i++) {
			terminal.putCharacter(printToChar[i]);
		}
	}

	// Diese Methode zeichnet das gesamte Spielfeld auf einmal
	private static void WriteSpielfeld(Terminal terminal) throws IOException {

		for (int i = 0; i < spielfeld.length; i++) {
			for (int j = 0; j < spielfeld[i].length; j++) {
				terminal.setCursorPosition(i, j);
				terminal.setForegroundColor(spielfeld[i][j].textColor);
				terminal.setBackgroundColor(spielfeld[i][j].backColor);
				terminal.putCharacter(spielfeld[i][j].Text);
				terminal.setBackgroundColor(spielfeld[50][2].backColor = Indexed.fromRGB(1,1,1));
				terminal.setCursorPosition(50, 2);
				Write(Integer.toString(Player.highscore), terminal);
					terminal.setCursorPosition(75, 2);
					Write(Player.leben +  "\u2665", terminal);
			}
		}

		terminal.flush();
	}

	// l??scht den Inhalt vom Spielfeld
	private static void ClearSpielfeld() {

		for (int i = 0; i < spielfeld.length; i++) {
			for (int j = 0; j < spielfeld[i].length; j++) {
				spielfeld[i][j].textColor = DefaultTextColor;
				spielfeld[i][j].backColor = DefaultBackColor;
				spielfeld[i][j].Text = ' ';
			}
		}
	}

	public static void draw(){

		//zeichnet Spieler
		for(int i = 0; i < Player.groese; i++){
			spielfeld[Player.x + i][Player.y].backColor = Indexed.fromRGB(144, 44, 22);
		}

		//Zeichet Ball
		spielfeld[(int)Ball.stuezVektorX][(int)Ball.stuezVektorY].Text = '\u058E';

		//zeichne Bl??cke
		for(Block b: blocks){
			b.drawBlock();
		}



	}
	public static void moveBall(){

		if(Ball.richtungsVektorX == 3){
			Ball.richtungsVektorX = 2;
		}
		if(Ball.richtungsVektorX == -3){
			Ball.richtungsVektorX = -2;
		}
		Ball.stuezVektorX += Ball.richtungsVektorX;
		Ball.stuezVektorY += Ball.richtungsVektorY;

	}

	public static void collisionPlayer(){

		//wenn der ball und der Spieler auf der Y Achse auf der selbenstufe sind
		if(Player.y -1  == Ball.stuezVektorY){

			//jeder wert vom Spieler wird mit dem Ball abgeglichen
			for(int i = 0; i < Player.groese; i++){

				//wenn ein wert ??bereinstimmt
				if(Player.x + i == Ball.stuezVektorX){

					//wenn ball auf die Linke seite vom Spieler trifft
					if(i <= 3){

						Ball.richtungsVektorX --;
						Ball.richtungsVektorY = Ball.richtungsVektorY * -1;

					}

					//wenn Ball in die Mitte vom spieler trifft
					if(i >= 4 && i <= 7 ){

						Ball.richtungsVektorY = Ball.richtungsVektorY * -1;
					}

					//wenn Ball auf die rechte Seite vom Spieler trifft
					if(i >= 8){

						Ball.richtungsVektorX ++;
						Ball.richtungsVektorY = Ball.richtungsVektorY * -1;
					}
				}
			}
		}
	}

	public static void collisionBoarder(){

		//obere Boarder
		if(Ball.stuezVektorY <= 1){

			Ball.richtungsVektorY = Ball.richtungsVektorY *-1;
		}

		if(Ball.stuezVektorX <= 1){
			richtungAendernSeite();
		}
		if(Ball.stuezVektorX >= spielfeldBreite - 2){
			richtungAendernSeite();
		}

		if(Ball.stuezVektorY == spielfeldHoehe - 1){
			Player.leben--;
			Player.x = 45;
			Ball.stuezVektorX = Player.x + Player.groese / 2 + 1;
			Ball.stuezVektorY = 20;
			Ball.richtungsVektorY = 1;
			Ball.richtungsVektorX = 0;
			draw();
			sleep2Sec();
		}
	}
	//Ball beruert einen Block
	public static void collisionBlock(){
		int levelBlock = blocks.length;
		int blocksLength = 11;
		//System.out.println(blocks.length);
		for(int i = 0;i < levelBlock;i++ ){
			for(int j = 0;j < blocksLength;j++) {
				if(Ball.stuezVektorY >= 0){
					if (blocks[i].x+j == Ball.stuezVektorX && blocks[i].y == Ball.stuezVektorY-1 && blocks[i].visibility) {
						System.out.println("hit");
						blocks[i].hits--;
						Player.highscore = Player.highscore+((i+1)*15);
						System.out.println(blocks[i].hits);
						Ball.richtungsVektorY = Ball.richtungsVektorY *-1;
				}else if(Ball.stuezVektorY <= 0){
						if (blocks[i].x+j == Ball.stuezVektorX && blocks[i].y == Ball.stuezVektorY+1 && blocks[i].visibility) {
							System.out.println("hit");
							blocks[i].hits--;
							Player.highscore = Player.highscore + ((i + 1) * 150);
							System.out.println(blocks[i].hits);
							Ball.richtungsVektorY = Ball.richtungsVektorY * -1;
						}
					}

				}
			}
		}
	}

	public static void gameOver(){
		if(Player.leben == 0){
			gamesOn = false;
			//System.exit(0);
		}
	}

	public static void levelCleared(){
		while(levelStart){
			for(int i = 0; i < blocks.length;i++){
				blockHM.put(i ,"Block"+1);
				if(blocks[i].typ.toString().equals("typ3")){
					blockHM.remove(i);
				}
			}
			levelStart = false;
		}
		for(int j = 0; j < blocks.length; j++){
			if (blockHM.containsKey(j) && blocks[j].visibility == false){
				blockHM.remove(j);
				System.out.println("block "+j+" wurde entfernt");
				System.out.println(blockHM.size() + "noch ??brig");
			}else if(blockHM.isEmpty()){
				System.out.println("level cleared");
				Player.leben = 0;
				gameOver();
			}
		}
	}


	public static void richtungAendernSeite(){
		Ball.richtungsVektorX = Ball.richtungsVektorX * -1;
	}


	private static void createLevel(){
		if(selectlevel.equals(KeyType.F1)){

			blocks = new Block[64];
			blocks[0] = new Block(2, 3, spielfeld, Block.Typ.typ1);
			blocks[1] = new Block(14, 3, spielfeld , Block.Typ.typ1);
			blocks[2] = new Block(26, 3, spielfeld, Block.Typ.typ1);
			blocks[3] = new Block(38, 3, spielfeld, Block.Typ.typ1);
			blocks[4] = new Block(50, 3, spielfeld, Block.Typ.typ1);
			blocks[5] = new Block(62, 3, spielfeld, Block.Typ.typ1);
			blocks[6] = new Block(74, 3, spielfeld, Block.Typ.typ1);
			blocks[7] = new Block(86, 3, spielfeld, Block.Typ.typ1);

			blocks[8] = new Block(2, 5, spielfeld, Block.Typ.typ2);
			blocks[9] = new Block(14, 5, spielfeld, Block.Typ.typ2);
			blocks[10] = new Block(26, 5, spielfeld, Block.Typ.typ2);
			blocks[11] = new Block(38, 5, spielfeld, Block.Typ.typ2);
			blocks[12] = new Block(50, 5, spielfeld, Block.Typ.typ2);
			blocks[13] = new Block(62, 5, spielfeld, Block.Typ.typ2);
			blocks[14] = new Block(74, 5, spielfeld, Block.Typ.typ2);
			blocks[15] = new Block(86, 5, spielfeld, Block.Typ.typ2);

			blocks[16] = new Block(2, 7, spielfeld, Block.Typ.typ1);
			blocks[17] = new Block(14, 7, spielfeld , Block.Typ.typ1);
			blocks[18] = new Block(26, 7, spielfeld, Block.Typ.typ3);
			blocks[19] = new Block(38, 7, spielfeld, Block.Typ.typ1);
			blocks[20] = new Block(50, 7, spielfeld, Block.Typ.typ1);
			blocks[21] = new Block(62, 7, spielfeld, Block.Typ.typ3);
			blocks[22] = new Block(74, 7, spielfeld, Block.Typ.typ1);
			blocks[23] = new Block(86, 7, spielfeld, Block.Typ.typ1);

			blocks[24] = new Block(2, 9, spielfeld, Block.Typ.typ1);
			blocks[25] = new Block(14, 9, spielfeld , Block.Typ.typ1);
			blocks[26] = new Block(26, 9, spielfeld, Block.Typ.typ3);
			blocks[27] = new Block(38, 9, spielfeld, Block.Typ.typ1);
			blocks[28] = new Block(50, 9, spielfeld, Block.Typ.typ1);
			blocks[29] = new Block(62, 9, spielfeld, Block.Typ.typ3);
			blocks[30] = new Block(74, 9, spielfeld, Block.Typ.typ1);
			blocks[31] = new Block(86, 9, spielfeld, Block.Typ.typ1);

			blocks[32] = new Block(2, 11, spielfeld, Block.Typ.typ1);
			blocks[33] = new Block(14, 11, spielfeld , Block.Typ.typ1);
			blocks[34] = new Block(26, 11, spielfeld, Block.Typ.typ3);
			blocks[35] = new Block(38, 11, spielfeld, Block.Typ.typ1);
			blocks[36] = new Block(50, 11, spielfeld, Block.Typ.typ1);
			blocks[37] = new Block(62, 11, spielfeld, Block.Typ.typ3);
			blocks[38] = new Block(74, 11, spielfeld, Block.Typ.typ1);
			blocks[39] = new Block(86, 11, spielfeld, Block.Typ.typ1);

			blocks[40] = new Block(2, 13, spielfeld, Block.Typ.typ1);
			blocks[41] = new Block(14, 13, spielfeld , Block.Typ.typ1);
			blocks[42] = new Block(26, 13, spielfeld, Block.Typ.typ3);
			blocks[43] = new Block(38, 13, spielfeld, Block.Typ.typ1);
			blocks[44] = new Block(50, 13, spielfeld, Block.Typ.typ1);
			blocks[45] = new Block(62, 13, spielfeld, Block.Typ.typ3);
			blocks[46] = new Block(74, 13, spielfeld, Block.Typ.typ1);
			blocks[47] = new Block(86, 13, spielfeld, Block.Typ.typ1);

			blocks[48] = new Block(2, 15, spielfeld, Block.Typ.typ1);
			blocks[49] = new Block(14, 15, spielfeld , Block.Typ.typ1);
			blocks[50] = new Block(26, 15, spielfeld, Block.Typ.typ3);
			blocks[51] = new Block(38, 15, spielfeld, Block.Typ.typ1);
			blocks[52] = new Block(50, 15, spielfeld, Block.Typ.typ1);
			blocks[53] = new Block(62, 15, spielfeld, Block.Typ.typ3);
			blocks[54] = new Block(74, 15, spielfeld, Block.Typ.typ1);
			blocks[55] = new Block(86, 15, spielfeld, Block.Typ.typ1);

			blocks[56] = new Block(2, 17, spielfeld, Block.Typ.typ2);
			blocks[57] = new Block(14, 17, spielfeld , Block.Typ.typ2);
			blocks[58] = new Block(26, 17, spielfeld, Block.Typ.typ2);
			blocks[59] = new Block(38, 17, spielfeld, Block.Typ.typ2);
			blocks[60] = new Block(50, 17, spielfeld, Block.Typ.typ2);
			blocks[61] = new Block(62, 17, spielfeld, Block.Typ.typ2);
			blocks[62] = new Block(74, 17, spielfeld, Block.Typ.typ2);
			blocks[63] = new Block(86, 17, spielfeld, Block.Typ.typ2);
		}
		if(selectlevel.equals(KeyType.F2)){

			blocks = new Block[64];
			blocks[0] = new Block(2, 3, spielfeld, Block.Typ.typ3);
			blocks[1] = new Block(14, 3, spielfeld , Block.Typ.typ3);
			blocks[2] = new Block(26, 3, spielfeld, Block.Typ.typ1);
			blocks[3] = new Block(38, 3, spielfeld, Block.Typ.typ3);
			blocks[4] = new Block(50, 3, spielfeld, Block.Typ.typ3);
			blocks[5] = new Block(62, 3, spielfeld, Block.Typ.typ1);
			blocks[6] = new Block(74, 3, spielfeld, Block.Typ.typ3);
			blocks[7] = new Block(86, 3, spielfeld, Block.Typ.typ3);

			blocks[8] = new Block(2, 5, spielfeld, Block.Typ.typ3);
			blocks[9] = new Block(14, 5, spielfeld, Block.Typ.typ2);
			blocks[10] = new Block(26, 5, spielfeld, Block.Typ.typ1);
			blocks[11] = new Block(38, 5, spielfeld, Block.Typ.typ2);
			blocks[12] = new Block(50, 5, spielfeld, Block.Typ.typ2);
			blocks[13] = new Block(62, 5, spielfeld, Block.Typ.typ1);
			blocks[14] = new Block(74, 5, spielfeld, Block.Typ.typ2);
			blocks[15] = new Block(86, 5, spielfeld, Block.Typ.typ3);

			blocks[16] = new Block(2, 7, spielfeld, Block.Typ.typ3);
			blocks[17] = new Block(14, 7, spielfeld , Block.Typ.typ2);
			blocks[18] = new Block(26, 7, spielfeld, Block.Typ.typ1);
			blocks[19] = new Block(38, 7, spielfeld, Block.Typ.typ2);
			blocks[20] = new Block(50, 7, spielfeld, Block.Typ.typ2);
			blocks[21] = new Block(62, 7, spielfeld, Block.Typ.typ1);
			blocks[22] = new Block(74, 7, spielfeld, Block.Typ.typ2);
			blocks[23] = new Block(86, 7, spielfeld, Block.Typ.typ3);

			blocks[24] = new Block(2, 9, spielfeld, Block.Typ.typ3);
			blocks[25] = new Block(14, 9, spielfeld , Block.Typ.typ2);
			blocks[26] = new Block(26, 9, spielfeld, Block.Typ.typ1);
			blocks[27] = new Block(38, 9, spielfeld, Block.Typ.typ2);
			blocks[28] = new Block(50, 9, spielfeld, Block.Typ.typ2);
			blocks[29] = new Block(62, 9, spielfeld, Block.Typ.typ1);
			blocks[30] = new Block(74, 9, spielfeld, Block.Typ.typ2);
			blocks[31] = new Block(86, 9, spielfeld, Block.Typ.typ3);

			blocks[32] = new Block(2, 11, spielfeld, Block.Typ.typ3);
			blocks[33] = new Block(14, 11, spielfeld , Block.Typ.typ2);
			blocks[34] = new Block(26, 11, spielfeld, Block.Typ.typ1);
			blocks[35] = new Block(38, 11, spielfeld, Block.Typ.typ2);
			blocks[36] = new Block(50, 11, spielfeld, Block.Typ.typ2);
			blocks[37] = new Block(62, 11, spielfeld, Block.Typ.typ1);
			blocks[38] = new Block(74, 11, spielfeld, Block.Typ.typ2);
			blocks[39] = new Block(86, 11, spielfeld, Block.Typ.typ3);

			blocks[40] = new Block(2, 13, spielfeld, Block.Typ.typ3);
			blocks[41] = new Block(14, 13, spielfeld , Block.Typ.typ2);
			blocks[42] = new Block(26, 13, spielfeld, Block.Typ.typ1);
			blocks[43] = new Block(38, 13, spielfeld, Block.Typ.typ2);
			blocks[44] = new Block(50, 13, spielfeld, Block.Typ.typ2);
			blocks[45] = new Block(62, 13, spielfeld, Block.Typ.typ1);
			blocks[46] = new Block(74, 13, spielfeld, Block.Typ.typ2);
			blocks[47] = new Block(86, 13, spielfeld, Block.Typ.typ3);

			blocks[48] = new Block(2, 15, spielfeld, Block.Typ.typ3);
			blocks[49] = new Block(14, 15, spielfeld , Block.Typ.typ2);
			blocks[50] = new Block(26, 15, spielfeld, Block.Typ.typ1);
			blocks[51] = new Block(38, 15, spielfeld, Block.Typ.typ2);
			blocks[52] = new Block(50, 15, spielfeld, Block.Typ.typ2);
			blocks[53] = new Block(62, 15, spielfeld, Block.Typ.typ1);
			blocks[54] = new Block(74, 15, spielfeld, Block.Typ.typ2);
			blocks[55] = new Block(86, 15, spielfeld, Block.Typ.typ3);

			blocks[56] = new Block(2, 17, spielfeld, Block.Typ.typ1);
			blocks[57] = new Block(14, 17, spielfeld , Block.Typ.typ1);
			blocks[58] = new Block(26, 17, spielfeld, Block.Typ.typ1);
			blocks[59] = new Block(38, 17, spielfeld, Block.Typ.typ1);
			blocks[60] = new Block(50, 17, spielfeld, Block.Typ.typ1);
			blocks[61] = new Block(62, 17, spielfeld, Block.Typ.typ1);
			blocks[62] = new Block(74, 17, spielfeld, Block.Typ.typ1);
			blocks[63] = new Block(86, 17, spielfeld, Block.Typ.typ1);
		}
		if(selectlevel.equals(KeyType.F3)){
			blocks = new Block[64];
			blocks[0] = new Block(2, 3, spielfeld, Block.Typ.typ2);
			blocks[1] = new Block(14, 3, spielfeld , Block.Typ.typ2);
			blocks[2] = new Block(26, 3, spielfeld, Block.Typ.typ2);
			blocks[3] = new Block(38, 3, spielfeld, Block.Typ.typ2);
			blocks[4] = new Block(50, 3, spielfeld, Block.Typ.typ2);
			blocks[5] = new Block(62, 3, spielfeld, Block.Typ.typ2);
			blocks[6] = new Block(74, 3, spielfeld, Block.Typ.typ2);
			blocks[7] = new Block(86, 3, spielfeld, Block.Typ.typ2);

			blocks[8] = new Block(2, 5, spielfeld, Block.Typ.typ1);
			blocks[9] = new Block(14, 5, spielfeld, Block.Typ.typ1);
			blocks[10] = new Block(26, 5, spielfeld, Block.Typ.typ1);
			blocks[11] = new Block(38, 5, spielfeld, Block.Typ.typ1);
			blocks[12] = new Block(50, 5, spielfeld, Block.Typ.typ1);
			blocks[13] = new Block(62, 5, spielfeld, Block.Typ.typ1);
			blocks[14] = new Block(74, 5, spielfeld, Block.Typ.typ1);
			blocks[15] = new Block(86, 5, spielfeld, Block.Typ.typ1);

			blocks[16] = new Block(2, 7, spielfeld, Block.Typ.typ1);
			blocks[17] = new Block(14, 7, spielfeld , Block.Typ.typ1);
			blocks[18] = new Block(26, 7, spielfeld, Block.Typ.typ1);
			blocks[19] = new Block(38, 7, spielfeld, Block.Typ.typ1);
			blocks[20] = new Block(50, 7, spielfeld, Block.Typ.typ1);
			blocks[21] = new Block(62, 7, spielfeld, Block.Typ.typ1);
			blocks[22] = new Block(74, 7, spielfeld, Block.Typ.typ1);
			blocks[23] = new Block(86, 7, spielfeld, Block.Typ.typ1);

			blocks[24] = new Block(2, 9, spielfeld, Block.Typ.typ1);
			blocks[25] = new Block(14, 9, spielfeld , Block.Typ.typ1);
			blocks[26] = new Block(26, 9, spielfeld, Block.Typ.typ1);
			blocks[27] = new Block(38, 9, spielfeld, Block.Typ.typ1);
			blocks[28] = new Block(50, 9, spielfeld, Block.Typ.typ1);
			blocks[29] = new Block(62, 9, spielfeld, Block.Typ.typ1);
			blocks[30] = new Block(74, 9, spielfeld, Block.Typ.typ1);
			blocks[31] = new Block(86, 9, spielfeld, Block.Typ.typ1);

			blocks[32] = new Block(2, 11, spielfeld, Block.Typ.typ1);
			blocks[33] = new Block(14, 11, spielfeld , Block.Typ.typ1);
			blocks[34] = new Block(26, 11, spielfeld, Block.Typ.typ1);
			blocks[35] = new Block(38, 11, spielfeld, Block.Typ.typ1);
			blocks[36] = new Block(50, 11, spielfeld, Block.Typ.typ1);
			blocks[37] = new Block(62, 11, spielfeld, Block.Typ.typ1);
			blocks[38] = new Block(74, 11, spielfeld, Block.Typ.typ1);
			blocks[39] = new Block(86, 11, spielfeld, Block.Typ.typ1);

			blocks[40] = new Block(2, 13, spielfeld, Block.Typ.typ1);
			blocks[41] = new Block(14, 13, spielfeld , Block.Typ.typ1);
			blocks[42] = new Block(26, 13, spielfeld, Block.Typ.typ1);
			blocks[43] = new Block(38, 13, spielfeld, Block.Typ.typ1);
			blocks[44] = new Block(50, 13, spielfeld, Block.Typ.typ1);
			blocks[45] = new Block(62, 13, spielfeld, Block.Typ.typ1);
			blocks[46] = new Block(74, 13, spielfeld, Block.Typ.typ1);
			blocks[47] = new Block(86, 13, spielfeld, Block.Typ.typ1);

			blocks[48] = new Block(2, 15, spielfeld, Block.Typ.typ3);
			blocks[49] = new Block(14, 15, spielfeld , Block.Typ.typ1);
			blocks[50] = new Block(26, 15, spielfeld, Block.Typ.typ1);
			blocks[51] = new Block(38, 15, spielfeld, Block.Typ.typ1);
			blocks[52] = new Block(50, 15, spielfeld, Block.Typ.typ1);
			blocks[53] = new Block(62, 15, spielfeld, Block.Typ.typ1);
			blocks[54] = new Block(74, 15, spielfeld, Block.Typ.typ1);
			blocks[55] = new Block(86, 15, spielfeld, Block.Typ.typ3);

			blocks[56] = new Block(2, 17, spielfeld, Block.Typ.typ3);
			blocks[57] = new Block(14, 17, spielfeld , Block.Typ.typ3);
			blocks[58] = new Block(26, 17, spielfeld, Block.Typ.typ1);
			blocks[59] = new Block(38, 17, spielfeld, Block.Typ.typ1);
			blocks[60] = new Block(50, 17, spielfeld, Block.Typ.typ1);
			blocks[61] = new Block(62, 17, spielfeld, Block.Typ.typ1);
			blocks[62] = new Block(74, 17, spielfeld, Block.Typ.typ3);
			blocks[63] = new Block(86, 17, spielfeld, Block.Typ.typ3);
		}

	}
	//GameOver Bildschirm
	public static void showGameOver(Terminal terminal) throws IOException {

		terminal.clearScreen();

		// GameOver mit Text
		// der Text wird hier direkt in das Terminal geschrieben und nicht in das Spielfeld
		terminal.setCursorPosition(6, 6);
		Write("  _____                         ____                 ", terminal);
		terminal.setCursorPosition(6, 7);
		Write(" / ____|                       / __ \\                ", terminal);
		terminal.setCursorPosition(6, 8);
		Write("| |  __  __ _ _ __ ___   ___  | |  | |_   _____ _ __ ", terminal);
		terminal.setCursorPosition(6, 9);
		Write("| | |_ |/ _` | '_ ` _ \\ / _ \\ | |  | \\ \\ / / _ \\ '__|", terminal);
		terminal.setCursorPosition(6, 10);
		Write("| |__| | (_| | | | | | |  __/ | |__| |\\ V /  __/ |   ", terminal);
		terminal.setCursorPosition(6, 11);
		Write(" \\_____|\\__,_|_| |_| |_|\\___|  \\____/  \\_/ \\___|_|   ", terminal);

		// Cursor auf Position bewegen
		terminal.setCursorPosition(6, 14);
		Write("Game Over", terminal); // Text schreiben
		terminal.setCursorPosition(6,15);
		Write("Du hast " + Player.highscore + " Punkte gesammelt.", terminal);
		if(highscore > localHighscore){
			terminal.setCursorPosition(6,16);
			Write("du hast den Highscore geknackt gl??ckwunsch", terminal);
		}else{
			terminal.setCursorPosition(6,16);
			Write("Du hast Verloren, schade villeicht n??chstes mal", terminal);
		}
		terminal.setCursorPosition(6, 17);
		Write("Dr??cke ESCAPE, um das Spiel zu verlassen.", terminal);
		terminal.setCursorPosition(6, 18);

		terminal.setCursorPosition(6,25 );
		Write("Highscore Level 1: " + strArray[0], terminal);
		terminal.setCursorPosition(6,26 );
		Write("Highscore Level 2: "+strArray[1], terminal);
		terminal.setCursorPosition(6,27 );
		Write("Highscore Level 3: "+ strArray[2], terminal);


		if(localHighscore < highscore) {
			while (true) {
				KeyStroke keyStroke = terminal.pollInput();
				terminal.setCursorPosition(9, 20);
				Write("Gebe einen Namen ein: " + username + "\n", terminal);
				if (keyStroke == null) {
					terminal.flush();
					keyStroke = terminal.readInput();
				}
				if (keyStroke.getKeyType() == KeyType.Enter) {
					setHighscore();
					break;
				}
				try {
					if (keyStroke.getKeyType() == KeyType.Backspace) {
						username = username.substring(0, username.length() - 1);
					} else {
						char eingabe = keyStroke.getCharacter();
						username = username + eingabe;
					}
				} catch (NullPointerException e) {
					System.out.println("ung??ltige eingabe");
				}
			}
		}

		// Texte im Terminal anzeigen
		terminal.flush();
	}

	public static void sleep2Sec(){
		try
		{
			Thread.sleep(2000);
		}
		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	public static void getHighscore(){
		File datei = new File("src\\main\\java\\game\\Highscore.txt");
		Scanner scan = null;
		strArray[0] = "";
		strArray[1] = "";
		strArray[2] = "";
		try{
			scan = new Scanner(datei);
			while (scan.hasNext()){
				for(int i = 0; i < strArray.length; i++ ){
					strArray[i] = scan.nextLine();
				}
			}

		}catch(FileNotFoundException e){
			System.out.println("File not found");
		}
	}

	public static void getlevelhighscore(){

		switch (level){
			case 1:
				split = strArray[0].split(" ");
				localName = split[0];
				localHighscore = Integer.parseInt(split[1]);
				break;
			case 2:
				split = strArray[1].split(" ");
				localName = split[0];
				localHighscore = Integer.parseInt(split[1]);
				break;
			case 3:
				split = strArray[2].split(" ");
				localName = split[0];
				localHighscore = Integer.parseInt(split[1]);
				break;
		}
	}

	public static void setHighscore(){
		File datei = new File("src\\main\\java\\game\\Highscore.txt");
		String[] test = new String[3];
		try{
			FileWriter writer = new FileWriter(datei);
			if(localHighscore < Player.highscore){
				switch (level){
					case 1:
						test[0] = username;
						test[1] = Integer.toString(highscore);
						strArray[0] = test[0] +" "+  test[1];

						break;
					case 2:
						test[0] = username;
						test[1] = Integer.toString(highscore);
						strArray[1] = test[0] +" "+ test[1];
						break;
					case 3:
						test[0] = username;
						test[1] = Integer.toString(highscore);
						strArray[2] = test[0] +" "+ test[1];
						break;
				}
			}
			writer.write(strArray[0] + "\n");
			writer.write(strArray[1] + "\n");
			writer.write(strArray[2] + "\n");
			writer.flush();
			username = "";
		} catch (IOException e) {
			System.out.printf("Datei konnte nicht bearbeitet werden");
		}

	}
}
