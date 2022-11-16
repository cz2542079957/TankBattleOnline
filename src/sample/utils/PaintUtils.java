package sample.utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Map;

public class PaintUtils
{
    //单个地图元素的边长大小
    public static int miniCellLength = 24; //一个完整的地图单元边长是48

    //region 图片预加载
    public static Image brick = new Image("file:src/resources/src/Map/brick.jpg");
    public static Image grass = new Image("file:src/resources/src/Map/grass.png");
    public static Image river = new Image("file:src/resources/src/Map/river.jpg");
    public static Image iron = new Image("file:src/resources/src/Map/iron.jpg");
    public static Image tile = new Image("file:src/resources/src/Map/tile.jpg");
    public static Image home = new Image("file:src/resources/src/Other/Home.jpg");

    public static Image player1 = new Image("file:src/resources/src/Tank/Player1.png");
    public static Image player2 = new Image("file:src/resources/src/Tank/Player2.png");
    public static Image enemy = new Image("file:src/resources/src/Tank/Enemy.png");
    public static Image shells = new Image("file:src/resources/src/Shells/Shells.png");
    public static Image props = new Image("file:src/resources/src/Other/props.jpg");   // 180 * 28

    public static Image born = new Image("file:src/resources/src/Other/Born.jpg");
    public static Image hit = new Image("file:src/resources/src/Other/Hit.png");
    public static Image explosion = new Image("file:src/resources/src/Other/Explosion.png");
    public static Image shield = new Image("file:src/resources/src/Other/Shield.png");

    public static Image enemyNumFlag = new Image("file:src/resources/src/Other/EnemyNum.jpg");
    public static Image lifePoint = new Image("file:src/resources/src/Other/LifePoint.jpg");

    public static Image gameOver = new Image("file:src/resources/src/Other/GameOver.png");
    //endregion

    //封装绘制文字
    public static void paintText(GraphicsContext gc, String text, int size, String color, double x, double y)
    {
        gc.setFill(Color.web(color));
        gc.setFont(new Font(size));
        gc.fillText(text, x, y);
    }

    //封装绘制矩形
    public static void paintRect(GraphicsContext gc, String color, double x, double y, double width, double height)
    {
        gc.setFill(Color.web(color));
        gc.fillRect(x, y, width, height);
    }

    //region 地图以及贴图相关

    //绘制地图边界
    public static void paintMapEdge(GraphicsContext gc)
    {
        paintRect(gc, "#999999", 0, 0, 24, 24 * 28);
        paintRect(gc, "#999999", 24 * 27, 0, 24, 24 * 28);
        paintRect(gc, "#999999", 0, 0, 24 * 28, 24);
        paintRect(gc, "#999999", 0, 24 * 27, 24 * 28, 24);
    }

    //绘制地图小单元
    public static void paintMiniMapCell(GraphicsContext gc, int x, int y, int type)
    {
        if (type == 0)
        {
            paintRect(gc, "#000000", y * miniCellLength, x * miniCellLength, miniCellLength, miniCellLength);
            return;
        }
        switch (type)
        {
            case 1:
                gc.drawImage(brick, y * miniCellLength, x * miniCellLength, miniCellLength, miniCellLength);
                break;
            case 2:
                gc.drawImage(grass, y * miniCellLength, x * miniCellLength, miniCellLength, miniCellLength);
                break;
            case 3:
                gc.drawImage(river, y * miniCellLength, x * miniCellLength, miniCellLength, miniCellLength);
                break;
            case 4:
                gc.drawImage(iron, y * miniCellLength, x * miniCellLength, miniCellLength, miniCellLength);
                break;
            case 5:
                gc.drawImage(tile, y * miniCellLength, x * miniCellLength, miniCellLength, miniCellLength);
                break;
            default:
        }
    }

    //绘制基地
    public static void paintBase(GraphicsContext gc, int x, int y, int type)
    {
        gc.drawImage(home, type * 32, 0, 32, 32, y, x, 48, 48);
    }

    //绘制玩家坦克
    public static void paintPlayerTank(GraphicsContext gc, List<Map<String, Object>> playerTankData)
    {
        for (int i = 0; i <= 1; i++)
            if (playerTankData.get(i) != null)
            {
                Map<String, Object> p = playerTankData.get(i);
                int x = (int) p.get("x");
                int y = (int) p.get("y");
                int toward = (int) p.get("toward");
                int type = (int) p.get("type");
                int modelLength = (int) p.get("modelLength");
                int movingStatus = (int) p.get("movingStatus");
                if (i == 0)
                    gc.drawImage(player1, 64 * (type - 1) + movingStatus * 32, 32 * (toward - 1), 32, 32, y, x, modelLength, modelLength);
                else
                    gc.drawImage(player2, 64 * (type - 1) + movingStatus * 32, 32 * (toward - 1), 32, 32, y, x, modelLength, modelLength);
            }
    }

