package com.tank.networktanksserver.game.props;

import com.tank.networktanksserver.game.animation.AnimateFactory;
import com.tank.networktanksserver.game.animation.Shield;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTank;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTankFactory;
import com.tank.networktanksserver.gameRoom.GameRoomController;
import com.tank.networktanksserver.gameRoom.GameRoomService;

import java.util.Random;
import java.util.UUID;

public class PropsProcessor
{
    // 道具地图 中的元素
//    1 生命道具
//    2 时间冻结
//    3 基地加固
//    4 地图炸弹
//    5 坦克升级
//    6 坦克护盾
    public static Random random = new Random();

    //道具生成器
    public static void propsCreater(GameRoomController gameRoomController)
    {
        boolean flag = true;
        while (flag)
        {
            int x = random.nextInt(25) + 1;
            int y = random.nextInt(25) + 1;
            if (gameRoomController.map[x][y] == 3 || gameRoomController.map[x][y] == 9) continue;
            int type = random.nextInt(6) + 1;
//            type = 5;
            String id = UUID.randomUUID().toString();
            gameRoomController.propsMap.put(id, new Props(id, x * 24, y * 24, type, gameRoomController.gameTime));
            flag = false;
        }

    }

    //生命加成
    public static void AddHPProp(GameRoomController gameRoomController, PlayerTank playerTank)
    {
        GameRoomService.gameAudioInfo(21, gameRoomController.playerControllerList);
        for (int i = 1; i <= 2; i++)
        {
            if (playerTank == gameRoomController.playerTanks.get(i))
            {
                gameRoomController.playerScoreList.get(i).lifePoint++;
                return;
            }
        }
    }

    //时间冻结
    public static void timeStop(GameRoomController gameRoomController)
    {
        GameRoomService.gameAudioInfo(20, gameRoomController.playerControllerList);
        gameRoomController.timeStopCreateTime = gameRoomController.gameTime;
    }

    //基地加固
    public static void baseReinforce(GameRoomController gameRoomController)
    {
        GameRoomService.gameAudioInfo(20, gameRoomController.playerControllerList);
        gameRoomController.baseReinforce = true;
    }

    //地图炸弹
    public static void MapBomb(GameRoomController gameRoomController)
    {
        GameRoomService.gameAudioInfo(22, gameRoomController.playerControllerList);
        gameRoomController.mapBomb = true;
    }

    //坦克升级
    public static void upgradeTank(GameRoomController gameRoomController, PlayerTank playerTank)
    {
        GameRoomService.gameAudioInfo(20, gameRoomController.playerControllerList);
        //玩家是否有护盾
        boolean hasShield = false;
        Shield shield = null;
        if (playerTank.hasShield)
        {
            hasShield = true;
            shield = playerTank.shield;
        }
        //判断是玩家几
        int playerIndex = 1;
        for (int i = 1; i <= 2; i++)
        {
            if (gameRoomController.playerTanks.get(i) == playerTank)
            {
                playerIndex = i;
                break;
            }
        }
        int type = playerTank.getTankType();
        switch (type)
        {
            case 1:
                playerTank = PlayerTankFactory.getPlayerTank(2, gameRoomController.gameTime - 48, playerTank.x,
                        playerTank.y, playerTank.toward);
                break;
            case 2:
                playerTank = PlayerTankFactory.getPlayerTank(3, gameRoomController.gameTime - 48, playerTank.x,
                        playerTank.y, playerTank.toward);
                break;
            case 3:
                playerTank = PlayerTankFactory.getPlayerTank(4, gameRoomController.gameTime - 48, playerTank.x,
                        playerTank.y, playerTank.toward);
                break;
            case 4:
                playerTank = PlayerTankFactory.getPlayerTank(5, gameRoomController.gameTime - 48, playerTank.x,
                        playerTank.y, playerTank.toward);
                break;
            default:
                break;
        }
        if (hasShield)
        {
            playerTank.hasShield = true;
            playerTank.shield = shield;
            shield.playerTank = playerTank;
        }
        gameRoomController.playerTanks.put(playerIndex, playerTank);
    }

    //坦克护盾
    public static void tankShield(GameRoomController gameRoomController, PlayerTank playerTank)
    {
        GameRoomService.gameAudioInfo(20, gameRoomController.playerControllerList);
        if (playerTank.hasShield)
        {
            playerTank.shield.destroy(gameRoomController);
            playerTank.shield = null;
            playerTank.hasShield = false;
        }
        String id = UUID.randomUUID().toString();
        Shield shield = (Shield) AnimateFactory.getAnimate(3, id, gameRoomController.gameTime, playerTank.x, playerTank.y);
        shield.playerTank = playerTank;
        playerTank.hasShield = true;
        playerTank.shield = shield;
        gameRoomController.animateMap.put(id, shield);
    }
}
