package com.tank.networktanksserver.game.tank.enemyTank;

import com.tank.networktanksserver.game.animation.AnimateFactory;
import com.tank.networktanksserver.game.shell.ShellFactory;
import com.tank.networktanksserver.game.tank.Tank;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTank;
import com.tank.networktanksserver.gameRoom.GameRoomController;
import com.tank.networktanksserver.gameRoom.GameRoomService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class EnemyTank extends Tank
{
    //坦克当前id
    public String id;
    //判断是否是带有道具奖励的坦克
    public boolean hasAward;
    //中途改变方向的概率
    public int towordChangeProbability;
    //中途改变方向的时间
    public long towordChangeTime = 0;

    @Override
    public boolean FireChecker(GameRoomController gameRoomController)
    {
        if (gameRoomController.gameTime - lastFireTime < fireReload) return false;
        return true;
    }

    @Override
    public void openFire(GameRoomController gameRoomController)
    {
        if (!FireChecker(gameRoomController)) return;
        String shellId = UUID.randomUUID().toString();
        //炮弹初始位置偏移量
        int[] offsetX = {0, -24, 0, 24, 0};
        int[] offsetY = {0, 0, 24, 0, -24};
        gameRoomController.shellMap.put(shellId, ShellFactory.getShell(this.shellType, shellId, 3, toward, shellSpeed,
                x + (modelLength - 8) / 2 + offsetX[toward], y + (modelLength - 8) / 2 + offsetY[toward], 1));

        lastFireTime = gameRoomController.gameTime;
    }

    @Override
    public void move(int[][] map, Map<Integer, PlayerTank> playerTanks, Map<String, EnemyTank> enemyTankMap)
    {
        if (speed == 0) return;
        //没有障碍
        if (moveChecker(map, playerTanks, enemyTankMap))
        {
            switch (toward)
            {
                case 1:
                    x -= speed;
                    break;
                case 2:
                    y += speed;
                    break;
                case 3:
                    x += speed;
                    break;
                case 4:
                    y -= speed;
                    break;
            }
            movingStatus = movingStatus == 0 ? 1 : 0;   //用于显示动态图
        }
        // 有障碍则改变方向
        else
        {
            changeToward();
        }
        speed = 0;  // 实时速度归零
    }

    //改变方向
    public void changeToward()
    {
        //随机生成方向
        int newToward = new Random().nextInt(4) + 1;
        toward = newToward;
    }

    //中途改变方向
    public void ChangeTowordHalfway(long gameTime)
    {
        if (gameTime - towordChangeTime > 150)
        {
            if (new Random().nextInt(10) < towordChangeProbability)
                changeToward();
            else towordChangeTime += 30;
        }

    }

    //获取坦克类型  1 轻型  2 轮战  3 中型  4~7 重型   11 轻型奖励   21 轮战奖励  31 中型奖励    41 51 61 71 重型奖励
    @Override
    public int getTankType()
    {
        int type;
        if (this instanceof EnemyLightTank) type = 1;
        else if (this instanceof EnemyWheelTank) type = 2;
        else if (this instanceof EnemyMediumTank) type = 3;
        else if (this instanceof EnemyHeavyTank)
        {
            if (HP == 1)
                type = 4;
            else if (HP == 2)
                type = 5;
            else if (HP == 3) type = 6;
            else type = 7;
        } else type = 1;


        // 判断是否携带奖励
        if (hasAward)
            type = type * 10 + 1;
        return type;
    }

    //打包当前坦克数据用于用户端绘制
    @Override
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

    //销毁坦克
    public void destroy(GameRoomController gameRoomController)
    {
        GameRoomService.gameAudioInfo(14, gameRoomController.playerControllerList);
        String animateId = UUID.randomUUID().toString();
        gameRoomController.animateMap.put(animateId, AnimateFactory.getAnimate(2, animateId, gameRoomController.gameTime,
                gameRoomController.enemyTankMap.get(id).x, gameRoomController.enemyTankMap.get(id).y));
        gameRoomController.enemyTankMap.remove(id);
        try
        {
            finalize();
        } catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

}
