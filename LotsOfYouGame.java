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

import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
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
	public static final String PLAYER_SHADOW_RSC = "lotsofyou/resource/player_shadow.png";


	//Walking PNGs
	public static final String WALKING_RSC_1 = "lotsofyou/resource/Animations/Walking_10x7/player_1_FRAME0.png";
	public static final String WALKING_RSC_2 = "lotsofyou/resource/Animations/Walking_10x7/player_1_FRAME1.png";
	public static final String WALKING_RSC_3 = "lotsofyou/resource/Animations/Walking_10x7/player_1_FRAME2.png";
	public static final String WALKING_RSC_4 = "lotsofyou/resource/Animations/Walking_10x7/player_1_FRAME3.png";
	public static final String WALKING_RSC_5 = "lotsofyou/resource/Animations/Walking_10x7/player_1_FRAME4.png";
	public static final String WALKING_RSC_6 = "lotsofyou/resource/Animations/Walking_10x7/player_1_FRAME5.png";

	//Armor Animated
	public static final String ARMOR_RSC_0 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME0.png";
	public static final String ARMOR_RSC_1 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME1.png";
	public static final String ARMOR_RSC_2 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME2.png";
	public static final String ARMOR_RSC_3 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME3.png";
	public static final String ARMOR_RSC_4 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME4.png";
	public static final String ARMOR_RSC_5 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME5.png";
	public static final String ARMOR_RSC_6 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME6.png";
	public static final String ARMOR_RSC_7 = "lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME7.png";

	//Player Rolling
	public static final String ROLLING_RSC_0 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME0.png";
	public static final String ROLLING_RSC_1 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME1.png";
	public static final String ROLLING_RSC_2 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME2.png";
	public static final String ROLLING_RSC_3 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME3.png";
	public static final String ROLLING_RSC_4 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME4.png";
	public static final String ROLLING_RSC_5 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME5.png";
	public static final String ROLLING_RSC_6 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME6.png";
	public static final String ROLLING_RSC_7 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME7.png";
	public static final String ROLLING_RSC_8 = "lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME8.png";

	//Player Rolling With Sword
	public static final String ROLLING_SWORD_RSC_0 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME0.png";
	public static final String ROLLING_SWORD_RSC_1 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME1.png";
	public static final String ROLLING_SWORD_RSC_2 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME2.png";
	public static final String ROLLING_SWORD_RSC_3 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME3.png";
	public static final String ROLLING_SWORD_RSC_4 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME4.png";
	public static final String ROLLING_SWORD_RSC_5 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME5.png";
	public static final String ROLLING_SWORD_RSC_6 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME6.png";
	public static final String ROLLING_SWORD_RSC_7 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME7.png";
	public static final String ROLLING_SWORD_RSC_8 = "lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME8.png";

	//Sword Animated
	public static final String SWORD_RSC_0 = "lotsofyou/resource/Animations/Sword_10x9/Sword_Animation_FRAME0.png";
	public static final String SWORD_RSC_1 = "lotsofyou/resource/Animations/Sword_10x9/Sword_Animation_FRAME1.png";
	public static final String SWORD_RSC_2 = "lotsofyou/resource/Animations/Sword_10x9/Sword_Animation_FRAME2.png";
	public static final String SWORD_RSC_3 = "lotsofyou/resource/Animations/Sword_10x9/Sword_Animation_FRAME3.png";
	public static final String SWORD_RSC_4 = "lotsofyou/resource/Animations/Sword_10x9/Sword_Animation_FRAME4.png";
	public static final String SWORD_RSC_5 = "lotsofyou/resource/Animations/Sword_10x9/Sword_Animation_FRAME5.png";

	//Walking with Sword
	public static final String WALKING_SWORD_RSC_0 = "lotsofyou/resource/Animations/Walking_Sword_10x11/Player_Walking_Sword_FRAME0.png";
	public static final String WALKING_SWORD_RSC_1 = "lotsofyou/resource/Animations/Walking_Sword_10x11/Player_Walking_Sword_FRAME1.png";
	public static final String WALKING_SWORD_RSC_2 = "lotsofyou/resource/Animations/Walking_Sword_10x11/Player_Walking_Sword_FRAME2.png";
	public static final String WALKING_SWORD_RSC_3 = "lotsofyou/resource/Animations/Walking_Sword_10x11/Player_Walking_Sword_FRAME3.png";
	public static final String WALKING_SWORD_RSC_4 = "lotsofyou/resource/Animations/Walking_Sword_10x11/Player_Walking_Sword_FRAME4.png";
	public static final String WALKING_SWORD_RSC_5 = "lotsofyou/resource/Animations/Walking_Sword_10x11/Player_Walking_Sword_FRAME5.png";

	public static final String ATTACK_RSC_0 =  "lotsofyou/resource/Animations/Attack/Player_attack_FRAME0.png";
	public static final String ATTACK_RSC_1 =  "lotsofyou/resource/Animations/Attack/Player_attack_FRAME1.png";
	public static final String ATTACK_RSC_2 =  "lotsofyou/resource/Animations/Attack/Player_attack_FRAME2.png";
	public static final String ATTACK_RSC_3 =  "lotsofyou/resource/Animations/Attack/Player_attack_FRAME3.png";
	public static final String ATTACK_RSC_4 =  "lotsofyou/resource/Animations/Attack/Player_attack_FRAME4.png";

	public static final String CLAP_ATTACK_RSC_0 =  "lotsofyou/resource/Animations/clap_attack/0.png";
	public static final String CLAP_ATTACK_RSC_1 =  "lotsofyou/resource/Animations/clap_attack/1.png";
	public static final String CLAP_ATTACK_RSC_2 =  "lotsofyou/resource/Animations/clap_attack/2.png";
	public static final String CLAP_ATTACK_RSC_3 =  "lotsofyou/resource/Animations/clap_attack/3.png";
	public static final String CLAP_ATTACK_RSC_4 =  "lotsofyou/resource/Animations/clap_attack/4.png";

	public static final String WALL_CORNER = "lotsofyou/resource/wall_corner.png";
	public static final String WALL_CROSS = "lotsofyou/resource/wall_cross.png";
	public static final String WALL_STRAIGHT = "lotsofyou/resource/wall_straight.png";
	public static final String WALL_T = "lotsofyou/resource/wall_t.png";

	public final int ScreenWidth;
	public final int ScreenHeight;
	public static final int INPUT_PACKET = 0;
	public static final int STATE_PACKET = 1;
	public static final int REMOVE_COLLECTIBLE_PACKET = 2;

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

		ResourceManager.loadImage(WALL_CORNER);
		ResourceManager.loadImage(WALL_CROSS);
		ResourceManager.loadImage(WALL_STRAIGHT);
		ResourceManager.loadImage(WALL_T);


		//Walking Animations
		ResourceManager.loadImage(WALKING_RSC_1);
		ResourceManager.loadImage(WALKING_RSC_2);
		ResourceManager.loadImage(WALKING_RSC_3);
		ResourceManager.loadImage(WALKING_RSC_4);
		ResourceManager.loadImage(WALKING_RSC_5);
		ResourceManager.loadImage(WALKING_RSC_6);

		//Armour
		for(int i=0; i < 8; i++) {
			ResourceManager.loadImage("lotsofyou/resource/Animations/Armor_10x7/Armor_FRAME" + i + ".png");
		}

		//Rolling
		for(int i=0; i < 9; i++) {
			ResourceManager.loadImage("lotsofyou/resource/Animations/Rolling_10x11/Rolling_FRAME" + i + ".png");
		}

		//Rolling with Sword
		for(int i=0; i < 9; i++) {
			ResourceManager.loadImage("lotsofyou/resource/Animations/Rolling_Sword_10x11/Rolling_Sword_FRAME" + i + ".png");
		}

		//Sword Animated
		for(int i=0; i < 6; i++) {
			ResourceManager.loadImage("lotsofyou/resource/Animations/Sword_10x9/Sword_Animation_FRAME" + i + ".png");
		}

		//Player Walking with sword
		for(int i=0; i < 6; i++) {
			ResourceManager.loadImage("lotsofyou/resource/Animations/Walking_Sword_10x11/Player_Walking_Sword_FRAME" + i + ".png");
		}

		//Attack
		for(int i=0; i < 5; i++) {
			ResourceManager.loadImage("lotsofyou/resource/Animations/Attack/Player_attack_FRAME" + i + ".png");
		}

		for(int i=0; i < 5; i++) {
			ResourceManager.loadImage("lotsofyou/resource/Animations/clap_attack/" + i + ".png");
		}
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