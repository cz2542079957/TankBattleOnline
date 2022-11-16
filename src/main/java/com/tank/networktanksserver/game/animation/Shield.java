package com.tank.networktanksserver.game.animation;

import com.tank.networktanksserver.game.tank.playerTank.PlayerTank;
import com.tank.networktanksserver.gameRoom.GameRoomController;

//护罩特效
public class Shield extends Animate
{
    public PlayerTank playerTank;

    public Shield(String id, long createTime, int x, int y, int width, int height)
    {
        super(id, createTime, x, y, width, height);
    }

    @Override
    public void play(GameRoomController gameRoomController)
    {
        if (gameRoomController.gameTime - createTime < keyFrame)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 2)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 3)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 4)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 5)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 6)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 7)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 8)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 9)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 10)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 11)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 12)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 13)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 14)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 15)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 16)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 17)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 18)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 19)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 20)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 21)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 22)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 23)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 24)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 25)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 26)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 27)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 28)
            offsetY = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 29)
            offsetY = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 30)
            offsetY = 32;
        else
        {
            playerTank.hasShield = false;
            playerTank.shield = null;
            destroy(gameRoomController);
        }
        ;
    }
}
