package sample.webSocket;

import com.alibaba.fastjson2.JSON;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import sample.Main;
import sample.dialog.GameDialog;
import sample.dialog.Login;
import sample.dialog.PlayerInfo;
import sample.game.audio.AudioProcessor;
import sample.game.controller.KeyPressed;
import sample.game.controller.KeyReleased;
import sample.game.gameHints.GameHints;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketService
{
    public static WebSocketService webSocketService = new WebSocketService();

    public static WebSocketService getInstance()
    {
        return webSocketService;
    }

    public static Login login;

    public static PlayerInfo playerInfo;

    public static GameDialog gameDialog;

    //游戏提示信息队列
    public static CopyOnWriteArrayList<GameHints> gameHintsList = new CopyOnWriteArrayList<GameHints>();


    //服务器消息处理器
    public void SocketMessageProcessor(Map<String, Object> msg)
    {
        int header = (int) msg.get("header");
        //登录
        if (header == 0)
        {
            loginProcessor(msg);
        }
        //测试服务器延迟
        else if (header == 1)
        {
            long t = new Date().getTime();
            if (WebSocketController.delay != 0)
            {
                long delay = t - WebSocketController.delay;
                Platform.runLater(() ->
                {
                    login.setDelay(delay);
                });
            }

        }
        //用户注册
        else if (header == 2)
        {
            signinProcessor(msg);
        }
        //用户信息获取
        else if (header == 3)
        {
            playerInfoProcessor(msg);
        }
        //服务器发送大厅等待人数数据
        else if (header == 10)
        {
            gameDialog.paintWaitingHall(Integer.parseInt(msg.get("count").toString()));
        }
        //服务器匹配成功通知
        else if (header == 11)
        {
            WebSocketController.gameRoomId = msg.get("id").toString();
            String p1 = msg.get("p1").toString();
            String p2 = msg.get("p2").toString();
            gameDialog.paintMatchSuccess(p1, p2);
        }
        //游戏准备开始，关卡提示
        else if (header == 100)
        {
            int level = Integer.parseInt(msg.get("level").toString());
            gameDialog.paintLevelStart(level);
        }
        //游戏关卡初始化完毕
        else if (header == 101)
        {
            System.out.println(msg.toString());
            WebSocketController.gameRoomId = (String) msg.get("id");
            // 开始清空游戏画布
            gameDialog.gameCanvas.getGraphicsContext2D().clearRect(0, 0, gameDialog.gameCanvas.getWidth(), gameDialog.gameCanvas.getHeight());
            gameDialog.gameCanvas.setOnKeyPressed(new KeyPressed());
            gameDialog.gameCanvas.setOnKeyReleased(new KeyReleased());
            Platform.runLater(() ->
            {
                //添加键盘监听事件
                gameDialog.gameCanvas.requestFocus();
            });
        }
        //游戏进行时数据
        else if (header == 102)
        {
            int[][] map = JSON.parseObject(msg.get("map").toString(), int[][].class);
            int enemyNum = JSON.parseObject(msg.get("enemyNum").toString(), int.class);
            int p1hp = JSON.parseObject(msg.get("p1hp").toString(), int.class);
            int p2hp = JSON.parseObject(msg.get("p2hp").toString(), int.class);
            int level = JSON.parseObject(msg.get("level").toString(), int.class);
            String playerTankDataString = msg.get("playerTankData").toString();
            List<Map<String, Object>> playerTankData = (List<Map<String, Object>>) JSON.parseObject(playerTankDataString, List.class);
            List<Map<String, Object>> enemyTankData = (List<Map<String, Object>>) JSON.parseObject(msg.get("enemyTankData").toString(), List.class);
            List<Map<String, Object>> shellData = (List<Map<String, Object>>) JSON.parseObject(msg.get("shellData").toString(), List.class);
            List<Map<String, Object>> animateData = (List<Map<String, Object>>) JSON.parseObject(msg.get("animateData").toString(), List.class);
            List<Map<String, Object>> propsData = (List<Map<String, Object>>) JSON.parseObject(msg.get("propsData").toString(), List.class);

            gameDialog.paintAll(map, enemyNum, p1hp, p2hp, level, playerTankData, enemyTankData, shellData, animateData, propsData);
            gameDialog.paintGameHint();

            if (gameHintsList.size() != 0)
            {
                gameDialog.paintGameHint();
            }

            sendPlayerControlInfo();
        }
        //队友断线提示
        else if (header == 103)
        {
            //时间戳，该信息的唯一标识
            long now = new Date().getTime();
            String content = msg.get("content").toString();
            //加入队列
            gameHintsList.add(new GameHints(now, content));
        }
        //游戏结束结算
        else if (header == 104)
        {
            int timeStatus = (int) msg.get("timeStatus");
            int level = (int) msg.get("level");
            int p1S = (int) msg.get("p1Score");
            int p2S = (int) msg.get("p2Score");
            String p1H = (String) msg.get("p1Hit");
            String p2H = (String) msg.get("p2Hit");
            int p1HL = Integer.parseInt(p1H.split("-")[0]);
            int p1HW = Integer.parseInt(p1H.split("-")[1]);
            int p1HM = Integer.parseInt(p1H.split("-")[2]);
            int p1HH = Integer.parseInt(p1H.split("-")[3]);
            int p2HL = Integer.parseInt(p2H.split("-")[0]);
            int p2HW = Integer.parseInt(p2H.split("-")[1]);
            int p2HM = Integer.parseInt(p2H.split("-")[1]);
            int p2HH = Integer.parseInt(p2H.split("-")[2]);
            gameDialog.paintGameOver(timeStatus, level, p1HL, p1HW, p1HM, p1HH, p1S, p2HL, p2HW, p2HM, p2HH, p2S);
        }
        //游戏音频 处理
        else if (header == 105)
        {
            //获取音频类型
            int type = (int) msg.get("type");
            //交给音频处理器
            AudioProcessor.AudioPlayer(type);
        }

    }

    //登入处理
    public void loginProcessor(Map<String, Object> msg)
    {
        if ((int) msg.get("res") == 0)
        {

        } else if ((int) msg.get("res") == 2)
        {
            Platform.runLater(() ->
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "用户名或密码错误");
                alert.show();
            });

        } else
        {
            Platform.runLater(() ->
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "登录数据出错");
                alert.show();
            });
        }
    }

    //注册处理
    public void signinProcessor(Map<String, Object> msg)
    {
        if ((int) msg.get("res") == 0)
        {

        } else if ((int) msg.get("res") == 2)
        {
            Platform.runLater(() ->
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "用户名已存在");
                alert.show();
            });
        } else
        {
            Platform.runLater(() ->
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "数据故障");
                alert.show();
            });
        }
    }

    //用户信息获取处理
    public void playerInfoProcessor(Map<String, Object> msg)
    {
        try
        {
            Parent root = new FXMLLoader(getClass().getResource("/resources/dialog/playerInfo.fxml")).load();
            Platform.runLater(() ->
            {
                Main.setStageScene(new Scene(root, 1200, 700));
                playerInfo = PlayerInfo.playerInfo;
                String nickName = msg.get("nickName").toString();
                int total_battels = (int) msg.get("total_battels");
                int total_score = (int) msg.get("total_score");
                int destroyLightTank = (int) msg.get("destroyLightTank");
                int destroyWheelTank = (int) msg.get("destroyWheelTank");
                int destroyMediumTank = (int) msg.get("destroyMediumTank");
                int destroyHeavyTank = (int) msg.get("destroyHeavyTank");
                int max_level = (int) msg.get("max_level");
                int max_score = (int) msg.get("max_score");
                int max_destroy = (int) msg.get("max_destroy");
                playerInfo.paintPlayerInfo(nickName, total_battels, total_score, destroyLightTank, destroyWheelTank, destroyMediumTank, destroyHeavyTank, max_score, max_level, max_destroy);

            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    //游戏开始
    public void gameStart()
    {
        try
        {
            Parent root = new FXMLLoader(getClass().getResource("/resources/dialog/gameDialog.fxml")).load();
            Platform.runLater(() ->
            {
                Main.setStageScene(new Scene(root, 1200, 700));
                WebSocketService.gameDialog = GameDialog.gameDialog;
                WebSocketController.gameDialog = GameDialog.gameDialog;
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("header", 4);
        WebSocketController.SendMessage(map);

    }

    //向服务器发送玩家的操作数据
    public void sendPlayerControlInfo()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("header", 102);
        map.put("gameRoomId", WebSocketController.gameRoomId);
        int toward; //0代表没动
        if (WebSocketController.w) toward = 1;
        else if (WebSocketController.d) toward = 2;
        else if (WebSocketController.s) toward = 3;
        else if (WebSocketController.a) toward = 4;
        else toward = 0;
        map.put("toward", toward);
        map.put("fire", WebSocketController.space);
        //如果用户没有进行有效的操作直接return不发送消息
        if (toward == 0 && WebSocketController.space == false) return;
        WebSocketController.SendMessage(map);
    }


}
