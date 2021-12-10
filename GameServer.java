package lotsofyou;

import jig.Vector;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class GameServer {

    private int playerCount;
    private final int playerCountMax;

    private ServerSocket serverSocket;
    private ArrayList<Socket> playerSockets;
    private ArrayList<ReadClient> playersReadRunnable;
    private ArrayList<WriteClient> playersWriteRunnable;

    private ArrayList<Player> players;

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
    public void acceptConnection() {
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
                    float x, y;
                    Player current = getPlayer(playerID);
                    x = dataIN.readFloat();
                    y = dataIN.readFloat();
                    if(current != null) {

                        current.setX(x);
                        current.setY(y);
                        //System.out.println("X: " + x + " Y: " + y);
                       // System.out.println("Player " + playerID + ": X: " + playerCoords[playerID].getX() + " Y: " + playerCoords[playerID].getY());
                    }
                    else {
                        players.add( new Player(x,y, playerID));
                      //  System.out.println("New Player " + playerID + ": X: " + playerCoords[playerID].getX() + " Y: " + playerCoords[playerID].getY());
                    }
                }

            } catch(IOException ex) {
//                ex.printStackTrace();
                players.remove(getPlayer(playerID));
                System.out.println("Socket closed for player " + playerID);
                playerCount--;

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
                    Player cur = getPlayer(playerID);
//                    for(int i =0; i < players.size(); i++ ) {
//                        if(i != playerID && cur != null) {
//                            dataOUT.writeInt(i);
//                            dataOUT.writeFloat(cur.getX());
//                            dataOUT.writeFloat(cur.getY());
//                            System.out.println("X: " + cur.getX() + " Y: " + cur.getY());
//                            dataOUT.flush();
//                        }
//                    }

                    for(Player p: players) {
                        if(p != cur && cur != null) {
                            dataOUT.writeInt(p.getID());
                            dataOUT.writeFloat(p.getX());
                            System.out.println("Player: " + p.getID() + " X: " + cur.getX() + " Y: " + cur.getY());
                            dataOUT.writeFloat(p.getY());
                            dataOUT.flush();
                        }
                    }
                    try {
                        Thread.sleep(25);
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
        gs.acceptConnection();
    }

}
