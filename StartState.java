package lotsofyou;

import jig.Vector;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class StartState extends BasicGameState {

    UI_interface ui;
    SpriteStack playerSprite;

   // SpriteStackAnimation playerAnimated;
    Player player;

    PlayerInput playerInput;

    private Animations animations;

    final PlayerManager playerManager = new PlayerManager();
    Camera cam = new Camera(960, 540);
    int frame = 0;


    @Override
    public int getID() {
        return LotsOfYouGame.STARTSTATE;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {

        playerSprite = new SpriteStack(LotsOfYouGame.PLAYER_TEST, 6, 3, cam);

        animations = new Animations(cam);

        ui = new UI_interface( 960, 540);
        playerInput = new PlayerInput();
        cam.setScale(3);
        serverConnect();

        Level.InitLevel("LotsOfYou/src/lotsofyou/levels/test.txt", cam);

        Collectible.setCollectibleRenderCam(cam);
    }


    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        //Collectible.addCollectible(Collectible.Type.ARMOR, new Vector(-64, -64));
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        // System.out.println("Rendering...");
        g.setBackground(new Color(83, 124, 68));

        ++frame;
        synchronized (playerManager) {
            for (Player p : playerManager.getPlayers()) {
                p.render();
//                p.drawDebug(g, cam);
            }
        }

        ui.render(g);

        synchronized (Collectible.getCollectibles()) {
            for(Collectible c : Collectible.getCollectibles()) {
                c.render();
            }
        }

        Level.render();

        SpriteStack.doDrawAll();
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
//            if(playerInput.down || playerInput.up || playerInput.left || playerInput.right) {
//                player.playerAnimation.play();
//                player.playerAnimation.update(delta);
//            }
//            else {
//                player.playerAnimation.stop();
//                player.playerAnimation.setFrame(0);
//            }

        // System.out.println("Updating...");
        synchronized (playerManager) {
            Player player = playerManager.getPlayer(playerID);
            if (player != null) {
                player.updateAnimation(delta);

                playerInput.update(container.getInput(), cam, new Vector(player.getX(), player.getY()));
                ui.update(player);

                Collectible.getCollectibles().forEach(c -> c.update(delta));
                cam.setTargetPos(player.getX(), player.getY());
                cam.update(container.getInput(), player);
            } else {
                playerInput.update(container.getInput(), cam, new Vector(0, 0));
            }
        }
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
            // System.out.println("You are player #" + playerID);

            rsRunnable = new ReadServer(in);
            wsRunnable = new WriteServer(out, playerInput);

            Thread readServer = new Thread(rsRunnable);
            Thread writeServer = new Thread(wsRunnable);

            readServer.start();
            writeServer.start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class ReadServer implements Runnable {
        private DataInputStream dataIN;

        public ReadServer (DataInputStream in) {
            dataIN = in;
            System.out.println("Read from Server runnable created!!");
        }
        public void run() {
            try {
                while(true) {
                    int packetType = dataIN.readInt();
                    if(packetType == LotsOfYouGame.STATE_PACKET) {
                        // System.out.println("Applying states");
                        int count = dataIN.readInt();
                        for(int i = 0; i != count; ++i) {
                            int playerId = dataIN.readInt();
                            Player p = playerManager.getPlayer(playerId);
                            if(p != null) {
                                PlayerState st = new PlayerState();
                                st.read(dataIN);
                                p.setPlayerState(st);
                            }
                            else {
                                p = new Player(animations, 0, 0);
                                p.setID(playerId);
                                playerManager.addPlayer(p, playerId);
                            }
                        }
                    } else if (packetType == LotsOfYouGame.REMOVE_COLLECTIBLE_PACKET) {
                        int size = dataIN.readInt();
                        synchronized (Collectible.getCollectibles()) {
                            for (int i = 0; i != size; ++i) {
                                Collectible.removeCollectible(dataIN.readInt());
                            }
                        }
                    }
                    Thread.sleep(10);
                   // System.out.println("Enemy " + enemyID + ": X: " + playerCoords[enemyID].getX() + " Y: " + playerCoords[enemyID].getY());
                }

            } catch (IOException | InterruptedException ex) {
                //TODO : set up enemies here on client side
                ex.printStackTrace();
            }

        }
    }
    private class WriteServer implements Runnable {
        private DataOutputStream dataOUT;
        private PlayerInput input;

        public WriteServer (DataOutputStream out, PlayerInput input) {
            dataOUT = out;
            this.input = input;
            System.out.println("Write to Server runnable created!!");

        }
        public void run() {
            try {
                while(true) {
                    if(input.pollUpdated()) {
                        dataOUT.writeInt(LotsOfYouGame.INPUT_PACKET);
                        input.send(dataOUT);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
