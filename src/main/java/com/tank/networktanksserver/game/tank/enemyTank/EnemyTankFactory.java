package com.tank.networktanksserver.game.tank.enemyTank;

import java.util.Random;

public class EnemyTankFactory
{
    // 1为轻型坦克  2为轮式战车  3为中型坦克  4为重型坦克
    //传入type 为1 2 3 4 时自动随机生成携带奖励的坦克
    public static EnemyTank getEnemyTank(int type, String id, long createTime, int x, int y, int toward)
    {
        EnemyTank enemyTank = null;
        switch (type)
        {
            case 1:
                enemyTank = new EnemyLightTank(id, createTime, x, y, toward, randomAward(1));
                break;
            case 2:
                enemyTank = new EnemyWheelTank(id, createTime, x, y, toward, randomAward(2));
                break;
            case 3:
                enemyTank = new EnemyMediumTank(id, createTime, x, y, toward, randomAward(2));
                break;
            case 4:
                enemyTank = new EnemyHeavyTank(id, createTime, x, y, toward, new Random().nextInt(4) + 1, randomAward(3));
                break;
            case 11:
                enemyTank = new EnemyLightTank(id, createTime, x, y, toward, true);
                break;
            case 21:
                enemyTank = new EnemyWheelTank(id, createTime, x, y, toward, true);
                break;
            case 31:
                enemyTank = new EnemyMediumTank(id, createTime, x, y, toward, true);
                break;
            case 41:
            case 51:
            case 61:
            case 71:
                enemyTank = new EnemyHeavyTank(id, createTime, x, y, toward,(type / 10 - 3),true);

        }
        return enemyTank;
    }

    //概率生成携带奖励的坦克
    public static boolean randomAward(int probability)
    {
        if (new Random().nextInt(10) < probability) return true;
        return false;
    }

}
