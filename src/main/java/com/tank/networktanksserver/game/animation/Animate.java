package com.tank.networktanksserver.game.animation;

import com.tank.networktanksserver.gameRoom.GameRoomController;

import java.util.HashMap;
import java.util.Map;

public abstract class Animate
{
    //id
    public String id;
    //创建时间
    public long createTime;
    //位置
    public int x, y;
    //偏移
    public int offsetX, offsetY;
    //大小
    public int width, height;
    //帧 单位时长
    public long keyFrame = 6;

    public Animate(String id, long createTime, int x, int y, int width, int height)
    {
        this.id = id;
        this.createTime = createTime;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //动画播放
    public abstract void play(GameRoomController gameRoomController);

    //获取当前动画类型
    public int getAnimateType()
    {
        int type;
        if (this instanceof Born) type = 1;
        else if (this instanceof Explosion) type = 2;
        else if (this instanceof Shield) type = 3;
        else if (this instanceof Hit) type = 4;
        else type = 1;
        return type;
    }

    //获取当前特效的方位数据
    public Map<String, Object> getAnimeteLocationData()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("type", getAnimateType());
        data.put("x", x);
        data.put("y", y);
        data.put("offsetX", offsetX);
        data.put("offsetY", offsetY);
        data.put("width", width);
        data.put("height", height);
        return data;
    }

    //销毁当前动画
    public void destroy(GameRoomController gameRoomController)
    {
        synchronized (gameRoomController.animateMap)
        {
            gameRoomController.animateMap.remove(id);
        }

    }

}

