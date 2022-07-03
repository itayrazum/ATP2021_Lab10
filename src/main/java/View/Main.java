package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage mainStage;
    private static MyViewModel viewModel;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.mainStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView2.fxml"));
        Parent root = fxmlLoader.load();
        mainStage.setTitle("Maze");
        mainStage.setScene(new Scene(root, 1000, 700));
        mainStage.show();
        IModel model = new MyModel();
        viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);

    }
    public static void main(String[] args) {
        launch(args);
    }
    public void stop() throws InterruptedException {
        viewModel.stopServers();
    }
}
