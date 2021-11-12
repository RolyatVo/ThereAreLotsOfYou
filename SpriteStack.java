package lotsofyou;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class SpriteStack extends SpriteSheet {

    private Camera renderCam;
    private Image imgArr[];
    private Image scaledArr[];
    private float currScale;

    private int width;
    private int height;

    public SpriteStack(String ref, int w, int h, Camera renderCam_) throws SlickException {
        super(ref, w, h);
        this.renderCam = renderCam_;

        int imgs = getVerticalCount();
        imgArr = new Image[imgs];
        scaledArr = new Image[imgs];
        for(int i = 0; i != imgs; ++i) {
            imgArr[i] = getSubImage(0, i);
            scaledArr[i] = imgArr[i];
        }
        this.currScale = 1.0f;

        this.width = w;
        this.height = h;
    }

    public void rescale(float scale) {
        for(int i = 0; i != imgArr.length; ++i) {
            scaledArr[i] = imgArr[i].getScaledCopy(scale);
        }
        this.currScale = scale;
    }

    public float getScaledWidth() {
        return scaledArr[0].getWidth();
    }

    public float getScaledHeight() {
        return scaledArr[0].getHeight();
    }

    @Override
    public void draw() {
        draw(0, 0);
    }

    @Override
    public void draw(float x, float y) {
        if(renderCam.getScale() != currScale) rescale(renderCam.getScale());

        float hypot2 = (float)Math.hypot(width, height) / 2;
        float w2 = (float)width / 2;
        float h2 = (float)height / 2;

        float screenPosX = x - renderCam.getX();
        float screenPosY = y - renderCam.getY();

        float centerOffsetX = screenPosX - renderCam.getWidth() / 2;
        float centerOffsetY = screenPosY - renderCam.getHeight() / 2;

        double cs = Math.cos(Math.toRadians(renderCam.getRotation()));
        double sn = Math.sin(Math.toRadians(renderCam.getRotation()));

        float newCenterOffsetX = (float)(centerOffsetX * cs - centerOffsetY * sn) * renderCam.getScale();
        float newCenterOffsetY = (float)(centerOffsetY * cs - centerOffsetX * sn) * renderCam.getScale();

        float drawPosX = (newCenterOffsetX + renderCam.getWidth() / 2) +
                (float)(Math.cos(Math.toRadians(renderCam.getRotation() + 45)) * hypot2 - w2) * renderCam.getScale();
        float drawPosY = (newCenterOffsetY + renderCam.getHeight() / 2) +
                (float)(Math.cos(Math.toRadians(renderCam.getRotation() - 45)) * hypot2 - h2) * renderCam.getScale();

        int offset = 0;
        for(Image i : scaledArr) {
            i.setRotation(renderCam.getRotation());
            i.draw(drawPosX, drawPosY - (offset++) * renderCam.getScale());
        }
    }
}
