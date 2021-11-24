package lotsofyou;

import jig.Vector;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Input;

public class Camera {
    private float x;
    private float y;

    private float width;
    private float height;

    private float scale;
    private float rotation;

    private final float moveRatio = 0.08f;

    public Camera(float width_, float height_) {
        this(0, 0, width_, height_);
    }

    public Camera(float x_, float y_, float width_, float height_) {
        this(x_, y_, width_, height_, 1.0f, 0.0f);
    }

    public Camera(float x_, float y_, float width_, float height_, float scale_, float rotation_) {
        this.x = x_;
        this.y = y_;
        this.width = width_;
        this.height = height_;
        this.scale = scale_;
        this.rotation = rotation_;
    }

    public Vector getPos() {
        return new Vector(x, y);
    }

    public float getX() {
        return x;
    }

    public float getCenterX() {
        return x + width / 2;
    }

    public float getY() {
        return y;
    }

    public float getCenterY() {
        return y + height / 2;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getScale() {
        return scale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void move(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void rotate(float angle) {
        this.rotation += angle;
    }

    public void update(Player targetPlayer, Input in) {
        x += moveRatio * (targetPlayer.getX() - getCenterX());
        y += moveRatio * (targetPlayer.getY() - getCenterY());

        if(Math.abs(targetPlayer.getRotation() - rotation) < Math.abs(rotation - targetPlayer.getRotation())) {
            rotation += moveRatio * (targetPlayer.getRotation() - rotation);
        } else {
            rotation -= moveRatio * (rotation - targetPlayer.getRotation());
        }

//        int zoom = 0;
//        if(in.isKeyDown(Keyboard.KEY_N)) --zoom;
//        if(in.isKeyDown(Keyboard.KEY_M)) ++zoom;
//        scale += (float)zoom * 0.2f;

//        int rotDir = 0;
//        if(in.isKeyDown(Keyboard.KEY_J)) ++rotDir;
//        if(in.isKeyDown(Keyboard.KEY_K)) --rotDir;
//        rotation += (float) rotDir * 0.5f;
    }

    public Vector screenToWorld(float x, float y) {
        double cs = Math.cos(Math.toRadians(-rotation));
        double sn = Math.sin(Math.toRadians(-rotation));

        float newCenterOffsetX = x - (width / 2);
        float newCenterOffsetY = y - (height / 2);
        float oldCenterOffsetX = (float)(newCenterOffsetX * cs - newCenterOffsetY * sn) / scale;
        float oldCenterOffsetY = (float)(newCenterOffsetX * sn + newCenterOffsetY * cs) / scale;
        return new Vector(oldCenterOffsetX + (width / 2) + this.x, oldCenterOffsetY + (height / 2) + this.y);
    }
}
