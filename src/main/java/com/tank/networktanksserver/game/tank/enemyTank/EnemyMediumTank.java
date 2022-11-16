package com.tank.networktanksserver.game.tank.enemyTank;

// 敌方中型坦克
public class EnemyMediumTank extends EnemyTank
{
    public EnemyMediumTank(String id, long createTime, int x, int y, int toward, boolean award)
    {
        this.id = id;
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.hasAward = award;
        this.defaultSpeed = 5;
        this.shellType = 1;
        this.shellSpeed = 13;
        this.fireReload = 45;
        this.towordChangeProbability = 3;
        this.lastFireTime = createTime ;
        this.towordChangeTime = createTime;
    }
}
