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
    PlayerInput input;

    private int healthNUM = 100;
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
        this.input = new PlayerInput();
        keyPress = -1;
        ID = -99;
        state = State.FREE;
    }

    public Player(float x, float y, int id) {
        this.x = x;
        this.y = y;
        ID = id;

    }

    void update(float delta, Camera cam) {
        float deltaSeconds = delta / 1000;

        switch (state) {
            case FREE -> free(deltaSeconds, cam);
            case ROLLING -> rolling(deltaSeconds);
            case ATTACKING -> attacking(deltaSeconds);
        }
    }

    void setPlayerInput(PlayerInput input) {
        this.input = new PlayerInput(input);
    }

    private void free(float deltaSeconds, Camera cam) {
        if(input.rotateLeft) {
            targetMoveRotation += rotationAmount;
        } else if (input.rotateRight) {
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
        if(input.left) { --xDir; keyPress = Keyboard.KEY_A; }
        if(input.right) { ++xDir; keyPress = Keyboard.KEY_D; }
        if(input.up) { --yDir; keyPress = Keyboard.KEY_W; }
        if(input.down) { ++yDir; keyPress = Keyboard.KEY_S; }

        float transX = (float)Math.sin(Math.toRadians(moveRotation)) * (yDir * moveSpeed * deltaSeconds);
        float transY = (float)Math.cos(Math.toRadians(moveRotation)) * (yDir * moveSpeed * deltaSeconds);

        transX += (float)Math.sin(Math.toRadians(moveRotation + 90)) * (xDir * moveSpeed * deltaSeconds);
        transY += (float)Math.cos(Math.toRadians(moveRotation + 90)) * (xDir * moveSpeed * deltaSeconds);

        x += transX;
        y += transY;

        playerSprite.setRotation(input.lookRotation);

        if(input.attack) {
            actionTime = 0;
            state = State.ATTACKING;
        } else if (input.roll && (xDir != 0 || yDir != 0)) {
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

    public void render() {
        playerSprite.draw(x - width / 2 ,y - height / 2);
    }
    public void render(float x, float y) { playerSprite.draw(x, y);}

    public float getMoveRotation() {
        return this.moveRotation;
    }

}
