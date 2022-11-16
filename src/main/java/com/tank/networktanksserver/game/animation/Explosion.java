package com.tank.networktanksserver.game.animation;

import com.tank.networktanksserver.gameRoom.GameRoomController;

public class Explosion extends Animate
{
    public Explosion(String id, long createTime, int x, int y, int width, int height)
    {
        super(id, createTime, x, y, width, height);
        keyFrame = 1;
    }

    @Override
    public void play(GameRoomController gameRoomController)
    {
        if (gameRoomController.gameTime - createTime < keyFrame)
        {
            offsetX = 0;
            offsetY = 0;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 2)
        {
            offsetX = 112;
            offsetY = 0;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 3)
        {
            offsetX = 112 * 2;
            offsetY = 0;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 4)
        {
            offsetX = 112 * 3;
            offsetY = 0;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 5)
        {
            offsetX = 0;
            offsetY = 96;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 6)
        {
            offsetX = 112;
            offsetY = 96;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 7)
        {
            offsetX = 112 * 2;
            offsetY = 96;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 8)
        {
            offsetX = 112 * 3;
            offsetY = 96;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 9)
        {
            offsetX = 0;
            offsetY = 96 * 2;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 10)
        {
            offsetX = 112;
            offsetY = 96 * 2;
        } else if (gameRoomController.gameTime - createTime < keyFrame * 11)
        {
            offsetX = 112 * 2;
            offsetY = 96 * 2;
        } else destroy(gameRoomController);
    }
}
