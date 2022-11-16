package com.tank.networktanksserver.game.tank.playerTank;

import com.tank.networktanksserver.game.animation.AnimateFactory;
import com.tank.networktanksserver.game.animation.Shield;
import com.tank.networktanksserver.game.props.Props;
import com.tank.networktanksserver.game.props.PropsProcessor;
import com.tank.networktanksserver.game.shell.ShellFactory;
import com.tank.networktanksserver.game.tank.Tank;
import com.tank.networktanksserver.game.tank.enemyTank.EnemyTank;
import com.tank.networktanksserver.gameRoom.GameRoomController;
import com.tank.networktanksserver.gameRoom.GameRoomService;

import java.util.Map;
import java.util.UUID;

public class PlayerTank extends Tank
{
    //是否有有效护盾
    public boolean hasShield = false;
    //有效护盾实例
    public Shield shield = null;
    //被摧毁时间
    public long destroyTime;


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
        GameRoomService.gameAudioInfo(10, gameRoomController.playerControllerList);
        //炮弹初始位置偏移量
        int[] offsetX = {0, -24, 0, 24, 0};
        int[] offsetY = {0, 0, 24, 0, -24};
        for (int i = 1; i <= 2; i++)
        {
            if (gameRoomController.playerTanks.get(i) == this)
            {
                gameRoomController.shellMap.put(shellId, ShellFactory.getShell(this.shellType, shellId, i, toward,
                        shellSpeed, x + (modelLength - 8) / 2 + offsetX[toward],
                        y + (modelLength - 8) / 2 + offsetY[toward], shellHit));
            }
        }
        lastFireTime = gameRoomController.gameTime;
    }

    @Override
    public void move(int[][] map, Map<Integer, PlayerTank> playerTanks, Map<String, EnemyTank> enemyTankMap)
    {
        if (speed == 0) return;
        if (moveChecker(map, playerTanks, enemyTankMap))
        {
            //实现移动方法
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
            movingStatus = movingStatus == 0 ? 1 : 0;
            //护盾跟随
            if (hasShield && shield != null)
            {
                shield.x = this.x;
                shield.y = this.y;
            }

        }
        speed = 0;  // 实时速度归零
    }

    @Override
    public int getTankType()
    {
        if (this instanceof InitialTank) return 1;
        else if (this instanceof LightTank) return 2;
        else if (this instanceof MediumTank) return 3;
        else if (this instanceof HeavyTank) return 4;
        else if (this instanceof SuperTank) return 5;
        else return 1;
    }

    //拾取道具检测
    public void getPropsChecker(GameRoomController gameRoomController)
    {
        int correction = 45;
        boolean flag;
        for (Props props : gameRoomController.propsMap.values())
        {
            flag = false;
            if (toward == 1 && (x - defaultSpeed - props.x <= correction) && (x - defaultSpeed - props.x >= 0) && (Math.abs(y - props.y) <= correction))
            {
                flag = true;
            } else if (toward == 2 && (props.y - (y + defaultSpeed) <= correction) && (props.y - (y + defaultSpeed) >= 0) && (Math.abs(x - props.x) <= correction))
            {
                flag = true;
            } else if (toward == 3 && (props.x - (x + defaultSpeed) <= correction) && (props.x - (x + defaultSpeed) >= 0) && (Math.abs(y - props.y) <= correction))
            {
                flag = true;
            } else if (toward == 4 && (y - defaultSpeed - props.y <= correction) && (y - defaultSpeed - props.y >= 0) && (Math.abs(x - props.x) <= correction))
            {
                flag = true;
            }
            if (flag)
            {
                gameRoomController.propsMap.remove(props.id);
                switch (props.type)
                {
                    case 1:
                        PropsProcessor.AddHPProp(gameRoomController, this);
                        break;
                    case 2:
                        PropsProcessor.timeStop(gameRoomController);
                        break;
                    case 3:
                        PropsProcessor.baseReinforce(gameRoomController);
                        break;
                    case 4:
                        PropsProcessor.MapBomb(gameRoomController);
                        break;
                    case 5:
                        PropsProcessor.upgradeTank(gameRoomController, this);
                        break;
                    case 6:
                        PropsProcessor.tankShield(gameRoomController, this);
                        break;
                    default:
                        break;
                }
                for (int i = 1; i <= 2; i++)
                {
                    if (gameRoomController.playerTanks.get(i) == this)
                    {
                        gameRoomController.playerScoreList.get(i).getPropScore();
                        break;
                    }
                }
            }
        }
    }

    //玩家坦克毁灭
    public void destroy(GameRoomController gameRoomController)
    {
        GameRoomService.gameAudioInfo(15, gameRoomController.playerControllerList);
        for (int i = 1; i <= 2; i++)
        {
            if (gameRoomController.playerTanks.get(i) == this)
            {
                gameRoomController.playerTanks.get(i).HP = 0;
                gameRoomController.playerTanks.get(i).destroyTime = gameRoomController.gameTime;
                gameRoomController.playerScoreList.get(i).lifePoint--;
                String animateId = UUID.randomUUID().toString();
                gameRoomController.animateMap.put(animateId, AnimateFactory.getAnimate(2, animateId, gameRoomController.gameTime, gameRoomController.playerTanks.get(i).x, gameRoomController.playerTanks.get(i).y));
                return;
            }
        }
        try
        {
            finalize();
        } catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

}

