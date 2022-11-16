package com.tank.networktanksserver.game.props;

import com.tank.networktanksserver.gameRoom.GameRoomController;

import java.util.HashMap;
import java.util.Map;

//道具类
public class Props
{
    //id
    public String id;
    //坐标
    public int x, y;
    //类型
    public int type;
    //模型边长
    public int modelLength = 48;
    //创建时间
    public long createTime;
    //消失前闪烁状态
    public int blinkStatus = 0;

    public Props(String id, int x, int y, int type, long createTime)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
        this.createTime = createTime;
    }

    //获取道具方位信息
    public Map<String, Object> getPropsLocationData()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("type", type);
        data.put("modelLength", modelLength);
        data.put("blinkStatus", blinkStatus);
        return data;
    }

    //销毁道具
    public void destroy(GameRoomController gameRoomController)
    {
        gameRoomController.propsMap.remove(id);
        try
        {
            finalize();
        } catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }


}
