package lotsofyou;

import jig.Vector;
import org.newdawn.slick.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerInput {
    boolean updated;

    boolean up;
    boolean down;
    boolean left;
    boolean right;
    boolean rotateRight;
    boolean rotateLeft;
    boolean roll;
    boolean attack;
    float lookRotation;

    public PlayerInput() {
        this.updated = false;
        this.up = false;
        this.down = false;
        this.left = false;
        this.right = false;
        this.rotateRight = false;
        this.rotateLeft = false;
        this.roll = false;
        this.attack = false;
        this.lookRotation = 0.0f;
    }

    public PlayerInput(PlayerInput other) {
        this.updated = other.updated;
        this.up = other.up;
        this.down = other.down;
        this.left = other.left;
        this.right = other.right;
        this.rotateLeft = other.rotateLeft;
        this.rotateRight = other.rotateRight;
        this.roll = other.roll;
        this.attack = other.attack;
        this.lookRotation = other.lookRotation;
    }

    synchronized void update(Input in, Camera cam, Vector playerPos) {
        up = in.isKeyDown(Input.KEY_W);
        down = in.isKeyDown(Input.KEY_S);
        left = in.isKeyDown(Input.KEY_A);
        right = in.isKeyDown(Input.KEY_D);

        rotateLeft = in.isKeyPressed(Input.KEY_Q);
        rotateRight = in.isKeyPressed(Input.KEY_E);

        roll = in.isKeyPressed(Input.KEY_LSHIFT);
        attack = in.isMousePressed(Input.MOUSE_LEFT_BUTTON);
        if(attack || in.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            Vector mousePos = cam.screenToWorld(in.getMouseX(), in.getMouseY());
            lookRotation = (float) mousePos.subtract(playerPos).getRotation();
        }

        updated = true;
    }

    synchronized boolean isUpdated() {
        return updated;
    }

    synchronized void send(DataOutputStream outputStream) throws IOException {
        outputStream.writeBoolean(up);
        outputStream.writeBoolean(down);
        outputStream.writeBoolean(left);
        outputStream.writeBoolean(right);
        outputStream.writeBoolean(rotateLeft);
        outputStream.writeBoolean(rotateRight);
        outputStream.writeBoolean(roll);
        outputStream.writeBoolean(attack);
        outputStream.writeFloat(lookRotation);
    }

    synchronized void read(DataInputStream inputStream) throws IOException {
        up = inputStream.readBoolean();
        down = inputStream.readBoolean();
        left = inputStream.readBoolean();
        right = inputStream.readBoolean();
        rotateLeft = inputStream.readBoolean();
        rotateRight = inputStream.readBoolean();
        roll = inputStream.readBoolean();
        attack = inputStream.readBoolean();
        lookRotation = inputStream.readFloat();
    }
}
