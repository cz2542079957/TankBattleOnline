package com.tank.networktanksserver.game.tank.enemyTank;

// 敌方轻型坦克
public class EnemyLightTank extends EnemyTank
{
    public EnemyLightTank(String id, long createTime, int x, int y, int toward, boolean award)
    {
        this.id = id;
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.hasAward = award;
        this.defaultSpeed = 4;
        this.shellType = 1;
        this.shellSpeed = 10;
        this.fireReload = 60;
        this.towordChangeProbability = 6;
        this.lastFireTime = createTime ;
        this.towordChangeTime = createTime;
    }
}
