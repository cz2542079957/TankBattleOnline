package com.tank.networktanksserver.game.animation;

import com.tank.networktanksserver.gameRoom.GameRoomController;
import org.springframework.web.bind.annotation.RestController;

// 出生特效
public class Born extends Animate
{

    public Born(String id, long createTime, int x, int y, int width, int height)
    {
        super(id, createTime, x, y, width, height);
        keyFrame = 3;
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
        else if (gameRoomController.gameTime - createTime < keyFrame * 4)
            offsetX = 96;
        else if (gameRoomController.gameTime - createTime < keyFrame * 5)
            offsetX = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 6)
            offsetX = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 7)
            offsetX = 64;
        else if (gameRoomController.gameTime - createTime < keyFrame * 8)
            offsetX = 96;
        else if (gameRoomController.gameTime - createTime < keyFrame * 9)
            offsetX = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 10)
            offsetX = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 11)
            offsetX = 64;
        else if (gameRoomController.gameTime - createTime < keyFrame * 12)
            offsetX = 96;
        else if (gameRoomController.gameTime - createTime < keyFrame * 13)
            offsetX = 0;
        else if (gameRoomController.gameTime - createTime < keyFrame * 14)
            offsetX = 32;
        else if (gameRoomController.gameTime - createTime < keyFrame * 15)
            offsetX = 64;
        else if (gameRoomController.gameTime - createTime < keyFrame * 16)
            offsetX = 96;
        else destroy(gameRoomController);
    }

}
