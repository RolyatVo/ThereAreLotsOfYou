package lotsofyou;

import jig.Vector;
import org.newdawn.slick.util.pathfinding.navmesh.Link;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GameServer {

    private int playerCount;
    private final int playerCountMax;
    int prevAnimFrame = 0;

    private ServerSocket serverSocket;
    private ArrayList<Socket> playerSockets;
    private ArrayList<ReadClient> playersReadRunnable;
    private final ArrayList<WriteClient> playersWriteRunnable;
    private final ArrayList<Integer> removedCollectibles = new ArrayList<>();

    private final PlayerManager players;

    Thread listen;

    private int waitTick = 0;
    private static final int waitTickMax = 60 * 20;
    private static final int minimumPlayerCount = 2;

    enum GameServerState {
        WAITING,
        RUNNING
    }

    GameServerState state;

    public GameServer () {
        System.out.println("Starting Server");
        playerSockets = new ArrayList<>();
        playersReadRunnable = new ArrayList<>();
        playersWriteRunnable = new ArrayList<>();
        players = new PlayerManager();
        playerCount = 0;
        playerCountMax = 10;
        state = GameServerState.WAITING;

        try {
            serverSocket = new ServerSocket(10000);

        } catch (IOException exception) {
            System.out.println("IO Exception from server");
        }

        Level.InitLevel("LotsOfYou/src/lotsofyou/levels/test.txt");

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

                    synchronized (playersWriteRunnable) {
                        for (WriteClient wr : playersWriteRunnable) {
                            wr.queueNewPlayer(playerCount);
                        }
                    }

                    playerSockets.add(server);
                    synchronized (playersWriteRunnable) {
                        playersWriteRunnable.add(wc);
                    }
                    synchronized (playersReadRunnable) {
                        playersReadRunnable.add(rc);
                    }

                    synchronized (players) {
                        players.addPlayer(new Player(0, 0, playerCount), playerCount);
                        synchronized (state) {
                            if(state == GameServerState.RUNNING) {
                                players.getPlayer(playerCount).damage(10000);
                            } else {
                                Level.prepareForSpawn(players.getPlayers());
                            }
                        }

                        System.out.println("New Player! Id: " + playerCount);


                        for (Player p : players.getPlayers()) {
                            serverOut.writeInt(LotsOfYouGame.JOIN_PACKET);
                            serverOut.writeInt(p.getID());
                        }
                    }

                    synchronized (playersWriteRunnable) {
                        synchronized (playersReadRunnable) {
                            Thread readClients = new Thread(playersReadRunnable.get(playersReadRunnable.size() - 1));


                            Thread writeClients = new Thread(playersWriteRunnable.get(playersWriteRunnable.size() - 1));

                            readClients.start();
                            writeClients.start();
                        }
                    }


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
                lastUpdate = currTime;

                ArrayList<ReadClient> toRemoveReads = new ArrayList<>();
                ArrayList<WriteClient> toRemoveWrites = new ArrayList<>();

                for(ReadClient r : playersReadRunnable) {
                    if(r.dead) toRemoveReads.add(r);
                }

                synchronized (playersReadRunnable) {
                    for(ReadClient r : playersReadRunnable) {
                        if(r.dead) toRemoveReads.add(r);
                    }

                    for (ReadClient toRemove : toRemoveReads) {
                        playersReadRunnable.remove(toRemove);
                        System.out.println("Removed reader.");
                    }
                }

                synchronized (playersWriteRunnable) {
                    for(WriteClient e : playersWriteRunnable) {
                        if(e.dead) toRemoveWrites.add(e);
                    }

                    for (WriteClient toRemove : toRemoveWrites) {
                        playersWriteRunnable.remove(toRemove);
                        System.out.println("Removed writer");
                    }
                }

                switch (state) {
                    case WAITING:
                        synchronized (playersWriteRunnable) {
                            if (playersWriteRunnable.size() >= minimumPlayerCount) {
                                ++waitTick;
                                if (waitTick == waitTickMax) {
                                    waitTick = 0;
                                    state = GameServerState.RUNNING;
                                }
                            } else {
                                waitTick = 0;
                            }
                        }
                        synchronized (players) {
                            players.getPlayers().forEach(Player::waitForOthers);
                        }
                        break;
                    case RUNNING:
                        // System.out.println("Attempting to update?");
                        synchronized (players) {
                            int deadCount = 0;
                            for (Player p : players.getPlayers()) {
                                // System.out.println("Updating player " + p.getID());
                                p.update(1000.0f / 60, Level.getTilemap());

                                for(Collectible c : Collectible.getCollectibles()) {
                                    if(c.intersects(new Vector(p.getX(), p.getY())) && p.canCollect(c)) {
                                        p.collect(c);
                                        removedCollectibles.add(c.getId());
                                    }
                                }

                                synchronized (removedCollectibles) {
                                    for (int i : removedCollectibles) {
                                        Collectible.removeCollectible(i);
                                    }

                                    synchronized (playersWriteRunnable) {
                                        for (WriteClient writeClient : playersWriteRunnable) {
                                            writeClient.copyRemoveCollectibles(removedCollectibles);
                                        }
                                    }
                                    removedCollectibles.clear();
                                }

                                for (Player other : players.getPlayers()) {
                                    if (other != p && other.canHit(p) && p.hitBy(other)) {
                                        other.hit(p);
                                        p.damage(other.getDamage());
                                    }
                                }
                                if(p.isDead()) ++deadCount;
                            }
                            if(players.getPlayers().size() - deadCount <= 1) {
                                restartGame();
                            }
                        }
                        break;
                }


            }
        }
    }

    private void restartGame() {
        System.out.println("restarting...");
        state = GameServerState.WAITING;
        synchronized (players) {
            for(Player p : players.getPlayers()) {
                p.resurrect();
            }
            Level.prepareForSpawn(players.getPlayers());
        }

        Level.InitLevel("LotsOfYou/src/lotsofyou/levels/test.txt");

        synchronized (playersWriteRunnable) {
            for (WriteClient write : playersWriteRunnable) {
                write.setShouldRestart();
            }
        }
    }

    private class ReadClient implements Runnable {
            private int playerID;
            private DataInputStream dataIN;
            private boolean dead;
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
                                synchronized (state) {
                                    System.out.println("Missing player!");
//                                    if(state == GameServerState.WAITING) {
//
//                                        players.addPlayer(new Player(0, 0, playerID), playerID);
//                                        Level.prepareForSpawn(players.getPlayers());
//                                        System.out.println("New Player! Id: " + playerID);
//                                    }
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(7);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }

            } catch(IOException ex) {
//                ex.printStackTrace();
                synchronized (players) {
                    players.removePlayer(playerID);
                }

                System.out.println("Socket closed for player " + playerID);
                playerCount--;

                synchronized (playersWriteRunnable) {
                    for(WriteClient w : playersWriteRunnable) {
                        w.queueRemovedPlayer(playerID);
                    }
                }
            }
            dead = true;
        }
    }
    private class WriteClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOUT;
        private final ArrayList<Integer> removeCollectiblesCopy;
        private final ArrayList<Integer> removedPlayersQueue;

        private final LinkedList<String> messageQueue;
        private final ArrayList<Integer> newPlayers;

        private int prevSecondsRemaining;

        private boolean shouldRestart;

        private boolean dead;

        public WriteClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOUT = out;
            System.out.println("Write: " + playerID + " Runnable created");
            removeCollectiblesCopy = new ArrayList<>();
            messageQueue = new LinkedList<>();
            newPlayers = new ArrayList<>();
            shouldRestart = false;
            removedPlayersQueue = new ArrayList<>();
        }

        public void queueNewPlayer(int pid) {
            synchronized (newPlayers) {
                newPlayers.add(pid);
            }
        }

        public void queueRemovedPlayer(int pid) {
            synchronized (removedPlayersQueue) {
                removedPlayersQueue.add(pid);
            }
        }

        public void setShouldRestart() {
            shouldRestart = true;
        }

        public void copyRemoveCollectibles(ArrayList<Integer> removeCollectibles) {
            synchronized (removeCollectiblesCopy) {
                removeCollectiblesCopy.addAll(removeCollectibles);
            }
        }

        @Override
        public void run() {
            try {
                while(true) {
                    synchronized(players) {
                        dataOUT.writeInt(LotsOfYouGame.STATE_PACKET);
                        dataOUT.writeInt(players.getPlayers().size());
                        for (Player p : players.getPlayers()) {
                            dataOUT.writeInt(p.getID());
                            PlayerState st = p.getPlayerState();
                            st.write(dataOUT);
                        }
                    }

                    synchronized (removeCollectiblesCopy) {
                        if(!removeCollectiblesCopy.isEmpty()) {
                            dataOUT.writeInt(LotsOfYouGame.REMOVE_COLLECTIBLE_PACKET);
                            dataOUT.writeInt(removeCollectiblesCopy.size());
                            for(int i : removeCollectiblesCopy) {
                                dataOUT.writeInt(i);
                            }
                        }
                        removeCollectiblesCopy.clear();
                    }

                    synchronized (newPlayers) {
                        for(int i : newPlayers) {
                            dataOUT.writeInt(LotsOfYouGame.JOIN_PACKET);
                            dataOUT.writeInt(i);
                        }
                        newPlayers.clear();
                    }

                    synchronized (removedPlayersQueue) {
                        for(int i : removedPlayersQueue) {
                            dataOUT.writeInt(LotsOfYouGame.QUIT_PACKET);
                            dataOUT.writeInt(i);
                        }
                        removedPlayersQueue.clear();
                    }

                    int secondsRemaining = (waitTickMax - waitTick) / 60;
                    if(state == GameServerState.WAITING && secondsRemaining <= 10) {
                        if(prevSecondsRemaining != secondsRemaining) {
                            dataOUT.writeInt(LotsOfYouGame.COUNTDOWN_PACKET);
                            dataOUT.writeInt(secondsRemaining);
                        }

                        prevSecondsRemaining = secondsRemaining;
                    }

                    if(shouldRestart) {
                        dataOUT.writeInt(LotsOfYouGame.RESTART_PACKET);
                        shouldRestart = false;
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch ( IOException ex ) {
                ex.printStackTrace();
                dead = true;
            }
        }
    }


    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.listenForConnections();
        gs.loop();
    }

}
