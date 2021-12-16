package lotsofyou;

import jig.Vector;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;

public class Collectible {
    public enum Type {
        SWORD,
        ARMOR
    }

    private static int idCtr = 0;
    private int id;

    private Type type;
    private Vector pos;
    private SpriteStack sprite;

    private static final float pickupDistance = 10.0f;
    private static final float rotationSpeed = 5.0f;

    private static final ArrayList<Collectible> collectibles = new ArrayList<>();
    private static Camera cam;

    public static void setCollectibleRenderCam(Camera cam) {
        Collectible.cam = cam;
    }

    public static void addCollectible(Type type, Vector pos) {
        try {
            SpriteStack spriteStack = null;
            switch (type) {
                case ARMOR -> spriteStack = new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
                case SWORD -> spriteStack = new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
                default -> new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
            }
            collectibles.add(new Collectible(type, pos, spriteStack, idCtr++));
        } catch (java.lang.RuntimeException | org.newdawn.slick.SlickException e) {
            System.out.println("Couldn't find the texture, loading collectible without one.");
            collectibles.add(new Collectible(type, pos, null, idCtr++));
        }
    }

    public static ArrayList<Collectible> getCollectibles() {
        return collectibles;
    }

    public static void removeCollectible(int id) {
        Collectible target = null;
        for(Collectible c : collectibles) {
            if(c.id == id) target = c;
        }
        if(target != null) collectibles.remove(target);
    }

    private Collectible(Type type, Vector pos, SpriteStack sprite, int id) {
        this.type = type;
        this.pos = pos;
        this.sprite = sprite;
        this.id = id;
    }

    public boolean intersects(Vector pos) {
        return pos.subtract(this.pos).length() < pickupDistance;
    }

    public Type getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public void render() {
        sprite.setRotation(sprite.getRotation() + rotationSpeed);
        sprite.draw(pos.getX() - (float)sprite.getFrameWidth() / 2, pos.getY() - (float)sprite.getFrameHeight() / 2);
    }
}
