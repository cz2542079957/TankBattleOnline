package sample.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import sample.webSocket.WebSocketService;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerInfo implements Initializable

{
    public static PlayerInfo playerInfo;
    @FXML
    private Label nickName;
    @FXML
    private Label total_battels;
    @FXML
    private Label total_score;
    @FXML
    private Label destroyLightTank;
    @FXML
    private Label destroyWheelTank;
    @FXML
    private Label destroyMediumTank;
    @FXML
    private Label destroyHeavyTank;
    @FXML
    private Label max_level;
    @FXML
    private Label max_score;
    @FXML
    private Label max_destroy;

    @FXML
    private Label playerDan;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        playerInfo = this;
    }

    //绘制个人中心
    public void paintPlayerInfo(String nickName, int total_battels, int total_score, int destroyLightTank, int destroyWheelTank, int destroyMediumTank, int destroyHeavyTank, int max_score, int max_level, int max_destroy)
    {

        this.nickName.setText("你好，" + nickName);
        this.total_battels.setText("战斗次数：" + String.valueOf(total_battels));
        this.total_score.setText("总分：" + String.valueOf(total_score));
        this.destroyLightTank.setText("击毁轻坦：" + String.valueOf(destroyLightTank));
        this.destroyWheelTank.setText("击毁轮战：" + String.valueOf(destroyWheelTank));
        this.destroyMediumTank.setText("击毁中坦：" + String.valueOf(destroyMediumTank));
        this.destroyHeavyTank.setText("击毁重坦：" + String.valueOf(destroyHeavyTank));
        this.max_score.setText("最高单场积分：" + String.valueOf(max_score));
        this.max_level.setText("最高关卡：" + String.valueOf(max_level));
        this.max_destroy.setText("最高单场击毁数：" + String.valueOf(max_destroy));
        if (total_score < 10000)
        {
            this.playerDan.setText("新兵");
            this.playerDan.setTextFill(Color.web("#868e96"));
        } else if (total_score < 20000)
        {
            this.playerDan.setText("下士");
            this.playerDan.setTextFill(Color.web("#ffe066"));
        } else if (total_score < 40000)
        {
            this.playerDan.setText("中士");
            this.playerDan.setTextFill(Color.web("#fcc419"));
        } else if (total_score < 70000)
        {
            this.playerDan.setText("上士");
            this.playerDan.setTextFill(Color.web("#f08c00"));
        } else if (total_score < 120000)
        {
            this.playerDan.setText("军士长");
            this.playerDan.setTextFill(Color.web("#a9e34b"));
        } else if (total_score < 190000)
        {
            this.playerDan.setText("准尉");
            this.playerDan.setTextFill(Color.web("#82c91e"));
        } else if (total_score < 280000)
        {
            this.playerDan.setText("特级准尉");
            this.playerDan.setTextFill(Color.web("#5c940d"));
        } else if (total_score < 400000)
        {
            this.playerDan.setText("少校");
            this.playerDan.setTextFill(Color.web("#38d9a9"));
        } else if (total_score < 560000)
        {
            this.playerDan.setText("中校");
            this.playerDan.setTextFill(Color.web("#12b886"));
        } else if (total_score < 760000)
        {
            this.playerDan.setText("上校");
            this.playerDan.setTextFill(Color.web("#087f5b"));
        } else if (total_score < 1060000)
        {
            this.playerDan.setText("准将");
            this.playerDan.setTextFill(Color.web("#74c0fc"));
        } else if (total_score < 1460000)
        {
            this.playerDan.setText("少将");
            this.playerDan.setTextFill(Color.web("#4dabf7"));
        } else if (total_score < 1960000)
        {
            this.playerDan.setText("中将");
            this.playerDan.setTextFill(Color.web("#228be6"));
        } else if (total_score < 3000000)
        {
            this.playerDan.setText("上将");
            this.playerDan.setTextFill(Color.web("#1864ab"));
        } else if (total_score < 5000000)
        {
            this.playerDan.setText("元帅");
            this.playerDan.setTextFill(Color.web("#fa5252"));
        } else if (total_score < 10000000)
        {
            this.playerDan.setText("统帅");
            this.playerDan.setTextFill(Color.web("#e64980"));
        } else
        {
            this.playerDan.setText("大元帅");
            this.playerDan.setTextFill(Color.web("#be4bdb"));
        }

    }

    //开始匹配
    @FXML
    public void gameStart()
    {
        WebSocketService.getInstance().gameStart();
    }

}
