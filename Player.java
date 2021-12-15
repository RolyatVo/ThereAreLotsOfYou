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

    //the player needs to propogate the keypresses, and their look rotation to the server

    private float x;
    private float y;
    private float width;
    private float height;

    private final float moveSpeed = 50.0f;
    private final float rollSpeed = 240.0f;

    //the direction the player is looking
    private float lookRotation;
    //the forward movement direction directions
    private float moveRotation;
    private float targetMoveRotation;

    private int keyPress;
    private int ID;

    SpriteStack playerSprite;
    SpriteStackAnimation playerAnimation;

    private int healthNUM = 50;
    private int armorPlates = 0;

    private final float rotationAmount = 30.0f;
    private final float moveRatio = 0.08f;

    private float actionTime;
    private final float rollTimeMax = 0.2f;
    private final float attackTimeMax = 0.5f;
    private final float attackOffset = 20.0f;
    private final float attackRadius = 40.0f;

    private Vector rollDir;

    private enum State {
        FREE,
        ATTACKING,
        ROLLING
    }

    private State state;

    public Player(SpriteStack sprite, float x, float y, float width, float height) {
        playerSprite = sprite;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lookRotation = 0;
        this.moveRotation = 0;
        this.targetMoveRotation = 0;
        this.actionTime = 0;
        this.rollDir = new Vector(0, 0);
        keyPress = -1;
        ID = -99;
        state = State.FREE;
    }
    public Player(SpriteStackAnimation sprite, float x, float y, float width, float height) {
        playerAnimation = sprite;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lookRotation = 0;
        this.moveRotation = 0;
        this.targetMoveRotation = 0;
        this.actionTime = 0;
        this.rollDir = new Vector(0, 0);
        keyPress = -1;
        ID = -99;
        state = State.FREE;
    }



    public Player(float x, float y, int id) {
        this.x = x;
        this.y = y;
        ID = id;

    }

    void update(float delta, Input in, Camera cam) {
        float deltaSeconds = delta / 1000;

        playerAnimation.update((int) delta);

        switch (state) {
            case FREE -> free(deltaSeconds, in, cam);
            case ROLLING -> rolling(deltaSeconds);
            case ATTACKING -> attacking(deltaSeconds);
        }
    }

    private void free(float deltaSeconds, Input in, Camera cam) {
        if(in.isKeyPressed(Keyboard.KEY_Q)) {
            targetMoveRotation += rotationAmount;
        } else if (in.isKeyPressed(Keyboard.KEY_E)) {
            targetMoveRotation -= rotationAmount;
        }

        if(Math.abs(targetMoveRotation - moveRotation) < Math.abs(moveRotation - targetMoveRotation)) {
            moveRotation += moveRatio * (targetMoveRotation - moveRotation);
        } else {
            moveRotation -= moveRatio * (moveRotation - targetMoveRotation);
        }

        keyPress = -1;
        int xDir = 0;
        int yDir = 0;
        if(in.isKeyDown(Keyboard.KEY_A)) { --xDir; keyPress = Keyboard.KEY_A; }
        if(in.isKeyDown(Keyboard.KEY_D)) { ++xDir; keyPress = Keyboard.KEY_D; }
        if(in.isKeyDown(Keyboard.KEY_W)) { --yDir; keyPress = Keyboard.KEY_W; }
        if(in.isKeyDown(Keyboard.KEY_S)) { ++yDir; keyPress = Keyboard.KEY_S; }

        float transX = (float)Math.sin(Math.toRadians(moveRotation)) * (yDir * moveSpeed * deltaSeconds);
        float transY = (float)Math.cos(Math.toRadians(moveRotation)) * (yDir * moveSpeed * deltaSeconds);

        transX += (float)Math.sin(Math.toRadians(moveRotation + 90)) * (xDir * moveSpeed * deltaSeconds);
        transY += (float)Math.cos(Math.toRadians(moveRotation + 90)) * (xDir * moveSpeed * deltaSeconds);

        x += transX;
        y += transY;

        if(in.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            Vector mousePos = cam.screenToWorld(in.getMouseX(), in.getMouseY());
            lookRotation = (float)mousePos.subtract(new Vector(x, y)).getRotation(); //+ cam.getRotation();
            playerSprite.setRotation(lookRotation);
        }

        if(in.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            actionTime = 0;
            state = State.ATTACKING;
        } else if (in.isKeyPressed(Input.KEY_LSHIFT) && (xDir != 0 || yDir != 0)) {
            rollDir = new Vector(xDir, yDir);

            actionTime = 0;
            state = State.ROLLING;
        }
    }

    private void attacking(float deltaSeconds) {
        if(actionTime > attackTimeMax) state = State.FREE;
        actionTime += deltaSeconds;
    }

    private void rolling(float deltaSeconds) {
        float transX = (float)Math.sin(Math.toRadians(moveRotation)) * (rollDir.getY() * rollSpeed * deltaSeconds);
        float transY = (float)Math.cos(Math.toRadians(moveRotation)) * (rollDir.getY() * rollSpeed * deltaSeconds);

        transX += (float)Math.sin(Math.toRadians(moveRotation + 90)) * (rollDir.getX() * rollSpeed * deltaSeconds);
        transY += (float)Math.cos(Math.toRadians(moveRotation + 90)) * (rollDir.getX() * rollSpeed * deltaSeconds);

        x += transX;
        y += transY;

        if(actionTime > rollTimeMax) state = State.FREE;
        actionTime += deltaSeconds;
    }

    public boolean hitBy(Player other) {
        if(other.state == State.ATTACKING && state != State.ROLLING) {
            Vector ourPos = new Vector(x, y);
            Vector otherPos = new Vector(other.x, other.y);
            return otherPos.add(
                    new Vector(0, 0).setRotation(other.lookRotation).setLength(attackOffset)
            ).distance(ourPos) < attackRadius;
        }
        return false;
    }

    public void damage(int amount) {
        state = State.FREE;
        healthNUM -= amount;
    }

    public int getKeyPress() { return this.keyPress; }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }

    public float getY() { return y; }
    public void setY(float y) {
        this.y = y;
    }

    public void setID(int id) { this.ID = id; }
    public int getID() { return this.ID; }

    public void setHealthNUM(int health) { this.healthNUM = health; }
    public int getHealthNUM() { return this.healthNUM; }

    public void setArmorPlates(int plates) { this.armorPlates = plates; }
    public int getArmorPlates() { return this.armorPlates; }

    public void render() { playerAnimation.draw(x - width / 2 ,y - height / 2);
    }
    public void render(float x, float y) { playerAnimation.draw(x, y);}

    public float getMoveRotation() {
        return this.moveRotation;
    }

}
