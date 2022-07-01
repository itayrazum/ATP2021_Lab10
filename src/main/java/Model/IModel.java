package Model;

import algorithms.mazeGenerators.Maze;

import java.net.UnknownHostException;

public interface IModel {

    /** This function generate a maze by the server, according client's request
     * @param rows represents number of rows in maze
     * @param cols represents number of columns in maze
     * @throws UnknownHostException
     */
    void generateMaze(int rows, int cols) throws UnknownHostException;

    /**
     * @return the maze
     */
    Maze getMaze();

    public void setMaze(Maze maze);


    void stopServers() throws InterruptedException;
}
