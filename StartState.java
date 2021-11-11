package lotsofyou;

import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class StartState extends BasicGameState {

  SpriteSheet box;
  int frame = 0;

  @Override
  public int getID() {
    return LotsOfYouGame.STARTSTATE;
  }

  @Override
  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    box = new SpriteSheet(LotsOfYouGame.TEST_BOX, 16, 16);
  }


  @Override
  public void enter(GameContainer container, StateBasedGame game) {

  }

  @Override
  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    ++frame;

    float posX = 1280.0f / 2 - box.getTextureWidth() * 5 / 2;
    float posY = 1280.0f / 2 - box.getTextureHeight() * 5 / 2;
    int imgCount = box.getVerticalCount();
    for(int i = 0; i != imgCount; ++i) {
      Image img = box.getSubImage(0, i).getScaledCopy(5);
      img.rotate(frame);
      img.draw(posX, posY - i * 5);
    }
  }

  @Override
  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

  }
}
