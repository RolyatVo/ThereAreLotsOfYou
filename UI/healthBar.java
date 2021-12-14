package lotsofyou.UI;
import lotsofyou.Player;
import org.newdawn.slick.*;



public class healthBar {
    private int percent;

    private int width;
    private int height;

    public healthBar(int percent, int barWidth, int barHeight) {
        this.width = barWidth;
        this.height = barHeight;
        this.percent = percent;
    }

    public void render(float x, float y, Graphics g) {

        //Draw black edge
        g.setLineWidth(7);
        g.setColor(Color.black);
        g.drawRect(x,y,width,height);

        //Draw red filled box
        g.setColor(Color.red);
        g.fillRect(x, y, percent * 2.5f, height);

        //Draw hp number
        g.setColor(Color.black);
        g.drawString( "" + percent, x + width / 2f - 10 , y + height / 2f - 8);

    }
    public void render() {

    }

    public void update(int hp) {
        this.percent = hp;
    }



}