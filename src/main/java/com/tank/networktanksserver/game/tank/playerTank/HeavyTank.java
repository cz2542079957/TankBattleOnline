package com.tank.networktanksserver.game.tank.playerTank;

import com.tank.networktanksserver.gameRoom.GameRoomController;

public class HeavyTank extends PlayerTank
{
    //是否开了第二炮
    public boolean doubleFire = false;


    public HeavyTank(long createTime, int x, int y, int toward)
    {
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.toward = toward;
        this.defaultSpeed = 6;
        this.shellType = 2;
        this.shellSpeed = 15;
        this.shellHit = 2;
        this.fireReload = 40;
        this.fireMiniReload = 5;
        this.lastFireTime = -this.fireReload;
    }

    @Override
    public boolean FireChecker(GameRoomController gameRoomController)
    {
        if (gameRoomController.gameTime - lastFireTime > fireReload)
        {
            doubleFire = false;
            return true;
        }
        if (doubleFire)
            return false;
        else
        {
            if (gameRoomController.gameTime - lastFireTime > fireMiniReload)
            {
                doubleFire = true;
                return true;
            } else return false;
        }
    }

}
