package lotsofyou.UI;
import jig.ResourceManager;
import lotsofyou.LotsOfYouGame;
import org.newdawn.slick.*;


public class armorSection {
    private int width;
    private int height;
    private int armorRating;

    private SpriteSheet shieldPNG;
    private Animation shieldAnimation;

    public armorSection(int widthSection, int heightSection) {
        this.width = widthSection;
        this.height = heightSection;
        this.shieldPNG = new SpriteSheet(ResourceManager.getImage(LotsOfYouGame.ARMOR_RSC), 32, 32);
        this.shieldAnimation = new Animation(this.shieldPNG, 150);
    }

    public void render(float x, float y, Graphics g) {
        for(int i =0; i<armorRating; i++) {
            shieldAnimation.draw(x + i*40, y );
        }

    }


    public void update(int armorAmount) {
        setArmorRating(armorAmount);
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setArmorRating(int num) {
        this.armorRating = num;
    }
}
