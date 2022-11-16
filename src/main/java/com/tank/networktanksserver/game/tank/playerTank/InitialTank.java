package com.tank.networktanksserver.game.tank.playerTank;

public class InitialTank extends PlayerTank
{
    public InitialTank(long createTime, int x, int y, int toward)
    {
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.defaultSpeed = 5;
        this.shellType = 1;
        this.shellSpeed = 12;
        this.fireReload = 40;
        this.lastFireTime = -this.fireReload;
    }

}
