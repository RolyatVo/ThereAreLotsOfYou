package lotsofyou;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.state.StateBasedGame;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * A Simple Game of Bounce.
 *
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 *
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 *
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 *
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 *
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 *
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 *
 *
 * @author wallaces
 *
 */
public class LotsOfYouGame extends StateBasedGame {

	public static final int STARTSTATE = 0;

	public static final String TEST_BOX = "lotsofyou/resource/box.png";
	public static final String TEST_GRASS = "lotsofyou/resource/grass_2.png";
	public static final String TEST_TREE = "lotsofyou/resource/tree.png";
	public static final String TEST_TREE2 = "lotsofyou/resource/tree2.png";
	public static final String PLAYER_TEST = "lotsofyou/resource/player_1.png";
	public static final String ARMOR_RSC = "lotsofyou/resource/shield.png";
	public static final String SWORD_RSC = "lotsofyou/resource/sword.png";

	public final int ScreenWidth;
	public final int ScreenHeight;

	public static final int INPUT_PACKET = 0;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 *
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public LotsOfYouGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		ResourceManager.filterMethod = ResourceManager.FILTER_NEAREST;
  }

	public int getScreenHeight() {
		return ScreenHeight;
	}
	public int getScreenWidth () {
		return ScreenWidth;
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
	  	addState(new StartState());

		ResourceManager.loadImage(TEST_BOX);
		ResourceManager.loadImage(TEST_GRASS);
		ResourceManager.loadImage(TEST_TREE);
		ResourceManager.loadImage(TEST_TREE2);
		ResourceManager.loadImage(PLAYER_TEST);
		ResourceManager.loadImage(ARMOR_RSC);
		ResourceManager.loadImage(SWORD_RSC);
	}

	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new LotsOfYouGame("ThereAreLotsOfYou!", 960, 540));
			app.setDisplayMode(960, 540, false);
			app.setVSync(true);

			app.start();
		} catch (Exception e) {
			e.printStackTrace();
    }
	}
}
