package game;

import com.googlecode.lanterna.TextColor;

public class Pixel {
	public char Text;
	public TextColor textColor;
	public TextColor backColor;
	
	public Pixel() {
		this.Text = ' ';
		textColor = TextColor.ANSI.WHITE;
		backColor = TextColor.ANSI.BLACK;
	}
}
