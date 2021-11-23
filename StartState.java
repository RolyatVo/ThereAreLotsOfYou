package lotsofyou;

import org.lwjgl.examples.spaceinvaders.Sprite;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.Random;

public class StartState extends BasicGameState {

  SpriteStack box;
  SpriteStack otherPlayers;
  UprightSprite grass;
  SpriteStack tree;
  Camera cam = new Camera(960, 960);
  int frame = 0;
  float zoom = 5;

  float x[] = new float[16];
  float y[] = new float[16];




  @Override
  public int getID() {
    return LotsOfYouGame.STARTSTATE;
  }

  @Override
  public void init(GameContainer container, StateBasedGame game) throws SlickException {
   // otherPlayers = new ArrayList<>();
    box = new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
    tree = new SpriteStack(LotsOfYouGame.TEST_TREE, 64, 64, cam);
    grass = new UprightSprite(LotsOfYouGame.TEST_GRASS, cam);
    otherPlayers = new SpriteStack(LotsOfYouGame.TEST_TREE2, 40, 40, cam);

    cam.setScale(5);
    serverConnect();
  }


  @Override
  public void enter(GameContainer container, StateBasedGame game) {

    Random rand = new Random();
    for(int i = 0; i != 16; ++ i) {
      x[i] = rand.nextInt(160);
      y[i] = rand.nextInt(160);
    }
  }

  @Override
  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    g.setBackground(new Color(83, 124, 68));

    ++frame;


    float posX = 960.0f / 2;
    float posY = 960.0f / 2;

    for(int i = 0; i != 10; ++i) {
      grass.draw(posX + x[i], posY + y[i]);
    }

    box.draw(posX, posY);
    box.draw(posX, posY + 45);
    otherPlayers.draw(0,0);
    tree.draw(posX + 32, posY + 32);

    int xDir = 0;
    int yDir = 0;
    if(Keyboard.isKeyDown(Keyboard.KEY_A)) --xDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_D)) ++xDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_W)) --yDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_S)) ++yDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_I)) zoom += (zoom > 8) ? 0 : 0.25f;
    if(Keyboard.isKeyDown(Keyboard.KEY_O)) zoom -= (zoom < 1.25) ? 0 : 0.25f;

    int rotDir = 0;
    if(Keyboard.isKeyDown(Keyboard.KEY_J)) ++rotDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_K)) --rotDir;
    cam.rotate(rotDir);
    cam.setScale(zoom);

    float rotation = cam.getRotation();
    float transX = (float)Math.sin(Math.toRadians(rotation)) * yDir;
    float transY = (float)Math.cos(Math.toRadians(rotation)) * yDir;

    cam.move(transX, transY);
  }

  @Override
  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

  }


  private Socket socket;
  private int playerID;
  private ReadServer rsRunnable;
  private WriteServer wsRunnable;

  private void serverConnect() {
    try {
      socket = new Socket("localhost", 55555);
      DataInputStream in = new DataInputStream(socket.getInputStream());
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());

      playerID = in.readInt();
      System.out.println("You are player #" + playerID);

      rsRunnable = new ReadServer(in);
      wsRunnable = new WriteServer(out);

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private class ReadServer implements Runnable {
    private DataInputStream dataIN;

    public ReadServer (DataInputStream in) {
      dataIN = in;
      System.out.println("Read to Server runnable created!!");


    }
    public void run() {

    }
  }
  private class WriteServer implements Runnable {
    private DataOutputStream dataOUT;

    public WriteServer (DataOutputStream out) {
      dataOUT = out;
      System.out.println("Write to Server runnable created!!");


    }
    public void run() {
//      try {
//        while(true) {
//
//        }
//      } catch (IOException ex) {
//        ex.printStackTrace();
//      }
    }
  }
}
