package lotsofyou;

import jig.Vector;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Level {
    private static Tilemap tilemap;
    private static final Vector spawnCenter = new Vector(0, 0);
    private static final float spawnRadius = 100;

    public static void InitLevel(String targetLevel, Camera cam) {
        try {
            SpriteStack corner = new SpriteStack(LotsOfYouGame.WALL_CORNER, 16, 16, cam);
            SpriteStack cross_ = new SpriteStack(LotsOfYouGame.WALL_CROSS, 16, 16, cam);
            SpriteStack straight = new SpriteStack(LotsOfYouGame.WALL_STRAIGHT, 16, 16, cam);
            SpriteStack wall_t = new SpriteStack(LotsOfYouGame.WALL_T, 16, 16, cam);

            ArrayList<Rectangle> collider = new ArrayList<>();
            collider.add(new Rectangle(0, 0, 16, 16));

            Tile vert = new Tile(new SpriteStack(straight), collider);
            straight.setRotation(90);
            Tile horiz = new Tile(new SpriteStack(straight), collider);
            Tile bottom_right = new Tile(new SpriteStack(corner), collider);
            corner.rotate(90);
            Tile bottom_left = new Tile(new SpriteStack(corner), collider);
            corner.rotate(90);
            Tile top_left = new Tile(new SpriteStack(corner), collider);
            corner.rotate(90);
            Tile top_right = new Tile(new SpriteStack(corner), collider);
            Tile bottom = new Tile(new SpriteStack(wall_t), collider);
            wall_t.rotate(90);
            Tile left = new Tile(new SpriteStack(wall_t), collider);
            wall_t.rotate(90);
            Tile right = new Tile(new SpriteStack(wall_t), collider);
            wall_t.rotate(90);
            Tile top = new Tile(new SpriteStack(wall_t), collider);
            wall_t.rotate(90);
            Tile cross = new Tile(cross_, collider);

            HashMap<Character, Tile> tiles = new HashMap<>();
            tiles.put('|', vert);
            tiles.put('-', horiz);
            tiles.put('0', top_left);
            tiles.put('1', top_right);
            tiles.put('2', bottom_left);
            tiles.put('3', bottom_right);
            tiles.put('4', top);
            tiles.put('5', bottom);
            tiles.put('6', left);
            tiles.put('7', right);
            tiles.put('+', cross);

            tilemap = new Tilemap(tiles);
            //tilemap.loadFromFile("LotsOfYou/src/lotsofyou/levels/test.txt");
            Collectible.setCollectibleRenderCam(cam);
            tilemap.loadFromFile(targetLevel);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public static void InitLevel(String targetFile) {
        ArrayList<Rectangle> collider = new ArrayList<>();
        collider.add(new Rectangle(0, 0, 16, 16));

        Tile vert = new Tile(null, collider);
        Tile horiz = new Tile(null, collider);
        Tile bottom_right = new Tile(null, collider);
        Tile bottom_left = new Tile(null, collider);
        Tile top_left = new Tile(null, collider);
        Tile top_right = new Tile(null, collider);
        Tile bottom = new Tile(null, collider);
        Tile left = new Tile(null, collider);
        Tile right = new Tile(null, collider);
        Tile top = new Tile(null, collider);
        Tile cross = new Tile(null, collider);

        HashMap<Character, Tile> tiles = new HashMap<>();
        tiles.put('|', vert);
        tiles.put('-', horiz);
        tiles.put('0', top_left);
        tiles.put('1', top_right);
        tiles.put('2', bottom_left);
        tiles.put('3', bottom_right);
        tiles.put('4', top);
        tiles.put('5', bottom);
        tiles.put('6', left);
        tiles.put('7', right);
        tiles.put('+', cross);

        tilemap = new Tilemap(tiles);
        //tilemap.loadFromFile("LotsOfYou/src/lotsofyou/levels/test.txt");
        tilemap.loadFromFile(targetFile);
    }
    
    public static Tilemap getTilemap() {
        return tilemap;
    }

    public static void render() {
        tilemap.draw();
    }

    public static void prepareForSpawn(Collection<Player> players) {
        float step = 360.0f / players.size();
        Vector offset = new Vector(spawnRadius, 0);
        for(Player p : players) {
            Vector playerPos = spawnCenter.add(offset);
            p.setX(playerPos.getX());
            p.setY(playerPos.getY());
            offset = offset.rotate(step);
        }
    }

}
