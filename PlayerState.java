package lotsofyou;

import jig.Vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerState {
    float x;
    float y;
    float lookRotation;
    float moveRotation;
    float targetMoveRotation;
    int health;
    int armorPlates;
    float actionTime;
    int rollX;
    int rollY;
    int state;

    public PlayerState() {};

    public PlayerState(float x_, float y_, float lookRotation_, float moveRotation_, float targetMoveRotation_, int health_, int armorPlates_,
                       float actionTime_, Vector rollDir, Player.State state_) {
        this.x = x_;
        this.y = y_;
        this.lookRotation = lookRotation_;
        this.moveRotation = moveRotation_;
        this.targetMoveRotation = targetMoveRotation_;
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
        dataOutputStream.writeFloat(lookRotation);
        dataOutputStream.writeFloat(moveRotation);
        dataOutputStream.writeFloat(targetMoveRotation);

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
        lookRotation = dataInputStream.readFloat();
        moveRotation = dataInputStream.readFloat();
        targetMoveRotation = dataInputStream.readFloat();

        health = dataInputStream.readInt();
        armorPlates = dataInputStream.readInt();

        actionTime = dataInputStream.readFloat();

        rollX = dataInputStream.readInt();
        rollY = dataInputStream.readInt();
        state = dataInputStream.readInt();
    }
}
