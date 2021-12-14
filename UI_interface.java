package lotsofyou;
import lotsofyou.UI.*;
import org.newdawn.slick.Graphics;


public class UI_interface {

    private final float bottomRow;
    private final float left;


    private healthBar healthBar;

    public UI_interface(Player player, int screenWidth, int screenHeight) {
        this.bottomRow = screenHeight * 0.9f;
        this.left = 20;


        healthBar = new healthBar(player.getHealthNUM(), 250, 30);
    }

    public void render(Graphics g) {
        this.healthBar.render(left, bottomRow, g);
    }

    public void update(Player player) {
        healthBar.update(player.getHealthNUM());
    }
}



