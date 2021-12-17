package lotsofyou;

import jig.Vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerState {
    float x;
    float y;
    float xVel;
    float yVel;
    float prevXVel;
    float prevYVel;
    float lookRotation;
    float attackRotation;
    float targetLookRotation;
    int health;
    int armorPlates;
    float actionTime;
    int rollX;
    int rollY;
    int state;

    public PlayerState() {};

    public PlayerState(float x_, float y_, float xVel, float yVel, float prevXVel, float prevYVel,
                       float lookRotation_, float attackRotation_, float targetLookRotation_, int health_, int armorPlates_,
                       float actionTime_, Vector rollDir, Player.State state_) {
        this.x = x_;
        this.y = y_;
        this.xVel = xVel;
        this.yVel = yVel;
        this.prevXVel = prevXVel;
        this.prevYVel = prevYVel;
        this.lookRotation = lookRotation_;
        this.attackRotation = attackRotation_;
        this.targetLookRotation = targetLookRotation_;
        this.health = health_;
        this.armorPlates = armorPlates_;
        this.actionTime = actionTime_;
        this.rollX = (int)rollDir.getX();
        this.rollY = (int)rollDir.getY();
        this.state = state_.ordinal();
    }

    void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeFloat(x);
        dataOutputStream.writeFloat(y);
        dataOutputStream.writeFloat(xVel);
        dataOutputStream.writeFloat(yVel);
        dataOutputStream.writeFloat(prevXVel);
        dataOutputStream.writeFloat(prevYVel);

        dataOutputStream.writeFloat(lookRotation);
        dataOutputStream.writeFloat(attackRotation);
        dataOutputStream.writeFloat(targetLookRotation);

        dataOutputStream.writeInt(health);
        dataOutputStream.writeInt(armorPlates);

        dataOutputStream.writeFloat(actionTime);

        dataOutputStream.writeInt(rollX);
        dataOutputStream.writeInt(rollY);
        dataOutputStream.writeInt(state);
    }

    void read(DataInputStream dataInputStream) throws IOException {
        x = dataInputStream.readFloat();
        y = dataInputStream.readFloat();
        xVel = dataInputStream.readFloat();
        yVel = dataInputStream.readFloat();
        prevXVel = dataInputStream.readFloat();
        prevYVel = dataInputStream.readFloat();

        lookRotation = dataInputStream.readFloat();
        attackRotation = dataInputStream.readFloat();
        targetLookRotation = dataInputStream.readFloat();

        health = dataInputStream.readInt();
        armorPlates = dataInputStream.readInt();

        actionTime = dataInputStream.readFloat();

        rollX = dataInputStream.readInt();
        rollY = dataInputStream.readInt();
        state = dataInputStream.readInt();
    }
}
