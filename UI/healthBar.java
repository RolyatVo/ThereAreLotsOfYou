package lotsofyou.UI;
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
        g.setLineWidth(5);
        g.setColor(Color.black);
        g.drawRect(x-2.5f,y-2.5f,width+5,height+5);

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