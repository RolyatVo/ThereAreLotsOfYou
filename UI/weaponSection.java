package lotsofyou.UI;

import jig.ResourceManager;
import lotsofyou.LotsOfYouGame;
import org.newdawn.slick.*;

public class weaponSection {
    private SpriteSheet sword;
    private boolean hasWeapon;
    private int scale;

    public weaponSection() {
        this.scale = 2;
        sword = new SpriteSheet(ResourceManager.getImage(LotsOfYouGame.SWORD_RSC).getScaledCopy(scale), 32*scale , 32*scale);
        hasWeapon = false;
    }

    public void render(float x, float y, Graphics g) {
        if(hasWeapon)
            sword.draw(x,y);

    }

    public void update(boolean hasWeapon) {
        setHasWeapon(hasWeapon);
    }

    public void setHasWeapon(boolean hasWeapon) {
        this.hasWeapon = hasWeapon;
    }

    public void setScale(int scale) { this.scale = scale; }
}
