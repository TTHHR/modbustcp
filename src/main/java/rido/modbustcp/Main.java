package rido.modbustcp;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import rido.modbustcp.view.Window;
//软件入口
public class Main extends Application {
    public static void main(String s[])
    {
        launch(s);
    }
    @Override
    public void start(Stage primaryStage) {
        Window mw=new Window();
        primaryStage.setScene(mw.getSence("MainWindow"));
        primaryStage.getIcons().add(new Image("/icon.png"));
        primaryStage.setOnCloseRequest(event -> mw.getControl().onWondowClose());
        primaryStage.show();
    }
}
