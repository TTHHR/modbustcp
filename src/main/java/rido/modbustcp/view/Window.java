package rido.modbustcp.view;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import rido.modbustcp.view.control.MainWindowControl;

import java.io.IOException;

public class Window  {
    MainWindowControl mc;
    public Scene getSence( String fxml) {
        Scene root=null;
        try {
        // 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(String.format("/%s.fxml",fxml)));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        VBox layout = fxmlLoader.load();
        root=new Scene(layout);
         mc=fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return root;
    }
    public MainWindowControl getControl()
    {
        return mc;
    }
}
