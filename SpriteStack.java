package lotsofyou;

import jig.Vector;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.pushingpixels.substance.api.colorscheme.TerracottaColorScheme;

import java.util.PriorityQueue;

public class SpriteStack extends SpriteSheet {

    private Camera renderCam;
    private Image imgArr[];
    private Image scaledArr[];
    private float currScale;

    private int width;
    private int height;

    static final PriorityQueue<ToDrawSpriteStack> toRender = new PriorityQueue<>();

    static void doDrawAll() {
        int i = 0;
        while(!toRender.isEmpty()) {
            ToDrawSpriteStack t = toRender.poll();
            if(t.minY > 0) t.draw();
        }
    }

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

    private class ToDrawSpriteStack implements Comparable<ToDrawSpriteStack> {
        private float minY;
        private Vector[] corners;
        private SpriteStack target;

        public ToDrawSpriteStack(float minY, SpriteStack target, Vector[] corners) {
            this.minY = minY;
            this.target = target;
            this.corners = corners;
        }

        public void draw() {
            target.doWarpedDraw(corners);
        }

        @Override
        public int compareTo(ToDrawSpriteStack o) {
            return (int)((minY - o.minY) * 1000);
        }
    }

    public SpriteStack(SpriteStack other) throws SlickException {
        super(other.ref, other.width, other.height);
        this.setRotation(other.getRotation());
        this.renderCam = other.renderCam;
        this.imgArr = other.imgArr;
        this.scaledArr = other.scaledArr;
        this.width = other.width;
        this.height = other.height;
        this.currScale = other.currScale;
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

        for(int i = 0; i != 4; ++i) {
            corners[i] = new Vector(corners[i].getX() * renderCam.getScale(), corners[i].getY() * renderCam.getScale());
        }

//        float minY = corners[0].getY();
//        for(int i = 1; i != 4; ++i) {
//            if(corners[i].getY() < minY) {
//                minY = corners[i].getY();
//            }
//        }

        float maxY = corners[0].getY();
        for(int i = 1; i != 4; ++i) {
            if(corners[i].getY() > maxY) {
                maxY = corners[i].getY();
            }
        }
//        float minX = corners[0].getX();
//        for(int i = 1; i != 4; ++i) {
//            if(corners[i].getX() < minX) {
//                minX = corners[i].getX();
//            }
//        }
//
//        float maxX = corners[0].getX();
//        for(int i = 1; i != 4; ++i) {
//            if(corners[i].getX() > maxX) {
//                maxX = corners[i].getX();
//            }
//        }

//        Rectangle bounding = new Rectangle(minX, minY, maxX - minX, maxY - minY);
//        Rectangle cameraBounding = new Rectangle(renderCam.getX(), renderCam.getY(), renderCam.getRenderWidth(), renderCam.getRenderHeight());

//        if(bounding.intersects(cameraBounding)) {
            toRender.add(new ToDrawSpriteStack(maxY, this, corners));
//        }
//        int offset = 0;
//        for(int i = imgArr.length - 1; i != -1; --i) {
//            imgArr[i].drawWarped(
//                    corners[0].getX() * renderCam.getScale(), (corners[0].getY() - offset) * renderCam.getScale(),
//                    corners[1].getX() * renderCam.getScale(), (corners[1].getY() - offset) * renderCam.getScale(),
//                    corners[3].getX() * renderCam.getScale(), (corners[3].getY() - offset) * renderCam.getScale(),
//                    corners[2].getX() * renderCam.getScale(), (corners[2].getY() - offset) * renderCam.getScale()
//            );
//            ++offset;
//        }
    }

    public void doWarpedDraw(Vector[] corners) {
        int offset = 0;
        for(int i = imgArr.length - 1; i != -1; --i) {
            imgArr[i].drawWarped(
                    corners[0].getX(), corners[0].getY() - (offset * renderCam.getScale()),
                    corners[1].getX(), corners[1].getY() - (offset * renderCam.getScale()),
                    corners[3].getX(), corners[3].getY() - (offset * renderCam.getScale()),
                    corners[2].getX(), corners[2].getY() - (offset * renderCam.getScale())
            );
            ++offset;
        }
    }

    public int getFrameWidth() {
        return this.width;
    }

    public int getFrameHeight() {
        return this.height;
    }
}
