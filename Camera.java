package lotsofyou;

import jig.Vector;

public class Camera {
    private float x;
    private float y;

    private float width;
    private float height;

    private float scale;
    private float rotation;

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
}
