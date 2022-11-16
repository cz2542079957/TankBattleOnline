package com.tank.networktanksserver.game.tank.enemyTank;

// 敌方重型坦克
public class EnemyHeavyTank extends EnemyTank
{
    public EnemyHeavyTank(String id, long createTime, int x, int y, int toward, int hp, boolean award)
    {
        this.id = id;
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.HP = hp;
        this.hasAward = award;
        this.defaultSpeed = 3;
        this.shellType = 1;
        this.shellSpeed = 12;
        this.fireReload = 60;
        this.towordChangeProbability = 1;
        this.lastFireTime = createTime;
        this.towordChangeTime = createTime;
    }

}
