package com.tank.networktanksserver.game.tank.playerTank;

public class PlayerTankFactory
{
    public static PlayerTank getPlayerTank(int level, long createTime, int x, int y, int toward)
    {
        PlayerTank playerTank = null;
        switch (level)
        {
            case 1:
                playerTank = new InitialTank(createTime, x, y, toward);
                break;
            case 2:
                playerTank = new LightTank(createTime, x, y, toward);
                break;
            case 3:
                playerTank = new MediumTank(createTime, x, y, toward);
                break;
            case 4:
                playerTank = new HeavyTank(createTime, x, y, toward);
                break;
            case 5:
                playerTank = new SuperTank(createTime, x, y, toward);
                break;
            default:
                break;
        }
        return playerTank;
    }

}
