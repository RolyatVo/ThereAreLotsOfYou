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

    private Vector[] playerCoords;

    public GameServer () {
        System.out.println("Starting Server");
        playerSockets = new ArrayList<>();
        playersReadRunnable = new ArrayList<>();
        playersWriteRunnable = new ArrayList<>();
        playerCoords = new Vector[10];
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
                    if(playerCoords[playerID] != null) {
                        x = dataIN.readFloat();
                        y = dataIN.readFloat();

                        playerCoords[playerID] = new Vector(x, y);


                        //System.out.println("X: " + x + " Y: " + y);
                       // System.out.println("Player " + playerID + ": X: " + playerCoords[playerID].getX() + " Y: " + playerCoords[playerID].getY());
                    }
                    else {
                        x = dataIN.readFloat();
                        y = dataIN.readFloat();
                        playerCoords[playerID] = new Vector(x, y);
                      //  System.out.println("New Player " + playerID + ": X: " + playerCoords[playerID].getX() + " Y: " + playerCoords[playerID].getY());

                    }
                }

            } catch(IOException ex) {
//                ex.printStackTrace();
                playerCoords[playerID] = null;
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
                    for(int i =0; i < playerCoords.length; i++ ) {
                        if(i != playerID && playerCoords[i] != null) {
                            dataOUT.writeInt(i);
                            dataOUT.writeFloat(playerCoords[i].getX());
                            dataOUT.writeFloat(playerCoords[i].getY());
                            System.out.println("X: " + playerCoords[i].getX() + " Y: " + playerCoords[i].getY());
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

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnection();
    }

}
