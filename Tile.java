package lotsofyou;

import jig.Vector;

import java.util.ArrayList;

public class Tile {
    private Vector pos;
    private SpriteStack spriteStack;
    private ArrayList<Rectangle> colliders;

    public Tile(SpriteStack s, ArrayList<Rectangle> colliders) {
        this.spriteStack = s;
        this.colliders = colliders;
    }

    public Tile(Tile other) {
        this.pos = other.pos;
        this.spriteStack = other.spriteStack;
        this.colliders = other.colliders;
    }

    ArrayList<Rectangle> getColliders() {
        return this.colliders;
    }

    public void setPos(Vector p) {
        this.pos = p;
    }

    boolean intersects(Rectangle other) {
        boolean ret = false;
        for(Rectangle r : colliders) {
            Rectangle cpy = new Rectangle(r.getX() + pos.getX(), r.getY() + pos.getY(), r.getWidth(), r.getHeight());
            if (cpy.intersects(other)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    void draw() {
        spriteStack.draw(pos.getX(), pos.getY());
    }

    float getX() {
        return pos.getX();
    }

    float getY() {
        return pos.getY();
    }
}
