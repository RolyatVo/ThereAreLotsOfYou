package lotsofyou;

import jig.Vector;
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
    }

    public Vector screenToWorld(float x, float y) {
        System.out.println(x + ", " + y);
        double cs = Math.cos(Math.toRadians(rotation));
        double sn = Math.sin(Math.toRadians(rotation));

        float worldX = x - getWidth() / 2;

/*
        Vector centerOffset = corners[i].subtract(new Vector(renderCam.getX(), renderCam.getY())).subtract(
                new Vector(renderCam.getWidth() / 2, renderCam.getHeight() / 2));
        float newCenterOffsetX = (float)(centerOffset.getX() * cs - centerOffset.getY() * sn) * renderCam.getScale();
        float newCenterOffsetY = (float)(centerOffset.getX() * sn + centerOffset.getY() * cs) * renderCam.getScale();
        corners[i] = new Vector(newCenterOffsetX + renderCam.getWidth() / 2, newCenterOffsetY + renderCam.getHeight() / 2);
 */

        return new Vector(0, 0);
    }
}
