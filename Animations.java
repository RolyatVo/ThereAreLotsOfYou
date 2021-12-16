package lotsofyou;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;

public class Animations {
    SpriteStack[] walking = new SpriteStack[7];
    SpriteStackAnimation walkingAnimation;

    SpriteStack[] armorFrames = new SpriteStack[8];
    SpriteStackAnimation armorAnimation;

    SpriteStack[] rollingFrames = new SpriteStack[9];
    SpriteStackAnimation rollingAnimation;

    SpriteStack[] rollingWithSwordFrames = new SpriteStack[9];
    SpriteStackAnimation rollingSwordAnimation;

    SpriteStack[] swordFrames = new SpriteStack[6];
    SpriteStackAnimation swordAnimation;

    SpriteStack[] walkingSword = new SpriteStack[6];
    SpriteStackAnimation walkingWithSwordAnimation;


    public Animations(Camera cam) throws SlickException {

        walking[0] = new SpriteStack(LotsOfYouGame.WALKING_RSC_1, 6, 7, cam);
        walking[1] = new SpriteStack(LotsOfYouGame.WALKING_RSC_2, 6, 7, cam);
        walking[2] = new SpriteStack(LotsOfYouGame.WALKING_RSC_3, 6, 7, cam);
        walking[3] = new SpriteStack(LotsOfYouGame.WALKING_RSC_4, 6, 7, cam);
        walking[4] = new SpriteStack(LotsOfYouGame.WALKING_RSC_5, 6, 7, cam);
        walking[5] = new SpriteStack(LotsOfYouGame.WALKING_RSC_6, 6, 7, cam);
        walkingAnimation = new SpriteStackAnimation(walking, 150);

        armorFrames[0] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_0, 10, 7, cam);
        armorFrames[1] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_1, 10, 7, cam);
        armorFrames[2] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_2, 10, 7, cam);
        armorFrames[3] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_3, 10, 7, cam);
        armorFrames[4] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_4, 10, 7, cam);
        armorFrames[5] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_5, 10, 7, cam);
        armorFrames[6] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_6, 10, 7, cam);
        armorFrames[7] = new SpriteStack(LotsOfYouGame.ARMOR_RSC_7, 10, 7, cam);
        armorAnimation = new SpriteStackAnimation(armorFrames, 150);

        rollingFrames[0] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_0, 10, 11, cam);
        rollingFrames[1] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_1, 10, 11, cam);
        rollingFrames[2] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_2, 10, 11, cam);
        rollingFrames[3] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_3, 10, 11, cam);
        rollingFrames[4] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_4, 10, 11, cam);
        rollingFrames[5] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_5, 10, 11, cam);
        rollingFrames[6] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_6, 10, 11, cam);
        rollingFrames[7] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_7, 10, 11, cam);
        rollingFrames[8] = new SpriteStack(LotsOfYouGame.ROLLING_RSC_8, 10, 11, cam);
        rollingAnimation = new SpriteStackAnimation(rollingFrames, 150);

        rollingWithSwordFrames[0] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_0, 10, 11, cam);
        rollingWithSwordFrames[1] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_1, 10, 11, cam);
        rollingWithSwordFrames[2] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_2, 10, 11, cam);
        rollingWithSwordFrames[3] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_3, 10, 11, cam);
        rollingWithSwordFrames[4] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_4, 10, 11, cam);
        rollingWithSwordFrames[5] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_5, 10, 11, cam);
        rollingWithSwordFrames[6] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_6, 10, 11, cam);
        rollingWithSwordFrames[7] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_7, 10, 11, cam);
        rollingWithSwordFrames[8] = new SpriteStack(LotsOfYouGame.ROLLING_SWORD_RSC_8, 10, 11, cam);
        rollingSwordAnimation = new SpriteStackAnimation(rollingWithSwordFrames, 150);

        swordFrames[0] = new SpriteStack(LotsOfYouGame.SWORD_RSC_0, 10, 9, cam);
        swordFrames[1] = new SpriteStack(LotsOfYouGame.SWORD_RSC_1, 10, 9, cam);
        swordFrames[2] = new SpriteStack(LotsOfYouGame.SWORD_RSC_2, 11, 9, cam);
        swordFrames[3] = new SpriteStack(LotsOfYouGame.SWORD_RSC_3, 10, 9, cam);
        swordFrames[4] = new SpriteStack(LotsOfYouGame.SWORD_RSC_4, 10, 9, cam);
        swordFrames[5] = new SpriteStack(LotsOfYouGame.SWORD_RSC_5, 10, 9, cam);
        swordAnimation = new SpriteStackAnimation(swordFrames, 100);

        walkingSword[0] = new SpriteStack(LotsOfYouGame.WALKING_SWORD_RSC_0, 10, 11, cam);
        walkingSword[1] = new SpriteStack(LotsOfYouGame.WALKING_SWORD_RSC_1, 10, 11, cam);
        walkingSword[2] = new SpriteStack(LotsOfYouGame.WALKING_SWORD_RSC_2, 10, 11, cam);
        walkingSword[3] = new SpriteStack(LotsOfYouGame.WALKING_SWORD_RSC_3, 10, 11, cam);
        walkingSword[4] = new SpriteStack(LotsOfYouGame.WALKING_SWORD_RSC_4, 10, 11, cam);
        walkingSword[5] = new SpriteStack(LotsOfYouGame.WALKING_SWORD_RSC_5, 10, 11, cam);
        walkingWithSwordAnimation = new SpriteStackAnimation(walkingSword, 150);

    }
}
