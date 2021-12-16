package lotsofyou;

import jig.Vector;
import org.lwjgl.examples.spaceinvaders.Sprite;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
    private final float attackOffset = 5.0f;
    private final float attackRadius = 10.0f;

    private Vector rollDir;

    enum State {
        FREE,
        ATTACKING,
        ROLLING
    }

    private State state;

    ArrayList<Integer> hitPlayers;

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
        this.state = State.FREE;
        this.hitPlayers = new ArrayList<>();
        keyPress = -1;
        ID = -99;
    }

    public Player(float x, float y, int id) {
        this.x = x;
        this.y = y;
        ID = id;

        this.width = 6;
        this.height = 3;
        this.lookRotation = 0;
        this.moveRotation = 0;
        this.targetMoveRotation = 0;
        this.actionTime = 0;
        this.rollDir = new Vector(0, 0);
        this.input = new PlayerInput();
        this.hitPlayers = new ArrayList<>();

        this.state = State.FREE;
    }

    void update(float delta) {
        float deltaSeconds = delta / 1000;

        switch (state) {
            case FREE -> free(deltaSeconds);
            case ROLLING -> rolling(deltaSeconds);
            case ATTACKING -> attacking(deltaSeconds);
        }
    }

    void setPlayerInput(PlayerInput input) {
        this.input = new PlayerInput(input);
    }

    private void free(float deltaSeconds) {
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

        lookRotation = input.lookRotation;

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
        if(actionTime > attackTimeMax) {
            hitPlayers.clear();
            state = State.FREE;
        }
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

    public void hit(Player other) {
        hitPlayers.add(other.getID());
    }

    public boolean canHit(Player other) {
        return !hitPlayers.contains(other.getID());
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

    public void render(PlayerInput in) {
        playerSprite.setRotation(lookRotation);
        playerSprite.draw(x - width / 2 ,y - height / 2);
    }
    public void render(float x, float y) { playerSprite.draw(x, y);}

    public float getMoveRotation() {
        return this.moveRotation;
    }

    public PlayerState getPlayerState() {
        return new PlayerState(x, y, lookRotation, moveRotation, targetMoveRotation, healthNUM, armorPlates, actionTime, rollDir, state);
    }

    public void setPlayerState(PlayerState targetState) {
        x = targetState.x;
        y = targetState.y;
        lookRotation = targetState.lookRotation;
        moveRotation = targetState.moveRotation;
        targetMoveRotation = targetState.targetMoveRotation;
        healthNUM = targetState.health;
        armorPlates = targetState.armorPlates;
        actionTime = targetState.actionTime;
        rollDir = new Vector(targetState.rollX, targetState.rollY);
        state = State.values()[targetState.state];
    }

    public void drawDebug(Graphics g, Camera cam) {
        if(state == State.ATTACKING) {
            System.out.println("Cam Pos: " + cam.getPos().getX() + ", " + cam.getPos().getY());
            Vector worldPos = new Vector(x, y).add(new Vector(5, 0).setRotation(lookRotation).setLength(attackOffset));
            System.out.println("World Pos: " + worldPos.getX() + ", " + worldPos.getY());
            float viewableAttackRadius = attackRadius * cam.getScale();
            g.fillOval((worldPos.getX() - attackRadius / 2 - cam.getPos().getX()) * cam.getScale(), (worldPos.getY() - attackRadius / 2 - cam.getPos().getY()) * cam.getScale(), viewableAttackRadius, viewableAttackRadius);
        }
    }
}
