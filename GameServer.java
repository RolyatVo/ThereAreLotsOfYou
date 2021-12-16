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

    private ArrayList<Player> players;

    Thread listen;

    public GameServer () {
        System.out.println("Starting Server");
        playerSockets = new ArrayList<>();
        playersReadRunnable = new ArrayList<>();
        playersWriteRunnable = new ArrayList<>();
        players = new ArrayList<>();
        playerCount = 0;
        playerCountMax = 10;

        try {
            serverSocket = new ServerSocket(55555);

        } catch (IOException exception) {
            System.out.println("IO Exception from server");
        }


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
                lastUpdate = currTime;
                for (Player p : players) {
                    p.update(1000.0f / 60);
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
                        Player p = getPlayer(playerID);
                        if(p != null) {
                            synchronized (p) {
                                p.setPlayerInput(in);
                            }
                        } else {
                            players.add( new Player(0, 0, playerID));
                            System.out.println("New Player! Id: " + playerID);
                        }
                    }

                }

            } catch(IOException ex) {
//                ex.printStackTrace();
                players.remove(getPlayer(playerID));
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
                    dataOUT.writeInt(players.size());
                    for(Player p : players) {
                        dataOUT.writeInt(p.getID());
                        PlayerState st = p.getPlayerState();
                        st.write(dataOUT);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
    }

    private Player getPlayer(int id) {
        for(Player p : players) {
            if (p.getID() == id)
                return p;
        }
        return null;
    }


    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.listenForConnections();
        gs.loop();
    }

}
