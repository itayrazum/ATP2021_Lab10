package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Observable;
import java.util.Observer;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int colPlayer;
    private int rowPlayer;
    private Server generateMaze;
    private Server solveSearchProblem;
    private Solution solution;
    private final Logger LOGF = LogManager.getLogger();


    public MyModel() {
        this.generateMaze = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        this.solveSearchProblem = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        this.generateMaze.start();
        LOGF.info("Generator maze port: "+ 5400);
        this.solveSearchProblem.start();
        LOGF.info("Solving maze port:  "+ 5401);
    }




    @Override
    public Maze getMaze() {
        return this.maze;
    }

    @Override
    public int getColPlayer() {
        return colPlayer;
    }

    @Override
    public int getRowPlayer() {
        return rowPlayer;
    }

    @Override
    public void setColPlayer(int col) {
        this.colPlayer = col;

    }

    @Override
    public void setRowPlayer(int row) {
        this.rowPlayer = row;

    }

    @Override
    public Solution getSolution() {
        return null;
    }

    @Override
    public void generateMaze(int row, int col) {
        try{
            Client client = new Client(InetAddress.getLocalHost(), 5400, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inputStream, OutputStream outputStream) {
                            try {
                                ObjectOutputStream toServer = new ObjectOutputStream(outputStream);

                                ObjectInputStream fromServer = new ObjectInputStream(inputStream);

                                toServer.flush();
                                int[] mazeInfo = new int[]{row, col};
                                toServer.writeObject(mazeInfo); //send maze dimensions to server

                                toServer.flush();
                                byte[] compressedMaze = (byte[])fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                                InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                                byte[] decompressedMaze = new byte[2600 /*CHANGESIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze - with bytes
                                is.read(decompressedMaze); //Fill decompressedMaze

                                maze = new Maze(decompressedMaze);
                            } catch (Exception e) { e.printStackTrace();
                            }

                        }

                    });
            client.communicateWithServer();

        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // change start position, and maze rows and cols number.
        rowPlayer = maze.getStartPosition().getY();
        colPlayer = maze.getStartPosition().getX();
        setChanged();
        notifyObservers("Generating"); // finished generate maze
        try {
            LOGF.info("User "+ InetAddress.getLocalHost().getHostAddress() +"  New maze was generated successfully ");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        rowPlayer = 0;
        colPlayer = 0;
        setChanged();
        notifyObservers("Maze generated successfully");
    }

    @Override
    public void updateLocationP(int direction) {
            switch (direction) {
                case 1 -> { //up
                    if (rowPlayer > 0 && maze.getMatrix()[rowPlayer - 1][colPlayer] == 0)
                        movePlayer(rowPlayer - 1, colPlayer);
                }
                case 2 -> { //down
                    if (rowPlayer < maze.getMatrix().length - 1 && maze.getMatrix()[rowPlayer + 1][colPlayer] == 0)
                        movePlayer(rowPlayer + 1, colPlayer);
                }
                case 3 -> { //left
                    if (colPlayer > 0 && maze.getMatrix()[rowPlayer][colPlayer - 1] == 0)
                        movePlayer(rowPlayer, colPlayer - 1);
                }
                case 4 -> { //right
                    if (colPlayer < maze.getMatrix()[0].length - 1 && maze.getMatrix()[rowPlayer][colPlayer + 1] == 0)
                        movePlayer(rowPlayer, colPlayer + 1);
                }
                case 5 -> { // right and down
                    if (colPlayer < maze.getMatrix()[0].length - 1 && rowPlayer < maze.getMatrix().length - 1 && maze.getMatrix()[rowPlayer + 1][colPlayer + 1] == 0)
                        movePlayer(rowPlayer + 1, colPlayer + 1);
                }
                case 6 -> { //right and up
                    if (colPlayer < maze.getMatrix()[0].length - 1 && rowPlayer > 0 && maze.getMatrix()[rowPlayer - 1][colPlayer + 1] == 0)
                        movePlayer(rowPlayer - 1, colPlayer + 1);
                }
                case 7 -> { //left down
                    if (colPlayer > 0 && rowPlayer < maze.getMatrix().length - 1 && maze.getMatrix()[rowPlayer + 1][colPlayer - 1] == 0)
                        movePlayer(rowPlayer + 1, colPlayer - 1);
                }
                case 8 -> { //left and up
                    if (colPlayer > 0 && rowPlayer > 0 && maze.getMatrix()[rowPlayer - 1][colPlayer - 1] == 0)
                        movePlayer(rowPlayer - 1, colPlayer - 1);
                }
            }


        }
    private void movePlayer(int row, int col) {
        this.rowPlayer = row;
        this.colPlayer = col;
        setChanged();
        notifyObservers("player moved");
        if (row == maze.getGoalPosition().getY() && col == maze.getGoalPosition().getX()) {
            setChanged();
        }

    }


    @Override
    public void solveMaze(Maze maze) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer,
                                                   OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new
                                        ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new
                                        ObjectInputStream(inFromServer);
                                toServer.writeObject(maze); //send maze to server
                                toServer.flush();
                                Solution mazeSolution = (Solution)
                                        fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server %s", mazeSolution));
//Print Maze Solution retrieved from the server

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
            setChanged();
            notifyObservers("Solved");
            LOGF.info("Solution is ready");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void assignObserver(Observer o) {
    this.addObserver(o);

    }

    @Override
    public void start() {
         generateMaze.start();
         solveSearchProblem.start();

    }

    @Override
    public void stop() throws InterruptedException {
        generateMaze.stop();
        solveSearchProblem.stop();
        Thread.sleep(1000);
        setChanged();
        notifyObservers("server stoped");


    }

    @Override
    public void updateConfig(int threadSize, int rows, int cols, String generateAlg, String solverAlg) {

    }

    @Override
    public String getSolverAlg() {
        return config;
    }

    @Override
    public String getGenerateAlg() {
        return null;
    }

    @Override
    public int getRowMaze() {
        return 0;
    }

    @Override
    public int getColMaze() {
        return 0;
    }

    @Override
    public boolean checkFinish() {
        return false;
    }

    @Override
    public void saveMaze(String name) {

    }

    @Override
    public void writeErrorToLog() {

    }

    @Override
    public int getThreadsNum() {
        return 0;
    }
}
