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
    private float prevXVel;
    private float prevYVel;
    private float xVel;
    private float yVel;

    private static final float moveSpeed = 50.0f;
    private static final float rollSpeed = 240.0f;

    private static final int width = 3;
    private static final int height = 3;

    //the direction the player is looking
    private float lookRotation;
    //the forward movement direction directions
    private float moveRotation;
    private float targetMoveRotation;

    private int keyPress;
    private int ID;

    SpriteStack playerSprite;
    Animations animations;
    SpriteStackAnimation currentAnimation;

    PlayerInput input;


    private int healthNUM = 50;
    private int armorPlates = 0;

    private static final float rotationAmount = 30.0f;
    private static final float moveRatio = 0.08f;

    private float actionTime;
    private static final float rollTimeMax = 0.5f;
    private static final float attackTimeMax = 0.5f;
    private final float attackOffset = 5.0f;
    private final float attackRadius = 10.0f;

    private Vector rollDir;

    enum State {
        FREE,
        ATTACKING,
        ROLLING
    }

    private State prevRenderState;
    private State state;

    ArrayList<Integer> hitPlayers;

    public Player(SpriteStack sprite, float x, float y) {
        playerSprite = sprite;
        this.x = x;
        this.y = y;
        this.lookRotation = 0;
        this.moveRotation = 0;
        this.targetMoveRotation = 0;
        this.actionTime = 0;
        this.rollDir = new Vector(0, 0);
        this.input = new PlayerInput();
        this.state = State.FREE;
        this.prevRenderState = this.state;
        this.hitPlayers = new ArrayList<>();
        this.xVel = 0;
        this.yVel = 0;
        keyPress = -1;
        ID = -99;
    }
    public Player(Animations animations, float x, float y) {
        this.x = x;
        this.y = y;
        this.lookRotation = 0;
        this.moveRotation = 0;
        this.targetMoveRotation = 0;
        this.actionTime = 0;
        this.rollDir = new Vector(0, 0);
        this.input = new PlayerInput();
        this.state = State.FREE;
        this.animations = animations;
        this.xVel = 0;
        this.yVel = 0;
        currentAnimation = animations.walkingAnimation;
        keyPress = -1;
        ID = -99;
    }



    public Player(float x, float y, int id) {
        this.x = x;
        this.y = y;
        ID = id;

        this.lookRotation = 0;
        this.moveRotation = 0;
        this.targetMoveRotation = 0;
        this.actionTime = 0;
        this.rollDir = new Vector(0, 0);
        this.input = new PlayerInput();
        this.hitPlayers = new ArrayList<>();

        this.xVel = 0;
        this.yVel = 0;

        this.state = State.FREE;
    }

    void update(float delta, Tilemap map) {

        float deltaSeconds = delta / 1000;

        switch (state) {
            case FREE -> free(deltaSeconds);
            case ROLLING -> rolling(deltaSeconds);
            case ATTACKING -> attacking(deltaSeconds);
        }

        Rectangle prevCollider = new Rectangle(x - 3, y - 3, 6, 6);
        Rectangle newCollider = new Rectangle(x - 3 + xVel, y - 3 + yVel, 6, 6);
//        System.out.println("Prev new position: " + newCollider.getX() + ", " + newCollider.getY());
        for(Tile t : map.getMap()) {
            if(t.intersects(newCollider)) {
                for(Rectangle r_ : t.getColliders()) {
                    Rectangle r = new Rectangle(r_.getX() + t.getX(), r_.getY() + t.getY(), r_.getWidth(), r_.getHeight());
                    if(r.intersects(newCollider)) {
                        System.out.println("Us " + newCollider.getX() + ", " + newCollider.getY());
                        System.out.println("Intersecting with " + r.getX() + ", " + r.getY());
                        //the amount into the thing we've gone (subtract to undo)
                        float xOverlap = 0, yOverlap = 0;

                        float prevRight = prevCollider.getX() + prevCollider.getWidth();
                        float prevBottom = prevCollider.getY() + prevCollider.getHeight();

                        float newRight = newCollider.getX() + newCollider.getWidth();
                        float newBottom = newCollider.getY() + newCollider.getHeight();

                        float tileRight = r.getX() + r.getWidth();
                        float tileBottom = r.getY() + r.getHeight();


                        //collided while moving right
                        if (prevRight <= r.getX() && newRight > r.getX()) {
                            xOverlap = newRight - r.getX();
                        }

                        //collided while moving left
                        if (prevCollider.getX() >= tileRight && newCollider.getX() < tileRight) {
                            xOverlap = newCollider.getX() - tileRight;
                        }

                        //collided while moving down
                        if (prevBottom <= r.getY() && newBottom > r.getY()) {
                            yOverlap = newBottom - r.getY();
                        }

                        //collided while moving up
                        if (prevCollider.getY() >= tileBottom && newCollider.getY() < tileBottom) {
                            yOverlap = newCollider.getY() - tileBottom;
                        }

                        //corner
                        if (xOverlap != 0 && yOverlap != 0) {
                            //if we just started moving in the x axis, or we're moving slower
                            if (xVel < yVel || prevXVel == 0) {
                                newCollider.setX(Math.round(newCollider.getX() - xOverlap));
                                xVel = 0;
                            } else {
                                newCollider.setY(Math.round(newCollider.getY() - yOverlap));
                                yVel = 0;
                            }
                        }
                        //just x
                        else if (xOverlap != 0) {
                            newCollider.setX(Math.round(newCollider.getX() - xOverlap));
                            System.out.println("xOverlap: " + xOverlap);
                            xVel = 0;
                        }
                        //just y
                        else if (yOverlap != 0) {
                            newCollider.setY(Math.round(newCollider.getY() - yOverlap));
                            System.out.println("yOverlap: " + yOverlap);
                            yVel = 0;
                        }

                        System.out.println("New Us " + newCollider.getX() + ", " + newCollider.getY());
                    }
                }
            }
        }

//        System.out.println("New new position: " + newCollider.getX() + ", " + newCollider.getY());

        x = newCollider.getX() + 3;
        y = newCollider.getY() + 3;

        prevXVel = xVel;
        prevYVel = yVel;
    }

    void updateAnimation(int delta) {
        currentAnimation.update((int)delta);
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

        xVel = (float)Math.sin(Math.toRadians(moveRotation)) * (yDir * moveSpeed * deltaSeconds);
        yVel = (float)Math.cos(Math.toRadians(moveRotation)) * (yDir * moveSpeed * deltaSeconds);

        xVel += (float)Math.sin(Math.toRadians(moveRotation + 90)) * (xDir * moveSpeed * deltaSeconds);
        yVel += (float)Math.cos(Math.toRadians(moveRotation + 90)) * (xDir * moveSpeed * deltaSeconds);

      //  if(playerSprite != null) playerSprite.setRotation(input.lookRotation);
      //  if(playerAnimation != null) playerAnimation.setRotation(input.lookRotation);

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
        xVel = (float)Math.sin(Math.toRadians(moveRotation)) * (rollDir.getY() * rollSpeed * deltaSeconds);
        yVel = (float)Math.cos(Math.toRadians(moveRotation)) * (rollDir.getY() * rollSpeed * deltaSeconds);

        xVel += (float)Math.sin(Math.toRadians(moveRotation + 90)) * (rollDir.getX() * rollSpeed * deltaSeconds);
        yVel += (float)Math.cos(Math.toRadians(moveRotation + 90)) * (rollDir.getX() * rollSpeed * deltaSeconds);

        if(actionTime > rollTimeMax) { state = State.FREE; }
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


    public void render() {
        if(state != prevRenderState) {
            if(state == State.FREE) {
                if(xVel != 0 || yVel != 0) {
                    currentAnimation = animations.walkingAnimation;
                } else {
                    currentAnimation = animations.idleAnimation;
                }
            }
            else if(state == State.ROLLING) {
                currentAnimation = animations.rollingAnimation;
            }
            currentAnimation.setFrame(0);
        }

        if(state == State.FREE) {
            if(xVel != 0 || yVel != 0) {
                currentAnimation = animations.walkingAnimation;
            } else {
                currentAnimation = animations.idleAnimation;
            }
        }

        if(state == State.FREE || state == State.ATTACKING) {
            currentAnimation.setRotation(lookRotation);
        }
        else if (state == State.ROLLING) {
            currentAnimation.setRotation((float)rollDir.getRotation());
        }

        currentAnimation.draw(x - currentAnimation.getFrameWidth() / 2 ,y - currentAnimation.getFrameHeight() / 2);
        prevRenderState = state;
    }

    public float getMoveRotation() {
        return this.moveRotation;
    }

    public PlayerState getPlayerState() {
        return new PlayerState(x, y, xVel, yVel, prevXVel, prevYVel, lookRotation, moveRotation, targetMoveRotation, healthNUM, armorPlates, actionTime, rollDir, state);
    }

    public void setPlayerState(PlayerState targetState) {
        x = targetState.x;
        y = targetState.y;
        xVel = targetState.xVel;
        yVel = targetState.yVel;
        prevXVel = targetState.prevXVel;
        prevYVel = targetState.prevYVel;
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

    public void collect(Collectible c) {
        System.out.println("Collected " + c.getType().name() + "!");
    }
}
