package com.tank.networktanksserver.game.tank;

import com.tank.networktanksserver.game.tank.enemyTank.EnemyTank;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTank;

import java.util.Map;

public interface Move
{
    //检查是否可以移动
    boolean moveChecker(int[][] map,  Map<Integer, PlayerTank> playerTanks, Map<String, EnemyTank> enemyTankMap);

    //开始下一步位移
    void move(int[][] map, Map<Integer, PlayerTank> playerTanks, Map<String, EnemyTank> enemyTankMap);
}
