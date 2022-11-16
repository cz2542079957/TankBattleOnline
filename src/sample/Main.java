package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.dialog.GameDialog;
import sample.dialog.Login;
import sample.utils.ExternalConfigurationUtils;
import sample.utils.TimedTasksProcessor;
import sample.webSocket.WebSocketController;
import sample.webSocket.WebSocketService;

import java.awt.*;

public class Main extends Application
{
    public static Stage stage;
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        stage = primaryStage;
        stage.setResizable(false);
        FXMLLoader login = new FXMLLoader(getClass().getResource("/resources/dialog/login.fxml"));
        FXMLLoader gameDialog = new FXMLLoader(getClass().getResource("/resources/dialog/gameDialog.fxml"));
        Parent root = login.load();
        gameDialog.load();
        stage.setTitle("网络版经典坦克大战");
        setStageScene(new Scene(root, 1000, 700));
        stage.show();

        ExternalConfigurationUtils.init();

        //使用多线程启动网路服务
        WebSocketController.login = Login.login;
        WebSocketService.login = Login.login;
        WebSocketController.gameDialog = GameDialog.gameDialog;
        WebSocketService.gameDialog = GameDialog.gameDialog;
        new Thread(WebSocketController.getInstance()).start();
    }

    @Override
    public void stop() throws InterruptedException
    {
        System.out.println("程序结束");
        TimedTasksProcessor.isRunning = false;
        WebSocketController.client.close();
    }

    public static void setStageScene(Scene scene)
    {
        stage.setScene(scene);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
