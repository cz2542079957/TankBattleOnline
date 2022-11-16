package com.tank.networktanksserver.game.tank.enemyTank;

public class EnemyWheelTank extends EnemyTank
{
    public EnemyWheelTank(String id, long createTime, int x, int y, int toward, boolean award)
    {
        this.id = id;
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.hasAward = award;
        this.defaultSpeed = 6;
        this.shellType = 1;
        this.shellSpeed = 10;
        this.fireReload = 70;
        this.towordChangeProbability = 6;
        this.lastFireTime = createTime ;
        this.towordChangeTime = createTime;
    }

}
