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
    private SpriteStackAnimation animatedSprite;


    private static final float pickupDistance = 10.0f;
    private static final float rotationSpeed = 1.0f;

    private static final ArrayList<Collectible> collectibles = new ArrayList<>();
    private static Camera cam;

    public static void setCollectibleRenderCam(Camera cam) {
        Collectible.cam = cam;
    }

    public static void addCollectible(Type type, Vector pos) {
        try {
            Animations animations = new Animations(cam);
            SpriteStackAnimation spriteStack = animations.walkingAnimation;
            switch (type) {
                case ARMOR -> spriteStack = animations.armorAnimation;
                case SWORD -> spriteStack = animations.swordAnimation;
              // default -> new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
            }
            collectibles.add(new Collectible(type, pos, spriteStack, idCtr++));
        } catch (java.lang.RuntimeException | org.newdawn.slick.SlickException e) {
            System.out.println("Couldn't find the texture, loading collectible without one.");
            collectibles.add(new Collectible(type, pos,  null, idCtr++));
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

//    private Collectible(Type type, Vector pos, SpriteStack sprite, int id) {
//        this.type = type;
//        this.pos = pos;
//        this.sprite = sprite;
//        this.id = id;
//    }
    private Collectible(Type type, Vector pos, SpriteStackAnimation spriteAnimation, int id) {
        this.type = type;
        this.pos = pos;
        this.animatedSprite = spriteAnimation;
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
        animatedSprite.setRotation(animatedSprite.getRotation() + rotationSpeed);
        animatedSprite.draw(pos.getX() - animatedSprite.getFrameWidth() / 2, pos.getY() - animatedSprite.getFrameHeight() / 2);
    }
    public void update(int delta) {
        collectibles.forEach(c -> c.animatedSprite.update(delta));
    }

    public static void clearCollectibles() {
        collectibles.clear();
    }
}
