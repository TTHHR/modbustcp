package rido.modbustcp.view.control;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import rido.modbustcp.presenter.MainPresenter;
import rido.modbustcp.view.inter.MainInterface;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainWindowControl implements MainInterface {
    @FXML
   private CheckBox refreshCheckBox;
    @FXML
    private TextField refreshTimeText;
    @FXML
    private Button runButton;
    @FXML
    private ListView functionList;
    @FXML
    private ListView functionNameList;
    @FXML
    private ListView dataTypeList;
    @FXML
    private ListView addressList;
    @FXML
    private ListView addressTypeList;
    @FXML
    private ListView valueList;
    @FXML
    private ListView remarkList;
    private ListProperty<String> functions = new SimpleListProperty<>();
    private ListProperty<String> functionNames = new SimpleListProperty<>();
    private ListProperty<String> dataTypes = new SimpleListProperty<>();
    private ListProperty<String> addresses = new SimpleListProperty<>();
    private ListProperty<String> values = new SimpleListProperty<>();
    private ListProperty<String> addressTypes = new SimpleListProperty<>();
    private ListProperty<String> remarks = new SimpleListProperty<>();
    private MainPresenter mp;
    public MainWindowControl()
    {
        super();
        mp=new MainPresenter(this);

    }
    @FXML
    protected void exitMenuClick() {
        Platform.exit();
        System.exit(0);
    }
    @FXML
    protected void idMenuClick() {
        Platform.runLater(
                () ->{
                    TextInputDialog dialog = new TextInputDialog("1");
                    dialog.setTitle("RIDO");
                    dialog.setHeaderText("请输入从设备ID");
                    dialog.setContentText("十六进制");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(s -> mp.setSlaveID(s));

                }
        );
    }
    @FXML
    protected void loadExcelClick() {

        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("选择设备文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel File", "*.xlsx")
        );
        File file=
                fileChooser.showOpenDialog(null);
        if(file!=null)
        {
            //设置数据绑定
            functionList.itemsProperty().bind(functions);
            functionNameList.itemsProperty().bind(functionNames);
            dataTypeList.itemsProperty().bind(dataTypes);
            addressList.itemsProperty().bind(addresses);
            addressList.setCellFactory(TextFieldListCell.forListView());
            addressList.setEditable(true);
            addressList.setOnEditCommit(new EventHandler<ListView.EditEvent>() {
                @Override
                public void handle(ListView.EditEvent event) {
                    mp.setAddresss(event.getIndex(),event.getNewValue().toString());
                }
            } );
            valueList.itemsProperty().bind(values);
            valueList.setCellFactory(TextFieldListCell.forListView());
            valueList.setEditable(true);
            valueList.setOnEditCommit(new EventHandler<ListView.EditEvent>() {
                @Override
                public void handle(ListView.EditEvent event) {
                    mp.setValue(event.getIndex(),event.getNewValue().toString());
                }
            } );
            addressTypeList.itemsProperty().bind(addressTypes);
            remarkList.itemsProperty().bind(remarks);
            functionNameList.setOnMouseClicked(new ListViewHandler(){//添加的目的是LISTVIEW联动
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {//其余LIST跟随NAME LIST
                    int index=functionNameList.getSelectionModel().getSelectedIndex();
                    functionList.getSelectionModel().select(index);
                    dataTypeList.getSelectionModel().select(index);
                    addressList.getSelectionModel().select(index);
                    valueList.getSelectionModel().select(index);
                    addressTypeList.getSelectionModel().select(index);
                    remarkList.getSelectionModel().select(index);
                }
            });
            mp.loadExcel(file.getAbsolutePath());//加载excel
        }
    }


    @Override
    public void showDialog(String title, String text) {
        Platform.runLater(
                () ->{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("RIDO");
                    alert.setHeaderText(title);
                    alert.setContentText(text);
                    alert.showAndWait();
                }
        );
    }
    @FXML
    protected void addressMenuClick() {
        Platform.runLater(
                () ->{
                    TextInputDialog dialog = new TextInputDialog("127.0.0.1:1234");
                    dialog.setTitle("RIDO");
                    dialog.setHeaderText("请输入IP地址");
                    dialog.setContentText("地址包含端口：");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(s -> mp.setIpAddress(s));

                }
        );
    }

    @Override
    public void setFunctionList(List list) {
        Platform.runLater(
                ()->{
                    functions.set(FXCollections.observableArrayList(list));
                }
        );
    }

    @Override
    public void setFunctionNameList(List list) {
        Platform.runLater(
                ()->{
                    functionNames.set(FXCollections.observableArrayList(list));
                }
        );
    }

    @Override
    public void setDataTypeList(List list) {
        Platform.runLater(
                ()->{
                    dataTypes.set(FXCollections.observableArrayList(list));
                }
        );
    }

    @Override
    public void setValueList(List list) {
        Platform.runLater(
                ()->{
                    values.set(FXCollections.observableArrayList(list));
                }
        );
    }

    @Override
    public void setAddressList(List list) {
        Platform.runLater(
                ()->{
                    addresses.set(FXCollections.observableArrayList(list));
                }
        );
    }
    @Override
    public void setAddressTypeList(List list) {
        Platform.runLater(
                ()->{
                    addressTypes.set(FXCollections.observableArrayList(list));
                }
        );
    }
    @Override
    public void setRemarkList(List list) {
        Platform.runLater(
                ()->{
                    remarks.set(FXCollections.observableArrayList(list));
                }
        );
    }

    @Override
    public void setRunStatus(boolean on) {
        Platform.runLater(
                ()->{
                    if(on)
                    {
                        runButton.setText("停止查询");
                        runButton.setTextFill(Color.RED);
                    }
                    else {
                        runButton.setText("开始查询");
                        runButton.setTextFill(Color.GREEN);
                    }
                }
        );

    }

    @FXML
    protected void onRunButtonClick() {
        if(refreshCheckBox.isSelected()&&!refreshTimeText.getText().equals(""))
            mp.run(refreshTimeText.getText());
        else
        mp.query();
    }
    public void onWondowClose()
    {
        mp.close();

    }
}
