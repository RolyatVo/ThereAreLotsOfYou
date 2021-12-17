package lotsofyou;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class UprightSprite extends Image {

    private Camera renderCam;

    public UprightSprite(String ref, Camera cam) throws SlickException {
        super(ref);
        this.renderCam = cam;
        setFilter(FILTER_NEAREST);
    }

    @Override
    public void draw(float x, float y) {
        float drawX = x + (float)getWidth() / 2;
        float drawY = y + getHeight();

        float centerOffsetX = drawX - renderCam.getX() - (float)renderCam.getWidth() / 2;
        float centerOffsetY = drawY - renderCam.getY() - (float)renderCam.getHeight() / 2;

        double cs = Math.cos(Math.toRadians(renderCam.getRotation()));
        double sn = Math.sin(Math.toRadians(renderCam.getRotation()));

        float newCenterOffsetX = (float)(centerOffsetX * cs - centerOffsetY * sn) * renderCam.getScale();
        float newCenterOffsetY = (float)(centerOffsetX * sn + centerOffsetY * cs) * renderCam.getScale();
        float finalDrawPosX = newCenterOffsetX + renderCam.getWidth() / 2;
        float finalDrawPosY = newCenterOffsetY + renderCam.getHeight() / 2;

        super.getScaledCopy(renderCam.getScale()).draw(finalDrawPosX, finalDrawPosY);
    }
}
