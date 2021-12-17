package lotsofyou;
import lotsofyou.UI.*;
import org.newdawn.slick.Graphics;


public class UI_interface {

    private final float bottomRow;
    private final float left;


    private healthBar healthBar;
    private armorSection armorSection;
    private weaponSection weaponSection;
    private announcement announce;

    public UI_interface(int screenWidth, int screenHeight) {
        this.bottomRow = screenHeight * 0.9f - 15;
        this.left = 20;


        healthBar = new healthBar(100, 250, 30);
        armorSection = new armorSection(200, 30);
        weaponSection = new weaponSection();
        announce = new announcement(screenWidth, screenHeight);
    }

    public void render(Graphics g) {
        this.healthBar.render(left, bottomRow, g);

        this.weaponSection.render(960 * 0.68f, bottomRow, g);
        this.armorSection.render(960 * 0.75f, bottomRow, g);
        announce.render(g);
    }

    public void update(Player player) {
        healthBar.update(player.getHealthNUM());
        armorSection.update(player.getArmorPlates());
        weaponSection.update(true);
    }

    public void setAnnouncement(String text) {
        this.announce.setText(text);
    }
}



