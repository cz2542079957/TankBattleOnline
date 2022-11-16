package sample.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.apache.commons.lang.StringUtils;
import sample.webSocket.WebSocketController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.codec.digest.DigestUtils;

public class Login implements Initializable
{
    public static Login login;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        login = this;
    }

    public static int status = 0; //当前网络连接状态，0为正在等待连接，1为连接完毕


    @FXML
    private TextField nickName;

    @FXML
    private TextField password;

    @FXML
    private Button buttonLogin;

    @FXML
    protected void clickLogin()
    {
        if (status == 0)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "未与服务器建立连接");
            alert.show();
            return;
        }
        String userNickName = nickName.getText().replace(" ", "");
        String userPassword = password.getText().replace(" ", "");
        if (StringUtils.isEmpty(userNickName) || StringUtils.isEmpty(userPassword))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "信息不能为空");
            alert.show();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("header", 0);
        map.put("userNickName", userNickName);
        map.put("userPassword", DigestUtils.md5Hex(userPassword));
        WebSocketController.SendMessage(map);

    }

    @FXML
    private Button buttonSignin;

    @FXML
    protected void clickSignin()
    {
        if (status == 0)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "未与服务器建立连接");
            alert.show();
            return;
        }
        String userNickName = nickName.getText().replace(" ", "");
        String userPassword = password.getText().replace(" ", "");
        if (StringUtils.isEmpty(userNickName) || StringUtils.isEmpty(userPassword))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "信息不能为空");
            alert.show();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("header", 2);
        map.put("userNickName", userNickName);
        map.put("userPassword", DigestUtils.md5Hex(userPassword));
        WebSocketController.SendMessage(map);
    }

    @FXML
    private Button buttonReconnect;

    @FXML
    protected void clickReconnet()
    {
        System.out.println("重新连接");
        WebSocketController.clientConnectInit();
    }

    @FXML
    private Label delay;

    @FXML
    private Label statusText;

    //设置右下方提示
    public void setStatusText(String str)
    {
        statusText.setText(str);
        if (str.equals("正在建立连接...")) statusText.setTextFill(Color.web("#ffffff"));
        else if (str.equals("连接成功")) statusText.setTextFill(Color.web("#5ebd00"));
        else statusText.setTextFill(Color.web("#ff3300"));
    }

    //设置延迟
    public void setDelay(long t)
    {
        delay.setVisible(true);
        delay.setManaged(true);
        delay.setText(String.valueOf(t) + " ms");
    }

    //关闭fps显示器
    public void closeDelay()
    {
        delay.setVisible(false);
        delay.setManaged(false);
    }

    //设置“重新连接”按钮是否可见
    public void setReconnetButtonShow(boolean flag)
    {
        buttonReconnect.setVisible(flag);
        buttonReconnect.setManaged(flag);
    }


}
