package lotsofyou.UI;

import org.newdawn.slick.Graphics;

public class announcement {
    private int width;
    private int height;
    private String text;

    public announcement(int width, int height) {
        this.width = width;
        this.height = height;
        text = "";
    }

    public void setText(String text) {
        this.text = text;
    }

    public void render(Graphics g) {
        g.drawString(text, width / 2, height / 2);
    }
}
