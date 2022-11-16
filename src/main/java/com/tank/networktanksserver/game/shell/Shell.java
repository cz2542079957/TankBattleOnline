package com.tank.networktanksserver.game.shell;

import com.tank.networktanksserver.game.animation.AnimateFactory;
import com.tank.networktanksserver.game.props.PropsProcessor;
import com.tank.networktanksserver.game.tank.enemyTank.EnemyTank;
import com.tank.networktanksserver.gameRoom.GameRoomController;
import com.tank.networktanksserver.gameRoom.GameRoomService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Shell
{
    //炮弹id
    public String id;
    //碰撞模型边长
    public int modelLength = 10;
    //敌我识别  1 玩家一    2 玩家二    3 敌人
    public int type;
    //伤害值 (hit值大于一时击中撞墙减少炮弹伤害量)
    public int hit;
    //方向
    public int toward;
    //速度
    public int speed;
    //位置
    public int x, y;

    public Shell(String id, int type, int toward, int speed, int x, int y, int hit)
    {
        this.id = id;
        this.type = type;
        this.toward = toward;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.hit = hit;
    }

    public int getShellType()
    {
        if (this instanceof CommonShell) return 1;
        else if (this instanceof ApShell) return 2;
        else if (this instanceof ElectromagneticShell) return 3;
        else return 1;
    }

    //打包当前炮弹数据用于用户端绘制
    public Map<String, Object> getShellLocationData()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("toward", toward);
        data.put("type", getShellType());
        data.put("modelLength", modelLength);
        return data;
    }

    public boolean moveChecker(int[][] map)
    {
        int correction = 3; //碰撞模型边界容错度
        // 方向向上
        if (toward == 1)
        {
            if (checkAndHitBlock(map, x, y) || checkAndHitBlock(map, x, y + modelLength)) return false;
        } else if (toward == 2)
        {
            if (checkAndHitBlock(map, x, y + modelLength) || checkAndHitBlock(map, x + modelLength, y + modelLength))
                return false;
        } else if (toward == 3)
        {
            if (checkAndHitBlock(map, x + modelLength, y) || checkAndHitBlock(map, x + modelLength, y + modelLength))
                return false;
        } else if (toward == 4)
        {
            if (checkAndHitBlock(map, x, y) || checkAndHitBlock(map, x + modelLength, y)) return false;
        } else return false;  //一般情况下不会发生这情况
        return true;
    }

    public void move(int[][] map, GameRoomController gameRoomController)
    {
        if (moveChecker(map))
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
        } else
        {
            String animateId = UUID.randomUUID().toString();
            gameRoomController.animateMap.put(animateId, AnimateFactory.getAnimate(4, animateId, gameRoomController.gameTime, x, y));
            if (hit <= 0)
            {
                // 只有玩家开炮声音
                if (type == 1 || type == 2)
                    GameRoomService.gameAudioInfo(11, gameRoomController.playerControllerList);
                destroy(gameRoomController);
            }
        }
    }

    //击中物体
    public boolean checkAndHitBlock(int[][] map, int x, int y)
    {
        int shellType = getShellType();
        int nx = x / 24, ny = y / 24;
        if (nx <= 0 || ny <= 0 || nx >= 27 || ny >= 27)
        {
            hit = 0;
            return true;
        }
        if (map[nx][ny] == 1)
        {
            if (toward == 1 || toward == 3)
            {
                if (ny % 2 == 1) map[nx][ny] = map[nx][ny + 1] = 0;
                else map[nx][ny] = map[nx][ny - 1] = 0;
            } else
            {
                if (nx % 2 == 1) map[nx][ny] = map[nx + 1][ny] = 0;
                else map[nx][ny] = map[nx - 1][ny] = 0;
            }
            hit--;
            return true;
        } else if (map[nx][ny] == 4)
        {
            if (shellType == 2 || shellType == 3)
            {
                if (toward == 1 || toward == 3)
                {
                    if (ny % 2 == 1) map[nx][ny] = map[nx][ny + 1] = 0;
                    else map[nx][ny] = map[nx][ny - 1] = 0;
                } else
                {
                    if (nx % 2 == 1) map[nx][ny] = map[nx + 1][ny] = 0;
                    else map[nx][ny] = map[nx - 1][ny] = 0;
                }

            }
            hit -= 2;
            return true;
        } else if (map[nx][ny] == 9)
        {
            //炮弹击中基地 游戏结束
            map[25][13] = map[25][14] = map[26][13] = map[26][14] = 10;
            hit = 0;
            return true;
        }
        return false;
    }

    //判断是否击中坦克或者炮弹
    public void checkHit(GameRoomController gameRoomController)
    {
        int cx = x + modelLength / 2;
        int cy = y + modelLength / 2;
        //判断击中玩家坦克
        for (int i = 1; i <= 2; i++)
        {
            if (gameRoomController.playerTanks.get(i).HP == 0 || gameRoomController.playerTanks.get(i).createTime > gameRoomController.gameTime - 48)
                continue;
            if ((type == 1 && i == 1) || (type == 2 && i == 2)) continue;
            int distance = (int) Math.sqrt(Math.pow(cx - (gameRoomController.playerTanks.get(i).x + 24), 2) + Math.pow(cy - (gameRoomController.playerTanks.get(i).y + 24), 2));
            if (distance < 28)
            {
                if (gameRoomController.playerTanks.get(i).hasShield)
                {
                    gameRoomController.playerTanks.get(i).shield.destroy(gameRoomController);
                    gameRoomController.playerTanks.get(i).shield = null;
                    gameRoomController.playerTanks.get(i).hasShield = false;
                } else
                {
                    gameRoomController.playerTanks.get(i).destroy(gameRoomController);
                }
                String animateId = UUID.randomUUID().toString();
                gameRoomController.animateMap.put(animateId, AnimateFactory.getAnimate(4, animateId, gameRoomController.gameTime, x, y));
                this.destroy(gameRoomController);
            }
        }

        //以下只需要判断玩家的炮弹即可 避免重复
        //判断击中敌人
        if (this.type == 3) return;
        for (EnemyTank enemyTank : gameRoomController.enemyTankMap.values())
        {
            if (enemyTank.createTime > gameRoomController.gameTime - 48) continue;
            int distance = (int) Math.sqrt(Math.pow(cx - (enemyTank.x + 24), 2) + Math.pow(cy - (enemyTank.y + 24), 2));
            if (distance < 28)
            {
                int temp = enemyTank.HP;
                enemyTank.HP -= hit;
                hit -= temp;
                if (enemyTank.hasAward)
                {
                    GameRoomService.gameAudioInfo(13, gameRoomController.playerControllerList);
                    enemyTank.hasAward = false;
                    PropsProcessor.propsCreater(gameRoomController);
                }
                if (enemyTank.HP <= 0)
                {
                    gameRoomController.playerScoreList.get(type).getHitScore(enemyTank.getTankType());
                    enemyTank.destroy(gameRoomController);
                } else
                {   //说明还没击毁敌方坦克
                    GameRoomService.gameAudioInfo(12, gameRoomController.playerControllerList);
                }
                String animateId = UUID.randomUUID().toString();
                gameRoomController.animateMap.put(animateId, AnimateFactory.getAnimate(4, animateId, gameRoomController.gameTime, x, y));
                if (hit <= 0)
                {
                    this.destroy(gameRoomController);
                }
            }
        }

        //判断击中其他炮弹
        for (Shell hitShell : gameRoomController.shellMap.values())
        {
            if (this.type == hitShell.type) continue;
            if ((int) Math.sqrt(Math.pow(cx - (hitShell.x + 5), 2) + Math.pow(cy - (hitShell.y + 5), 2)) <= 10)
            {
                int temp = hitShell.hit;
                hitShell.hit -= hit;
                hit -= temp;
                if (hitShell.hit <= 0) hitShell.destroy(gameRoomController);
                if (hit <= 0)
                {
                    this.destroy(gameRoomController);
                }
            }
        }
    }


    public void destroy(GameRoomController gameRoomController)
    {
        gameRoomController.shellMap.remove(id);
        try
        {
            finalize();
        } catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

}
