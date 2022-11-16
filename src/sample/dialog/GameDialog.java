package sample.dialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import sample.Main;
import sample.utils.PaintUtils;
import sample.webSocket.WebSocketService;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GameDialog implements Initializable
{
    public static GameDialog gameDialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        gameDialog = this;
    }

    @FXML
    public Pane pane;
    @FXML
    public Canvas gameCanvas;
    //画布外框
    public Scene scene = null;

    //获取画框外框
    public Scene getScene()
    {
        if (scene == null)
        {
            scene = new Scene(pane, 1200, 700);
        }
        return scene;
    }

    //绘制等待大厅
    public void paintWaitingHall(int count)
    {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        PaintUtils.paintRect(gc, "#000000", 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        PaintUtils.paintText(gc, "等待人数:" + count, 50, "#ffffff", 470, 330);
    }

    //绘制匹配成功
    public void paintMatchSuccess(String p1, String p2)
    {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        PaintUtils.paintRect(gc, "#000000", 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        PaintUtils.paintText(gc, "匹配成功!", 50, "#ffffff", 470, 130);
        PaintUtils.paintText(gc, "P1:" + p1, 45, "#fcc419", 100, 260);
        PaintUtils.paintText(gc, "P2:" + p2, 45, "#40c057", 100, 400);
        PaintUtils.paintText(gc, "正在初始化游戏...", 45, "#ffffff", 800, 630);
    }

    //绘制关卡提示界面
    public void paintLevelStart(int level)
    {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        PaintUtils.paintRect(gc, "#999999", 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        PaintUtils.paintText(gc, "LEVEL " + level, 45, "#000000", 470, 320);
    }

    //绘制游戏结束结算界面
    public void paintGameOver(int timeStatus, int level, int p1HL, int p1HW, int p1HM, int p1HH, int p1S, int p2HL, int p2HW, int p2HM, int p2HH, int p2S)
    {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        //显示gameOver
        if (timeStatus < 30)
        {
            gc.drawImage(PaintUtils.gameOver, 260, 220);
            return;
        }
        //显示结束
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        //玩家当期一共闯过了多少关
        PaintUtils.paintText(gc, "最高关卡 : " + level, 40, "#ffffff", 500, 50);
        //绘制玩家击毁战绩
        PaintUtils.paintText(gc, " 玩家一战绩: ", 35, "#ffffff", 100, 150);
        gc.drawImage(PaintUtils.enemy, 0, 32, 32, 32, 100, 200, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p1HL), 35, "#ffffff", 330, 240);
        gc.drawImage(PaintUtils.enemy, 4 * 32, 32, 32, 32, 100, 280, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p1HW), 35, "#ffffff", 330, 320);
        gc.drawImage(PaintUtils.enemy, 8 * 32, 32, 32, 32, 100, 360, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p1HM), 35, "#ffffff", 330, 400);
        gc.drawImage(PaintUtils.enemy, 12 * 32, 32, 32, 32, 100, 440, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p1HH), 35, "#ffffff", 330, 480);
        PaintUtils.paintText(gc, "总分: " + String.valueOf(p1S), 35, "#ffffff", 130, 600);

        PaintUtils.paintText(gc, "玩家二战绩:", 35, "#ffffff", 700, 150);
        gc.drawImage(PaintUtils.enemy, 0, 32, 32, 32, 700, 200, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p2HL), 35, "#ffffff", 930, 240);
        gc.drawImage(PaintUtils.enemy, 4 * 32, 32, 32, 32, 700, 280, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p2HW), 35, "#ffffff", 930, 320);
        gc.drawImage(PaintUtils.enemy, 8 * 32, 32, 32, 32, 700, 360, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p2HM), 35, "#ffffff", 930, 400);
        gc.drawImage(PaintUtils.enemy, 12 * 32, 32, 32, 32, 700, 440, 60, 60);
        PaintUtils.paintText(gc, String.valueOf(p2HH), 35, "#ffffff", 930, 480);
        PaintUtils.paintText(gc, "总分: " + String.valueOf(p2S), 35, "#ffffff", 730, 600);

        PaintUtils.paintText(gc, "5秒后返回", 30, "#ffffff", 900, 650);
    }

    //region 游戏进行时画面绘制

    //游戏开始后全局绘制
    public void paintAll(int[][] map, int enemyNum, int p1hp, int p2hp, int level, List<Map<String, Object>> playerTankData, List<Map<String, Object>> enemyTankData, List<Map<String, Object>> shellData, List<Map<String, Object>> animateData, List<Map<String, Object>> propsData)
    {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        paintMapExceptGrass(gc, map);
        PaintUtils.paintShells(gc, shellData);
        paintTanks(gc, playerTankData, enemyTankData);
        paintMapGrass(gc, map);
        paintAnimate(gc, animateData);
        paintProps(gc, propsData);
        paintInfoTable(gc, enemyNum, p1hp, p2hp, level);
    }

    //绘制地图 除了草丛（草丛图层在玩家坦克图层之上）
    public void paintMapExceptGrass(GraphicsContext gc, int[][] map)
    {
        for (int i = 1; i <= 26; i++)
        {
            for (int j = 1; j <= 26; j++)
            {
                if (map[i][j] == 2)
                {
                    PaintUtils.paintRect(gc, "#000000", j * 24, i * 24, 24, 24);
                    continue;
                }
                PaintUtils.paintMiniMapCell(gc, i, j, map[i][j]);
            }
        }
        if (map[25][13] == 9) PaintUtils.paintBase(gc, 25 * 24, 13 * 24, 0);
        else PaintUtils.paintBase(gc, 25 * 24, 13 * 24, 1);
        PaintUtils.paintMapEdge(gc);
    }

    //绘制草丛
    public void paintMapGrass(GraphicsContext gc, int[][] map)
    {
        for (int i = 1; i <= 26; i++)
        {
            for (int j = 1; j <= 26; j++)
            {
                if (map[i][j] == 2)
                {
                    PaintUtils.paintMiniMapCell(gc, i, j, 2);
                }
            }
        }
    }

    //绘制玩家坦克
    public void paintTanks(GraphicsContext gc, List<Map<String, Object>> playerTankData, List<Map<String, Object>> enemyTankData)
    {
        PaintUtils.paintPlayerTank(gc, playerTankData);
        PaintUtils.paintEnemyTank(gc, enemyTankData);
    }

    //绘制全场特效动画
    public void paintAnimate(GraphicsContext gc, List<Map<String, Object>> animateData)
    {
        for (Map<String, Object> animate : animateData)
        {
            int type = Integer.parseInt(animate.get("type").toString());
            int x = Integer.parseInt(animate.get("x").toString());
            int y = Integer.parseInt(animate.get("y").toString());
            int offsetX = Integer.parseInt(animate.get("offsetX").toString());
            int offsetY = Integer.parseInt(animate.get("offsetY").toString());
            int width = Integer.parseInt(animate.get("width").toString());
            int height = Integer.parseInt(animate.get("height").toString());
            if (type == 1) PaintUtils.paintBornAnimate(gc, offsetX, offsetY, x, y, width, height);
            else if (type == 2)
            {
                PaintUtils.paintExplosionAnimate(gc, offsetX, offsetY, x, y, width, height);
            } else if (type == 3)
            {
                PaintUtils.paintShieldAnimate(gc, offsetX, offsetY, x, y, width, height);
            } else if (type == 4)
            {
                PaintUtils.paintHitAnimate(gc, offsetX, offsetY, x, y, width, height);

            } else return;

        }
    }

    //绘制道具
    public void paintProps(GraphicsContext gc, List<Map<String, Object>> propsData)
    {
        for (Map<String, Object> prop : propsData)
        {
            int x = (int) prop.get("x");
            int y = (int) prop.get("y");
            int type = (int) prop.get("type");
            int modelLength = (int) prop.get("modelLength");
            int blinkStatus = (int) prop.get("blinkStatus");
            if (blinkStatus < 4) PaintUtils.paintProps(gc, x, y, type, modelLength);
        }
    }

    //绘制统计面板
    public void paintInfoTable(GraphicsContext gc, int enemyNum, int p1hp, int p2hp, int level)
    {
        PaintUtils.paintEnemyNum(gc, enemyNum);
        PaintUtils.paintPlayerAndLevelInfo(gc, p1hp, p2hp, level);
    }

    //绘制游戏系统消息
    public void paintGameHint()
    {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        if (WebSocketService.gameHintsList.size() != 0)
            PaintUtils.paintGameHint(gc, WebSocketService.gameHintsList.get(0).content);
    }


    //endregion

}
