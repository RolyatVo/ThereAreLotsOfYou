package lotsofyou;

import jig.Vector;
import org.lwjgl.examples.spaceinvaders.Sprite;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Tilemap {
    private HashMap<Character, Tile> tiles;

    private ArrayList<Tile> map;


    public Tilemap(HashMap<Character, Tile> tiles_) {
        this.tiles = tiles_;
        this.map = new ArrayList<>();
    }

    public void loadFromFile(String targetFile) {
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        //https://stackoverflow.com/questions/5868369/how-can-i-read-a-large-text-file-line-by-line-using-java
        try (BufferedReader br = new BufferedReader(new FileReader(targetFile))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                int col = 0;
                for(char c: line.toCharArray()) {
                    if(c == 'S') {
                        Collectible.addCollectible(Collectible.Type.SWORD, new Vector(16 * col, 16 * lineNum));
                    }
                    else if (c == 'A') {
                        Collectible.addCollectible(Collectible.Type.ARMOR, new Vector(16 * col, 16 * lineNum));
                    }
                    else if(c != ' ') {
                        Tile cpy = new Tile(tiles.get(c));
                        cpy.setPos(new Vector(16 * col, 16 * lineNum));
                        map.add(cpy);
                    }

                    ++col;
                }
                ++lineNum;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw() {
        for(Tile t : map) {
            t.draw();
        }
    }

    public ArrayList<Tile> getMap() {
        return this.map;
    }
}
