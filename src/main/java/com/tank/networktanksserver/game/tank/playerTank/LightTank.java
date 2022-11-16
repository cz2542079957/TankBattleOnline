package com.tank.networktanksserver.game.tank.playerTank;

public class LightTank extends PlayerTank
{
    public LightTank(long createTime, int x, int y, int toward)
    {
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.defaultSpeed = 6;
        this.shellType = 1;
        this.shellSpeed = 15;
        this.fireReload = 30;
        this.lastFireTime = -this.fireReload;
    }
}
