package com.tank.networktanksserver.game.tank;

import com.tank.networktanksserver.gameRoom.GameRoomController;

public interface Fire
{
    //检查是否可以开火
    boolean FireChecker(GameRoomController gameRoomController);

    //开火
    void openFire(GameRoomController gameRoomController);

}
