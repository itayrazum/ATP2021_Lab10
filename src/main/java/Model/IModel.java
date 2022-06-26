package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.net.UnknownHostException;
import java.util.Observer;

public interface IModel {
    Maze getMaze();
    int getColPlayer();
    int getRowPlayer();
    void setColPlayer(int col);
    void setRowPlayer(int row);
    Solution getSolution();
    void generateMaze(int row, int col) throws UnknownHostException;
    void updateLocationP(int direction);
    void solveMaze(Maze mazeToSolve);
    void assignObserver(Observer o);
    void start();
    void stop() throws InterruptedException;
    void updateConfig(int threadSize, int rows, int cols, String generateAlg, String solverAlg);
    String getSolverAlg();
    String getGenerateAlg();
    int getRowMaze();
    int getColMaze();
    boolean checkFinish();
    void saveMaze(String name);
    void writeErrorToLog();

    int getThreadsNum();
}