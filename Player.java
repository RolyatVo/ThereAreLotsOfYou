package lotsofyou;

import jig.Vector;
import org.lwjgl.examples.spaceinvaders.Sprite;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player {
    private float x;
    private float y;
    private float width;
    private float height;

    private final float moveSpeed = 50.0f;

    private float rotation;


    SpriteStack playerSprite;

    public Player(SpriteStack sprite, float x, float y, float width, float height) {
        playerSprite = sprite;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = 0;
    }

    void update(float delta, Input in, Camera cam) {
        float deltaSeconds = delta / 1000;

        int xDir = 0;
        int yDir = 0;
        if(in.isKeyDown(Keyboard.KEY_A)) --xDir;
        if(in.isKeyDown(Keyboard.KEY_D)) ++xDir;
        if(in.isKeyDown(Keyboard.KEY_W)) --yDir;
        if(in.isKeyDown(Keyboard.KEY_S)) ++yDir;

        float transX = (float)Math.sin(Math.toRadians(cam.getRotation())) * (yDir * moveSpeed * deltaSeconds);
        float transY = (float)Math.cos(Math.toRadians(cam.getRotation())) * (yDir * moveSpeed * deltaSeconds);

        transX += (float)Math.sin(Math.toRadians(cam.getRotation() + 90)) * (xDir * moveSpeed * deltaSeconds);
        transY += (float)Math.cos(Math.toRadians(cam.getRotation() + 90)) * (xDir * moveSpeed * deltaSeconds);

        x += transX;
        y += transY;

        if(in.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            Vector mousePos = cam.screenToWorld(in.getMouseX(), in.getMouseY());
            rotation = (float)mousePos.subtract(new Vector(x, y)).getRotation(); //+ cam.getRotation();
            playerSprite.setRotation(rotation);
        }
    }



    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    public void render() {
        playerSprite.draw(x - width / 2 ,y - height / 2);
    }
    public void render(float x, float y) { playerSprite.draw(x, y);}
}
