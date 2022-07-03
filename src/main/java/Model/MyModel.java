package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

public class MyModel extends Observable implements IModel {
    private Maze maze;
    private int playerRow = 0;
    private int playerCol = 0;
    private Solution solution;
    private Server generateMaze;
    private Server solveSearchProblem;
    private int x;
    private final Logger LOG = LogManager.getLogger();

    /**
     * Constructor of MyModel class
     */
    public MyModel() {
        this.generateMaze = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        this.solveSearchProblem = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        this.generateMaze.start();
        LOG.info("Generator maze server start to work with port "+ 5400);
        this.solveSearchProblem.start();
        LOG.info("Solving maze server start to work with port "+ 5401);
    }


    /** This function generate a maze by the server, according client's request
     * @param row represents number of rows in maze
     * @param col represents number of columns in maze
     * @throws UnknownHostException
     */
    @Override
    public void generateMaze(int row, int col) throws UnknownHostException {
        Client client = new Client( InetAddress.getByName("127.0.0.1"), 5400, new IClientStrategy() {
            public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                try {
                    LOG.info("client number: " +InetAddress.getLocalHost() +  " ask from server to generate new maze" );
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();
                    int[] mazeDimensions = new int[]{row, col};
                    toServer.writeObject(mazeDimensions);
                    toServer.flush();
                    byte[] compressedMaze = (byte[]) fromServer.readObject();
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressedMaze = new byte[24 + mazeDimensions[0] * mazeDimensions[1]];
                    is.read(decompressedMaze);
                    maze = new Maze(decompressedMaze);
                    LOG.info("Maze sizes: rows-"+maze.getRow()+" Maze sizes: colums-"+maze.getCol());
                    LOG.info("Generator maze server finish serve client");
                } catch (Exception var10) {
                    var10.printStackTrace();
                }

            }
        });
        client.communicateWithServer();
        playerRow = 0;
        playerCol = 0;
        setChanged();
        notifyObservers("Maze generated");
    }




    /**
     * @return the maze
     */
    @Override
    public Maze getMaze() {
        return maze;
    }



    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    @Override
    public void stopServers() throws InterruptedException {
        this.generateMaze.stop();
        this.solveSearchProblem.stop();
        Thread.sleep(1000);
        setChanged();
        notifyObservers("exit");
    }


}


