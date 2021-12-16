package lotsofyou;

import jig.Vector;
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

    @Override
    public void draw() {
        draw(0, 0);
    }

    @Override
    public void draw(float x, float y) {


        Vector corners[] = new Vector[4];
        for(int i = 0; i != 4; ++i) {
            Vector centerOffset = new Vector((i % 2) * height - (float)height / 2,
                                             (i / 2) * width - (float)width / 2);

            corners[i] = centerOffset.rotate(getRotation()).add(new Vector((float)width / 2, (height) / 2)).add(new Vector(x, y));
        }

        double cs = Math.cos(Math.toRadians(renderCam.getRotation()));
        double sn = Math.sin(Math.toRadians(renderCam.getRotation()));

        for(int i = 0; i != 4; ++i) {
            Vector halfRenderRes = renderCam.getRenderRes().scale(0.5f);
            Vector centerOffset = corners[i].subtract(new Vector(renderCam.getX(), renderCam.getY())).subtract(
                    halfRenderRes);
            float newCenterOffsetX = (float)(centerOffset.getX() * cs - centerOffset.getY() * sn);
            float newCenterOffsetY = (float)(centerOffset.getX() * sn + centerOffset.getY() * cs);
            corners[i] = new Vector(newCenterOffsetX + halfRenderRes.getX(), newCenterOffsetY + halfRenderRes.getY());
        }

        int offset = 0;
        for(int i = imgArr.length - 1; i != -1; --i) {
            imgArr[i].drawWarped(
                    corners[0].getX() * renderCam.getScale(), (corners[0].getY() - offset) * renderCam.getScale(),
                    corners[1].getX() * renderCam.getScale(), (corners[1].getY() - offset) * renderCam.getScale(),
                    corners[3].getX() * renderCam.getScale(), (corners[3].getY() - offset) * renderCam.getScale(),
                    corners[2].getX() * renderCam.getScale(), (corners[2].getY() - offset) * renderCam.getScale()
            );
            ++offset;
        }
    }
}
