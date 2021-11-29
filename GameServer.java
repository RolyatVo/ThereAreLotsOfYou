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
                Thread readThreadplayers = new Thread(playersReadRunnable.get(playersReadRunnable.size()-1));

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
                    playerCoords[playerID].setX(dataIN.readFloat());
                    playerCoords[playerID].setY(dataIN.readFloat());
                }

            } catch(IOException ex) {
                ex.printStackTrace();
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
                        if(i != playerID) {
                            dataOUT.writeInt(i);
                            dataOUT.writeFloat(playerCoords[i].getX());
                            dataOUT.writeFloat(playerCoords[i].getY());
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
