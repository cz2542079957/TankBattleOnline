package com.tank.networktanksserver.game.animation;

public class AnimateFactory
{
    public static Animate getAnimate(int type, String id, long createTime, int x, int y)
    {
        switch (type)
        {
            case 1:
                return new Born(id, createTime, x, y, 48, 48);
            case 2:
                return new Explosion(id, createTime, x - 16, y - 16, 80, 80);
            case 3:
                return new Shield(id, createTime, x - 1, y - 1, 50, 50);
            case 4:
                return new Hit(id, createTime, x, y, 48, 48);
            default:
                return null;
        }
    }

}
