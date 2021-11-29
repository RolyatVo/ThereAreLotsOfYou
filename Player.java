package lotsofyou;

import org.lwjgl.examples.spaceinvaders.Sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player {
    private float x;
    private float y;


    SpriteStack playerSprite;
    Camera cam;

    public Player(SpriteStack sprite, float x, float y, Camera cam) {
        playerSprite = sprite;
        this.x = x;
        this.y = y;
        this.cam = cam;
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
        playerSprite.draw(x ,y);
    }
    public void render(float x, float y) { playerSprite.draw(x, y);}
}
