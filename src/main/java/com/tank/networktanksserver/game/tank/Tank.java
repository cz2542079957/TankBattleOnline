package com.tank.networktanksserver.game.tank;

import com.tank.networktanksserver.game.tank.enemyTank.EnemyTank;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTank;

import java.util.HashMap;
import java.util.Map;

public abstract class Tank implements Fire, Move
{
    //坦克模型物理边长
    public int modelLength = 48;
    //坦克生成时间
    public long createTime;
    //坦克生命值
    public int HP = 1;
    //坦克当前位置
    public int x, y;
    //坦克 默认速度/速度
    public int defaultSpeed, speed;
    //坦克方向
    public int toward;
    //坦克 炮弹类型
    public int shellType;
    //坦克 炮弹速度
    public int shellSpeed;
    //坦克 炮弹伤害量
    public int shellHit = 1;
    //坦克 默认炮弹数量/当前炮弹数量 （可发射）
    public int defaultShellNum, shellNum;
    //坦克上一次开炮时间
    public long lastFireTime = 0;
    //开炮时间间隔/最小时间间隔
    public long fireReload, fireMiniReload;
    //处于移动状态
    public int movingStatus = 0;

    //判断地图上面某一个点的位移位置是否有障碍
    public boolean checkBlock(int[][] map, int x, int y, int offsetX, int offsetY)
    {
        int nx = (int) Math.floor((x + offsetX) / 24);
        int ny = (int) Math.floor((y + offsetY) / 24);
        if (nx <= 0 || ny <= 0 || nx >= 27 || ny >= 27)
        {
            return true;
        }
        if (map[nx][ny] == 1 || map[nx][ny] == 3 || map[nx][ny] == 4 || map[nx][ny] == 9 || map[nx][ny] == 10)
        {
            return true;
        }
        // 光滑地砖
        else if (map[nx][ny] == 5)
        {
            this.speed = this.defaultSpeed + 3 ;
            return false;
        }

        return false;    // 存在障碍 返回true
    }

    //获取坦克类型
    public abstract int getTankType();


    //打包当前坦克数据用于用户端绘制
    public Map<String, Object> getTankLocationData()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("toward", toward);
        data.put("type", getTankType());
        data.put("modelLength", modelLength);
        data.put("movingStatus", movingStatus);
        return data;
    }

    //判断地图上该位置的玩家是否会阻碍
    public boolean checkHinderPlayerTank(Map<Integer, PlayerTank> playerTanks)
    {
        int correction = 44;
        for (int i = 1; i <= 2; i++)
        {
            if (playerTanks.get(i).HP == 0 || this == playerTanks.get(i)) continue;
            if (toward == 1 && (x - defaultSpeed - playerTanks.get(i).x <= correction) && (x - defaultSpeed - playerTanks.get(i).x >= 0) && (Math.abs(y - playerTanks.get(i).y) <= correction))
            {
                return true;
            } else if (toward == 2 && (playerTanks.get(i).y - (y + defaultSpeed) <= correction) && (playerTanks.get(i).y - (y + defaultSpeed) >= 0) && (Math.abs(x - playerTanks.get(i).x) <= correction))
            {
                return true;
            } else if (toward == 3 && (playerTanks.get(i).x - (x + defaultSpeed) <= correction) && (playerTanks.get(i).x - (x + defaultSpeed) >= 0) && (Math.abs(y - playerTanks.get(i).y) <= correction))
            {
                return true;
            } else if (toward == 4 && (y - defaultSpeed - playerTanks.get(i).y <= correction) && (y - defaultSpeed - playerTanks.get(i).y >= 0) && (Math.abs(x - playerTanks.get(i).x) <= correction))
            {
                return true;
            }
        }
        return false;
    }

    //判断地图上该位置的敌人是否会阻碍
    public boolean checkHinderEnemyTank(Map<String, EnemyTank> enemyTankMap)
    {
        int correction = 45;
        for (EnemyTank enemyTank : enemyTankMap.values())
        {
            if (this == enemyTank) continue;
            if (toward == 1 && (x - defaultSpeed - enemyTank.x <= correction) && (x - defaultSpeed - enemyTank.x >= 0) && (Math.abs(y - enemyTank.y) <= correction))
            {
                return true;
            } else if (toward == 2 && (enemyTank.y - (y + defaultSpeed) <= correction) && (enemyTank.y - (y + defaultSpeed) >= 0) && (Math.abs(x - enemyTank.x) <= correction))
            {
                return true;
            } else if (toward == 3 && (enemyTank.x - (x + defaultSpeed) <= correction) && (enemyTank.x - (x + defaultSpeed) >= 0) && (Math.abs(y - enemyTank.y) <= correction))
            {
                return true;
            } else if (toward == 4 && (y - defaultSpeed - enemyTank.y <= correction) && (y - defaultSpeed - enemyTank.y >= 0) && (Math.abs(x - enemyTank.x) <= correction))
            {
                return true;
            }
        }
        return false;
    }

    //综合碰撞检测
    @Override
    public boolean moveChecker(int[][] map, Map<Integer, PlayerTank> playerTanks, Map<String, EnemyTank> enemyTankMap)
    {
        int correction = 5; //碰撞模型边界容错度
        // 物体碰撞检测
        if (toward == 1)
        {
            if (checkBlock(map, x, y + correction, -defaultSpeed / 2, 0)
                    || checkBlock(map, x, y + modelLength / 2, -defaultSpeed / 2, 0)
                    || checkBlock(map, x, y + modelLength - correction, -defaultSpeed / 2, 0))
                return false;
        } else if (toward == 2)
        {
            if (checkBlock(map, x + correction, y + modelLength, 0, defaultSpeed / 2)
                    || checkBlock(map, x + modelLength / 2, y + modelLength, 0, defaultSpeed / 2)
                    || checkBlock(map, x + modelLength - correction, y + modelLength, 0, defaultSpeed / 2))
                return false;
        } else if (toward == 3)
        {
            if (checkBlock(map, x + modelLength, y + correction, defaultSpeed / 2, 0)
                    || checkBlock(map, x + modelLength, y + modelLength / 2, defaultSpeed / 2, 0)
                    || checkBlock(map, x + modelLength, y + modelLength - correction, defaultSpeed / 2, 0))
                return false;
        } else if (toward == 4)
        {
            if (checkBlock(map, x + correction, y, 0, -defaultSpeed / 2)
                    || checkBlock(map, x + modelLength / 2, y, 0, -defaultSpeed / 2)
                    || checkBlock(map, x + modelLength - correction, y, 0, -defaultSpeed / 2))
                return false;
        } else return false;  //一般情况下不会发生这情况

        //坦克碰撞检测
        if (checkHinderPlayerTank(playerTanks)) return false;
        if (checkHinderEnemyTank(enemyTankMap)) return false;

        return true;
    }


}