    //绘制敌人坦克
    public static void paintEnemyTank(GraphicsContext gc, List<Map<String, Object>> enemyTankData)
    {
        for (Map<String, Object> enemyTank : enemyTankData)
        {
            int x = Integer.parseInt(enemyTank.get("x").toString());
            int y = Integer.parseInt(enemyTank.get("y").toString());
            int toward = Integer.parseInt(enemyTank.get("toward").toString());
            int type = Integer.parseInt(enemyTank.get("type").toString());
            int modelLength = Integer.parseInt(enemyTank.get("modelLength").toString());
            int movingStatus = Integer.parseInt(enemyTank.get("movingStatus").toString());
            int offsetX = 0, offsetY = 0;
            switch (type)
            {
                case 1:
                    offsetX = 0;
                    break;
                case 2:
                    offsetX = 4 * 32;
                case 3:
                    offsetX = 8 * 32;
                    break;
                case 4:
                    offsetX = 12 * 32;
                    break;
                case 5:
                    offsetX = 14 * 32;
                    break;
                case 6:
                    offsetX = 16 * 32;
                    break;
                case 7:
                    offsetX = 18 * 32;
                    break;
                case 11:
                    offsetX = 2 * 32;
                    break;
                case 21:
                    offsetX = 6 * 32;
                    break;
                case 31:
                    offsetX = 10 * 32;
                    break;
                case 41:
                case 51:
                case 61:
                case 71:
                    offsetX = 20 * 32;
                    break;
            }
            gc.drawImage(enemy, offsetX + movingStatus * 32, 32 * (toward - 1) + offsetY, 32, 32, y, x, modelLength, modelLength);
        }
    }

    //绘制炮弹
    public static void paintShells(GraphicsContext gc, List<Map<String, Object>> shellData)
    {
        for (Map<String, Object> shell : shellData)
        {
            int x = Integer.parseInt(shell.get("x").toString());
            int y = Integer.parseInt(shell.get("y").toString());
            int toward = Integer.parseInt(shell.get("toward").toString());
            int type = Integer.parseInt(shell.get("type").toString());
            int modelLength = Integer.parseInt(shell.get("modelLength").toString());
            gc.drawImage(shells, 16 * (toward - 1), (type - 1) * 16, 16, 16, y, x, modelLength - 2, modelLength - 2);
        }
    }

    //绘制道具贴图
    public static void paintProps(GraphicsContext gc, int x, int y, int type, int modelLength)
    {
        gc.drawImage(props, 30 * (type - 1), 0, 30, 28, y, x, modelLength, modelLength);
    }

    //endregion

    //region  特效相关

    //绘制出生特效
    public static void paintBornAnimate(GraphicsContext gc, int offsetX, int offsetY, int x, int y, int width, int height)
    {
        gc.drawImage(born, offsetX, offsetY, 32, 32, y, x, width, height);
    }

    //绘制爆炸特效
    public static void paintExplosionAnimate(GraphicsContext gc, int offsetX, int offsetY, int x, int y, int width, int height)
    {
        gc.drawImage(explosion, offsetX, offsetY, 112, 96, y, x, width, height);
    }

    //绘制护盾特效
    public static void paintShieldAnimate(GraphicsContext gc, int offsetX, int offsetY, int x, int y, int width, int height)
    {
        gc.drawImage(shield, offsetX, offsetY, 32, 32, y, x, width, height);
    }

    //绘制击中效果特效
    public static void paintHitAnimate(GraphicsContext gc, int offsetX, int offsetY, int x, int y, int width, int height)
    {
        gc.drawImage(hit, offsetX, offsetY, 32, 32, y - 20, x - 20, width, height);
    }

    //endregion

    //region 统计面板相关

    //绘制敌人剩余数量
    public static void paintEnemyNum(GraphicsContext gc, int enemyNum)
    {
        int x = 16, y = 685;
        int offsetX, offsetY;
        for (int i = 0; i < 64; i++)
        {
            offsetX = (i / 8) * 64;
            offsetY = i % 8 * 64;
            if (enemyNum >= i + 1) gc.drawImage(enemyNumFlag, 0, 0, 58, 58, y + offsetY, x + offsetX, 48, 48);
            else paintRect(gc, "#000000", y + offsetY, x + offsetX, 48, 48);
        }
    }

    //绘制玩家剩余血量 以及 当前关卡
    public static void paintPlayerAndLevelInfo(GraphicsContext gc, int p1hp, int p2hp, int level)
    {
        paintRect(gc, "#000000", 685, 64 * 8 + 20, 64 * 8, 300);
        gc.drawImage(lifePoint, 0, 0, 58, 58, 685, 64 * 8 + 20, 48, 48);
        paintText(gc, String.valueOf(p1hp), 40, "#5ebd00", 685 + 48 + 5, 64 * 9);
        gc.drawImage(lifePoint, 0, 0, 58, 58, 685 + 3 * 48, 64 * 8 + 20, 48, 48);
        paintText(gc, String.valueOf(p2hp), 40, "#ff9933", 685 + 4 * 48 + 5, 64 * 9);
        paintText(gc, "LEVEL：" + level, 40, "#ffffff", 685, 64 * 10);
    }

    //endregion

    //region  其他

    //用于绘制游戏提示信息队列
    public static void paintGameHint(GraphicsContext gc, String hint)
    {
        paintText(gc, hint, 28, "#ff2500", 50, 50);
    }


    //endregion

}
