package lotsofyou;

import jig.Vector;
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

    UI_interface ui;
    SpriteStack playerSprite;

    SpriteStackAnimation playerAnimated;
    Player player;

    PlayerInput playerInput;

    ArrayList<Player> players;
    SpriteStack tree;
    Camera cam = new Camera(960, 540);
    int frame = 0;


    @Override
    public int getID() {
        return LotsOfYouGame.STARTSTATE;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        //box = new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
        tree = new SpriteStack(LotsOfYouGame.TEST_TREE, 64, 64, cam);

        playerSprite = new SpriteStack(LotsOfYouGame.PLAYER_TEST, 6, 3, cam);


        SpriteStack[] frames = new SpriteStack[7];

        frames[0] = new SpriteStack(LotsOfYouGame.WALKING_RSC_1, 6, 7, cam);
        frames[1] = new SpriteStack(LotsOfYouGame.WALKING_RSC_2, 6, 7, cam);
        frames[2] = new SpriteStack(LotsOfYouGame.WALKING_RSC_3, 6, 7, cam);
        frames[3] = new SpriteStack(LotsOfYouGame.WALKING_RSC_4, 6, 7, cam);
        frames[4] = new SpriteStack(LotsOfYouGame.WALKING_RSC_5, 6, 7, cam);
        frames[5] = new SpriteStack(LotsOfYouGame.WALKING_RSC_6, 6, 7, cam);



        playerAnimated = new SpriteStackAnimation(frames, 150);

        players = new ArrayList<>();


        ui = new UI_interface( 960, 540);
        playerInput = new PlayerInput();
        cam.setScale(3);
        serverConnect();
    }


    @Override
    public void enter(GameContainer container, StateBasedGame game) {

    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setBackground(new Color(83, 124, 68));

        ++frame;


        tree.draw(32, 32);
        for(Player p : players) {
            p.render();
        }

        ui.render(g);

    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        Player player = getPlayer(playerID);
        if(player != null) {
            playerInput.update(container.getInput(), cam, new Vector(player.getX(), player.getY()));
            ui.update(player);

            cam.setTargetPos(player.getX(), player.getY());
            cam.update(container.getInput(), player);
        } else {
            playerInput.update(container.getInput(), cam, new Vector(0, 0));
        }
    }

    private Player getPlayer(int id) {
        for(Player p : players) {
            if(p.getID() == id) return p;
        }
        return null;
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
                        int count = dataIN.readInt();
                        for(int i = 0; i != count; ++i) {
                            int playerId = dataIN.readInt();
                            Player p = getPlayer(playerId);
                            if(p != null) {
                                PlayerState st = new PlayerState();
                                st.read(dataIN);
                                p.setPlayerState(st);
                            }
                            else {
                                p = new Player(playerSprite, 0, 0, 6, 3);
                                p.setID(playerId);
                                players.add(p);
                            }
                        }
                    }
                   // System.out.println("Enemy " + enemyID + ": X: " + playerCoords[enemyID].getX() + " Y: " + playerCoords[enemyID].getY());
                }

            } catch (IOException ex) {
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
                    if(input.isUpdated()) {
                        dataOUT.writeInt(LotsOfYouGame.INPUT_PACKET);
                        input.send(dataOUT);
                        //System.out.println("Input sent!");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
