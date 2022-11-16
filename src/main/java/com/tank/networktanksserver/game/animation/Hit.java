package com.tank.networktanksserver.game.animation;

import com.tank.networktanksserver.gameRoom.GameRoomController;

public class Hit extends Animate
{

    public Hit(String id, long createTime, int x, int y, int width, int height)
    {
        super(id, createTime, x, y, width, height);
        keyFrame = 1;
    }

    @Override
    public void play(GameRoomController gameRoomController)
    {
        if (gameRoomController.gameTime - createTime < keyFrame)
            offsetX = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 2)
            offsetX = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 3)
            offsetX = 64;
        else
            destroy(gameRoomController);
    }
}
