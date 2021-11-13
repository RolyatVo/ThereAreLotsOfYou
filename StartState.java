package lotsofyou;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.Random;

public class StartState extends BasicGameState {

  SpriteStack box;
  UprightSprite grass;
  Camera cam = new Camera(960, 960);
  int frame = 0;

  float x[] = new float[16];
  float y[] = new float[16];

  @Override
  public int getID() {
    return LotsOfYouGame.STARTSTATE;
  }

  @Override
  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    box = new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
    grass = new UprightSprite(LotsOfYouGame.TEST_GRASS, cam);
    cam.setScale(5);
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

    int xDir = 0;
    int yDir = 0;
    if(Keyboard.isKeyDown(Keyboard.KEY_A)) --xDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_D)) ++xDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_W)) --yDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_S)) ++yDir;

    int rotDir = 0;
    if(Keyboard.isKeyDown(Keyboard.KEY_J)) ++rotDir;
    if(Keyboard.isKeyDown(Keyboard.KEY_K)) --rotDir;
    cam.rotate(rotDir);

    float rotation = cam.getRotation();
    float transX = (float)Math.sin(Math.toRadians(rotation)) * yDir;
    float transY = (float)Math.cos(Math.toRadians(rotation)) * yDir;

    cam.move(transX, transY);
  }

  @Override
  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

  }
}
