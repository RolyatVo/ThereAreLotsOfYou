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
    Player player;
    ArrayList<Player> players;
    SpriteStack tree;
    Camera cam = new Camera(960, 540);
    int frame = 0;


    float posX = 960.0f / 2;
    float posY = 540.0f / 2;


    @Override
    public int getID() {
        return LotsOfYouGame.STARTSTATE;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        //box = new SpriteStack(LotsOfYouGame.TEST_BOX, 16, 16, cam);
        tree = new SpriteStack(LotsOfYouGame.TEST_TREE, 64, 64, cam);
        playerSprite = new SpriteStack(LotsOfYouGame.PLAYER_TEST, 6, 3, cam);

        players = new ArrayList<>();

        player = new Player(playerSprite, posX, posY, 6, 3);
        ui = new UI_interface(player, 960, 540);
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


        tree.draw(posX + 32, posY + 32);
        player.render();
//        if(playerCoords[enemyID] != null)
//            enemyPlayer.render(playerCoords[enemyID].getX(), playerCoords[enemyID].getY());


        players.forEach(p -> p.render(p.getX(), p.getY()));
        ui.render(g);

    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        player.update(delta, container.getInput(), cam);
        cam.setTargetPos(player.getX(), player.getY());
        cam.update(container.getInput(), player);
        ui.update(player);
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
            wsRunnable = new WriteServer(out);

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
            System.out.println("Read to Server runnable created!!");


        }
        public void run() {
            try {
                while(true) {
                    //TODO:  Should go through and update each enemy get server to send each
                    //TODO: Make it so server sends "commands" or prompts so that we know what information is about to
                    // come through
                    int enemyID = dataIN.readInt();
                    Player enemy = getPlayer(enemyID);
                    if(enemy  != null) {
                        float xpos = dataIN.readFloat();
                        float ypos = dataIN.readFloat();
                        enemy.setX(xpos);
                        enemy.setY(ypos);

                    }
                    else {
                        enemy = new Player(playerSprite, 0, 0, 16, 16);
                        enemy.setID(enemyID);
                        players.add(enemy);

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

        public WriteServer (DataOutputStream out) {
            dataOUT = out;
            System.out.println("Write to Server runnable created!!");

        }
        public void run() {
            try {
                while(true) {
                    //TODO Maybe try to get client send a command or response so that server knows what to do
                    dataOUT.writeFloat(player.getX());
                    dataOUT.writeFloat(player.getY());
                    dataOUT.flush();
                    if (player.getKeyPress() != -1) {
                        System.out.println("Player Pressed: " + player.getKeyPress());
                    }
//          System.out.println("X: " + player.getX() +" Y: " + player.getY());
                    try {
                        Thread.sleep(25);

                    } catch (InterruptedException ex) {
                        ex.fillInStackTrace();
                    }

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
