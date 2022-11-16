package com.tank.networktanksserver.game.shell;

import com.tank.networktanksserver.game.animation.AnimateFactory;
import com.tank.networktanksserver.game.props.PropsProcessor;
import com.tank.networktanksserver.game.tank.enemyTank.EnemyTank;
import com.tank.networktanksserver.gameRoom.GameRoomController;

import java.util.UUID;

public class ElectromagneticShell extends Shell
{
    public ElectromagneticShell(String id, int type, int toward, int speed, int x, int y, int hit)
    {
        super(id, type, toward, speed, x, y, hit);
    }

    @Override
    public boolean checkAndHitBlock(int[][] map, int x, int y)
    {
        int nx = x / 24, ny = y / 24;
        if (nx <= 0 || ny <= 0 || nx >= 27 || ny >= 27)
        {
            hit = 0;
            return true;
        }
        //可以毁坏除了河流外的所有障碍
        if (map[nx][ny] == 1 || map[nx][ny] == 2 || map[nx][ny] == 5)
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
        {   //
            if (toward == 1 || toward == 3)
            {
                if (ny % 2 == 1) map[nx][ny] = map[nx][ny + 1] = 0;
                else map[nx][ny] = map[nx][ny - 1] = 0;
            } else
            {
                if (nx % 2 == 1) map[nx][ny] = map[nx + 1][ny] = 0;
                else map[nx][ny] = map[nx - 1][ny] = 0;
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

    @Override
    public void checkHit(GameRoomController gameRoomController)
    {
        //获取中心坐标
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
            if (distance < 35)
            {
                int temp = enemyTank.HP;
                enemyTank.HP -= hit;
                hit -= temp;
                if (enemyTank.hasAward)
                {
                    enemyTank.hasAward = false;
                    PropsProcessor.propsCreater(gameRoomController);
                }
                if (enemyTank.HP <= 0)
                {
                    gameRoomController.playerScoreList.get(type).getHitScore(enemyTank.getTankType());
                    enemyTank.destroy(gameRoomController);
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
            // 撞击其他炮弹
            if ((int) Math.sqrt(Math.pow(cx - (hitShell.x + 5), 2) + Math.pow(cy - (hitShell.y + 5), 2)) <= 12)
            {
                int temp = hitShell.hit;
                hitShell.hit -= hit;
                hit -= temp;
                if (hitShell.hit <= 0) hitShell.destroy(gameRoomController);
                if (hit <= 0) this.destroy(gameRoomController);
            }
            // 其他炮弹速度全部除以二
            else if ((int) Math.sqrt(Math.pow(cx - (hitShell.x + 5), 2) + Math.pow(cy - (hitShell.y + 5), 2)) <= 100)
            {
                if (hitShell.speed <= 8) continue;  //  避免重复减速
                hitShell.speed /= 2;
            }
        }
    }

    //重新计算速度
    public void checkSpeed()
    {
        speed = (int) Math.floor(28 * (hit + 5) / 25f);
    }

    @Override
    public void move(int[][] map, GameRoomController gameRoomController)
    {
        checkSpeed();
        super.move(map, gameRoomController);
    }

    @Override
    public void destroy(GameRoomController gameRoomController)
    {
        //炮弹
        int nx = (int) Math.floor(x + (modelLength / 2f) / 24);
        int ny = (int) Math.floor(y + (modelLength / 2f) / 24);
        //爆炸影响距离
        int effectDistance = 100;
        for (EnemyTank enemyTank : gameRoomController.enemyTankMap.values())
        {
            if (Math.pow(x - enemyTank.x, 2) + Math.pow(y - enemyTank.y, 2) < effectDistance * effectDistance)
            {
                enemyTank.HP--;
                if (enemyTank.HP <= 0)
                    enemyTank.destroy(gameRoomController);
            }
        }

        String animateId = UUID.randomUUID().toString();
        gameRoomController.animateMap.put(animateId, AnimateFactory.getAnimate(2, animateId, gameRoomController.gameTime, x - 18, y - 18));
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
