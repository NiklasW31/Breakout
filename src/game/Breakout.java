package game;

import java.io.IOException;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.Indexed;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;

import static game.Player.gety;

public class Breakout {

	public enum Richtung {
		Rechts, Links, Unten, Oben
	}

	public static int spielfeldHoehe = 50;
	public static int spielfeldBreite = 100;
	public static Pixel[][] spielfeld;
	public static TextColor DefaultBackColor = TextColor.ANSI.BLACK;
	public static TextColor DefaultTextColor = TextColor.ANSI.WHITE;

	public static void main(String[] args) throws IOException {

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

		// "Application-loop" - kehrt immer wieder zum Startbildschirm zurück
		// wird beim Startbildschirm ESCAPE gedrückt wird die Anwendung beendet
		while (true) {
			// zeigt simple Startseite an, die mit "Enter" oder "Escape" verlassen wird
			showStartseite(terminal);

			// "Game-loop" wird hier ausgeführt
			double geschwindigkeit = 50;
			runGame(geschwindigkeit, terminal);
			
			// GAME OVER  hier hinzufügen
		}

	}

	private static void runGame(double geschwindigkeit, Terminal terminal) throws IOException {

		// initiale Spieleinstellungen
		Richtung richtung = Richtung.Rechts;
		int posX = spielfeldBreite / 2;
		int posY = spielfeldHoehe / 2;

		// Startfarbe
		int r = 3, g = 33, b = 66;

		Player player = new Player();
		Ball ball = new Ball();

		// Spiel in "Dauerschleife" (game loop)
		while (true) {

			ClearSpielfeld();
			draw();
			moveBall();
			collisionBall();

			// Hintergrundfarbe mit RGB (ACHTUNG 6x6x6 Color Cube)
			// siehe TextColor Klasse in Lanterna

			// obere Zeile einfärben
			for (int i = 0; i < spielfeldBreite; i++) {
				spielfeld[i][0].backColor = Indexed.fromRGB(r, g, b);
			}

			// untere Zeile einfärben
			for (int i = 0; i < spielfeldBreite; i++) {
				spielfeld[i][spielfeldHoehe - 1].backColor = Indexed.fromRGB(r, g, b);
			}

			// linke Spalte einfärben
			for (int i = 0; i < spielfeldHoehe; i++) {
				spielfeld[0][i].backColor = Indexed.fromRGB(r, g, b);
				spielfeld[1][i].backColor = Indexed.fromRGB(r, g, b);
			}

			// rechte Spalte einfärben
			for (int i = 0; i < spielfeldHoehe; i++) {
				spielfeld[spielfeldBreite - 2][i].backColor = Indexed.fromRGB(r, g, b);
				spielfeld[spielfeldBreite - 1][i].backColor = Indexed.fromRGB(r, g, b);
			}

			// Farbe verändern 
			// (Modulo wird verwendet, weil die Zahlen nur zwischen 0-255 liegen dürfen
			r += 3;
			g += 3;
			b += 3;
			r %= 256;
			g %= 256;
			b %= 256;

			// Tastatureinggabe wird gelesen
			//KeyStroke eingabe = terminal.readInput(); 	// stopped und wartet auf Eingabe
			KeyStroke eingabe = terminal.pollInput(); 		// läuft weiter, auch wenn keine Eingabe erfolgt ist 
			if (eingabe != null) {

				// wenn die linke Pfeiltaste gedrückt wird
				if (eingabe.getKeyType().equals(KeyType.ArrowLeft)) {
					// kann nicht in entgegen gesetzte Richtung laufen (z.B. Snake)

						richtung = Richtung.Links;
						
						// wenn der Spielfeld verlassen wird, dann ...
						if (Player.x > 2) {
							Player.x --;
						}



				}

				// wenn die rechte Pfeiltaste gedrückt wird
				if (eingabe.getKeyType().equals(KeyType.ArrowRight)) {

						richtung = Richtung.Rechts;
						if (Player.x + Player.groese < spielfeldBreite - 2) {
							Player.x ++;
						}

				}


				// wenn ESC Taste gedrückt wird
				if (eingabe.getKeyType().equals(KeyType.Escape)) {
					// Spielfeld leerräumen für den GAME OVER / Startbildschirm
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

	private static void showStartseite(Terminal terminal) throws IOException {

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
		Write("Drücke ENTER, um das Spiel zu starten.", terminal);
		terminal.setCursorPosition(6, 16);
		Write("Drücke ESCAPE, um das Spiel zu verlassen.", terminal);

		// Texte im Terminal anzeigen
		terminal.flush();

		// Eingabe abwarten
		while (true) {

			// Tastatureinggabe wird gelesen
			KeyStroke eingabe = terminal.readInput();
			if (eingabe != null) {

				// System.out.println(eingabe); // zur Kontrolle kann eingebene
				// Taste angezeigt werden

				// wenn die Taste ENTER gedruckt wird
				if (eingabe.getKeyType().equals(KeyType.Enter)) {

					// Startbildschirm wird beendet / while Schleife wird 
					// unterbrochen (GAME-Loop wird danach gestartet - siehe main-Methode 
					break;
				}

				// wenn die Taste ESC gedrückt wird, beendet sich das Programm
				if (eingabe.getKeyType().equals(KeyType.Escape)) {
					System.exit(0);
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
			}
		}

		terminal.flush();
	}

	// löscht den Inhalt vom Spielfeld
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
		spielfeld[Ball.stuezVektorX][Ball.stuezVektorY].Text = '\u058E';

	}
	public static void moveBall(){

		// wenn ball senkrecht nach unten gehen soll
		if(Ball.richtungsVektorX == 0 && Ball.richtungsVektorY == -1){

			Ball.stuezVektorY ++;
		}


		//wenn ball senkrecht nach oben gehen soll
		if(Ball.richtungsVektorX == 0 && Ball.richtungsVektorY == 1){
			Ball.stuezVektorY--;
		}

	}

	public static void collisionBall(){

		//wenn der ball und der Spieler auf der Y Achse auf der selbenstufe sind
		if(Player.y - 1 == Ball.stuezVektorY){

			//jeder wert vom Spieler wird mit dem Ball abgeglichen
			for(int i = 0; i < Player.groese; i++){

				//wenn ein wert übereinstimmt
				if(Player.x + i == Ball.stuezVektorX){

					//richtung des balles wird verändert
					Ball.richtungsVektorY = 1;
				}
			}
		}
	}




}
