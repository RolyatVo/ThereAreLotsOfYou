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
    private Vector targetPos;

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
        this.targetPos = new Vector(x, y);
    }

    public Vector getPos() {
        return new Vector(x, y);
    }

    public Vector getRenderRes() {
        return new Vector(width, height).scale(1.0f / scale);
    }

    public float getRenderWidth() {
        return width / scale;
    }

    public float getRenderHeight() {
        return height / scale;
    }

    public float getX() {
        return x;
    }

    public float getCenterX() {
        return x + getRenderWidth() / 2;
    }

    public float getY() {
        return y;
    }

    public float getCenterY() {
        return y + getRenderHeight() / 2;
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

    public void setTargetPos(float x, float y) {
        this.targetPos = new Vector(x, y);
    }

    public void rotate(float angle) {
        this.rotation += angle;
    }

    public void update(Input in, Player player) {
        x += moveRatio * (targetPos.getX() - getCenterX());
        y += moveRatio * (targetPos.getY() - getCenterY());

        rotation = player.getMoveRotation();

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

        float newCenterOffsetX = x / scale - (getRenderWidth() / 2);
        float newCenterOffsetY = y / scale - (getRenderHeight() / 2);
        float oldCenterOffsetX = (float)(newCenterOffsetX * cs - newCenterOffsetY * sn);
        float oldCenterOffsetY = (float)(newCenterOffsetX * sn + newCenterOffsetY * cs);
        return new Vector(oldCenterOffsetX + (getRenderWidth() / 2) + this.x, oldCenterOffsetY + (getRenderHeight() / 2) + this.y);
    }
}
