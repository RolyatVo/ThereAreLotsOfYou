package lotsofyou.UI;

import jig.ResourceManager;
import jig.Vector;
import lotsofyou.LotsOfYouGame;
import lotsofyou.Player;
import org.lwjgl.examples.spaceinvaders.Sprite;
import org.newdawn.slick.Graphics;


public class minimap {
    int x;
    int y;

    float playerX;
    float playerY;

    public minimap(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(Graphics g) {
        //the 28 is because off the shaved off sides of the texture, the map is 128x128 while the texture is 100 x 100
        //the three is the camera zoom. 16 is the tile size
        float pixelX = playerX / 16 - 16;
        float pixelY = playerY / 16 - 16;

        ResourceManager.getImage(LotsOfYouGame.MINIMAP).draw(x, y);
        ResourceManager.getImage(LotsOfYouGame.PLAYER_MARKER).draw(x + pixelX, y + pixelY);
    }

    public void update(Player p) {
        this.playerX = p.getX();
        this.playerY = p.getY();
    }
}
