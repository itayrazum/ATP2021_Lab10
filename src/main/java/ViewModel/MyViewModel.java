package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;

import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }

    public void setMaze(Maze maze){
        model.setMaze(maze);
    }

    /** This function return a maze
     * @return a maze by model class
     */
    public Maze getMaze(){
        return model.getMaze();
    }


    public void generateMaze(int rows, int cols) throws UnknownHostException {
        model.generateMaze(rows, cols);
    }


    @Override
    public void update(Observable o, Object arg) {

    }
    public void stopServers() throws InterruptedException {
        model.stopServers();
    }
}
