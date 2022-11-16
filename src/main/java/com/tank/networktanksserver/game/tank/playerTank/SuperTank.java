package com.tank.networktanksserver.game.tank.playerTank;

public class SuperTank extends PlayerTank
{
    public SuperTank(long createTime, int x, int y, int toward)
    {
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.defaultSpeed = 7;
        this.shellType = 3;
        this.shellSpeed = 24;
        this.shellHit = 20;
        this.fireReload = 35;
        this.lastFireTime = -this.fireReload;
    }
}
