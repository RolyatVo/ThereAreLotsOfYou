package lotsofyou;

import org.lwjgl.examples.spaceinvaders.Sprite;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

import java.awt.*;
import java.util.ArrayList;

public class SpriteStackAnimation {
    private final ArrayList<SSFrame> frames = new ArrayList<>();
    boolean isPlaying;
    float rotation;

//    private Camera cam;

    private int currentFrame = -1;
    private int currentDuration = -1;

    public SpriteStackAnimation(SpriteStack[] frames, int duration) {
        currentDuration = duration;
        currentFrame = 0;
        isPlaying = true;
        rotation = 0;
//        this.cam = cam;


        for (SpriteStack frame : frames) {
            addFrame(frame, duration);
        }
    }

    public void draw(float x, float y) {
        SSFrame current = getCurrentSSFrame();

        if(current != null)
            getCurrentSSFrame().spritestack.draw(x , y);

    }
    public void setFrame(int index) { this.currentFrame = index; }

    public void stop() { this.isPlaying = false; }
    public void play() { this.isPlaying = true; }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        getCurrentSSFrame().spritestack.setRotation(this.rotation);
    }

    public void update(int delta)  {

        if(isPlaying) {
            if (this.currentDuration < 0) {
                ++currentFrame;
                currentFrame %= frames.size();

                this.currentDuration = getCurrentSSFrame().duration;
            }
            this.currentDuration -= delta;
        }
    }



    private SSFrame getCurrentSSFrame() {
        return frames.get(currentFrame);
    }


    public void addFrame(SpriteStack ss, int duration) {
        if(duration != 0) {
            frames.add(new SSFrame(ss, duration));
        }
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getFrameWidth() {
        return getCurrentSSFrame().spritestack.getFrameWidth();
    }
    public float getFrameHeight() {
        return getCurrentSSFrame().spritestack.getFrameHeight();
    }

    private class SSFrame {
        public SpriteStack spritestack;
        public int duration;

        public SSFrame(SpriteStack frame, int duration) {
            this.spritestack = frame;
            this.duration = duration;
        }
    }
}
