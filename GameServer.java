package lotsofyou;

import jig.Vector;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GameServer {

    private int playerCount;
    private final int playerCountMax;

    private ServerSocket serverSocket;
    private ArrayList<Socket> playerSockets;
    private ArrayList<ReadClient> playersReadRunnable;
    private ArrayList<WriteClient> playersWriteRunnable;
    private final ArrayList<Integer> removedCollectibles = new ArrayList<>();

    private final PlayerManager players;

    Thread listen;

    public GameServer () {
        System.out.println("Starting Server");
        playerSockets = new ArrayList<>();
        playersReadRunnable = new ArrayList<>();
        playersWriteRunnable = new ArrayList<>();
        players = new PlayerManager();
        playerCount = 0;
        playerCountMax = 10;

        try {
            serverSocket = new ServerSocket(55555);

        } catch (IOException exception) {
            System.out.println("IO Exception from server");
        }

        // Collectible.addCollectible(Collectible.Type.SWORD, new Vector(-64, -64));
    }

    public void listenForConnections() {
        listen = new Thread(() -> {
            try {
                System.out.println("Looking for connections");

                while(playerCount < playerCountMax) {
                    Socket server = serverSocket.accept();
                    DataInputStream serverIn = new DataInputStream(server.getInputStream());
                    DataOutputStream serverOut = new DataOutputStream(server.getOutputStream());

                    playerCount++;
                    serverOut.writeInt(playerCount);
                    System.out.println("Player " + playerCount + " has joined!");

                    ReadClient rc = new ReadClient(playerCount, serverIn);
                    WriteClient wc = new WriteClient(playerCount, serverOut);

                    playerSockets.add(server);
                    playersWriteRunnable.add(wc);
                    playersReadRunnable.add(rc);

                    Thread readClients = new Thread(playersReadRunnable.get(playersReadRunnable.size()-1));

                    Thread writeClients = new Thread(playersWriteRunnable.get(playersWriteRunnable.size()-1));

                    readClients.start();
                    writeClients.start();

                }

            } catch (IOException exception) {
                System.out.println("Exception from acceptConnection");
            }
        });
        listen.start();
    }

    public void loop() {
        long lastUpdate = System.currentTimeMillis();
        for(;;) {
            //lock to time step;
            long currTime = System.currentTimeMillis();
            if((double)(currTime - lastUpdate) > (1000.0 / 60)) {
                // System.out.println("Attempting to update?");
                lastUpdate = currTime;
                synchronized (players) {
                    for (Player p : players.getPlayers()) {
                        // System.out.println("Updating player " + p.getID());
                        p.update(1000.0f / 60);

                        synchronized (removedCollectibles) {
                            for(Collectible c : Collectible.getCollectibles()) {
                                if(c.intersects(new Vector(p.getX(), p.getY()))) {
                                    p.collect(c);
                                    removedCollectibles.add(c.getId());
                                }
                            }

                            for (int i : removedCollectibles) {
                                Collectible.removeCollectible(i);
                            }
                        }

                        for (Player other : players.getPlayers()) {
                            if (other != p && other.canHit(p) && p.hitBy(other)) {
                                System.out.println("Hit?");
                                other.hit(p);
                                p.damage(10);
                            }
                        }
                    }
                }
            }
        }
    }

    private class ReadClient implements Runnable {
            private int playerID;
            private DataInputStream dataIN;
            public ReadClient(int pid, DataInputStream in) {
                playerID = pid;
                dataIN = in;
                System.out.println("Read: " + playerID + " Runnable created");
            }


        @Override
        public void run() {
            try {
                while(true) {
                    int packetType = dataIN.readInt();
                    if(packetType == LotsOfYouGame.INPUT_PACKET) {
                        PlayerInput in = new PlayerInput();
                        in.read(dataIN);
                        synchronized (players) {
                            Player p = players.getPlayer(playerID);
                            if (p != null) {
                                // System.out.println("Applying input...");
                                p.setPlayerInput(in);
                            } else {
                                players.addPlayer(new Player(0, 0, playerID), playerID);
                                System.out.println("New Player! Id: " + playerID);
                            }
                        }
                    }
                }

            } catch(IOException ex) {
//                ex.printStackTrace();
                players.removePlayer(playerID);
                System.out.println("Socket closed for player " + playerID);
                playerCount--;
                //this removes the "top" player id
            }
        }
    }
    private class WriteClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOUT;
        public WriteClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOUT = out;
            System.out.println("Write: " + playerID + " Runnable created");
        }

        @Override
        public void run() {
            try {
                while(true) {
                    dataOUT.writeInt(LotsOfYouGame.STATE_PACKET);
                    dataOUT.writeInt(players.getPlayers().size());
                    for(Player p : players.getPlayers()) {
                        dataOUT.writeInt(p.getID());
                        PlayerState st = p.getPlayerState();
                        st.write(dataOUT);
                    }

                    synchronized (removedCollectibles) {
                        if(!removedCollectibles.isEmpty()) {
                            dataOUT.writeInt(LotsOfYouGame.REMOVE_COLLECTIBLE_PACKET);
                            dataOUT.writeInt(removedCollectibles.size());
                            for(int i : removedCollectibles) {
                                dataOUT.writeInt(i);
                            }
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.listenForConnections();
        gs.loop();
    }

}
