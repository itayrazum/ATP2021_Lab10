package View;

import ViewModel.MyViewModel;
import javafx.scene.control.TextField;

import java.net.UnknownHostException;

public class MyViewController {
    public MyViewModel viewModel;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;

    public void generateMaze() throws UnknownHostException {

        int rows = Integer.valueOf((textField_mazeRows.getText()));
        int cols = Integer.valueOf((textField_mazeColumns.getText()));
        viewModel.generateMaze(rows,cols);
        mazeDisplayer.drawMaze(viewModel.getMaze().getMatrix());
    }

    public void solveMaze(){

    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }


}
