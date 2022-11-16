package com.tank.networktanksserver.gameRoom;

public class PlayerScore
{
    //玩家昵称
    public String nickName;
    //玩家生命数
    public int lifePoint = 3;
    //玩家坦克等级
    public int tankLevel = 1;
    //玩家总分
    public int score = 0;
    //玩家击毁坦克情况
    public int destroyLightTank = 0;
    public int destroyWheelTank = 0;
    public int destroyMediumTank = 0;
    public int destroyHeavyTank = 0;

    public PlayerScore(String nickName)
    {
        this.nickName = nickName;
    }

    // 检查该玩家是否还有生命数
    public boolean hasLifePoint()
    {
        if (lifePoint > 0) return true;
        else return false;
    }

    // 击毁加分
    public void getHitScore(int TankType)
    {
        switch (TankType)
        {
            case 1:
            case 11:
                score += 350;
                destroyLightTank++;
                break;
            case 2:
            case 21:
                score += 550;
                destroyWheelTank++;
                break;
            case 3:
            case 31:
                score += 650;
                destroyMediumTank++;
            case 4:
            case 5:
            case 6:
            case 7:
            case 41:
            case 51:
            case 61:
            case 71:
                score += 800;
                destroyHeavyTank++;
                break;

        }
    }

    //道具加分
    public void getPropScore()
    {
        score += 300;
    }

}
