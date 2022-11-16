package com.tank.networktanksserver.timedTasks;

import com.tank.networktanksserver.gameRoom.GameRoomController;
import com.tank.networktanksserver.webSocket.WebSocketController;
import com.tank.networktanksserver.webSocket.WebSocketService;

import java.util.*;

public class TimedTasksService
{
    public static long reconnectTimeOut = 1000 * 60;

    //更新等待界面数据
    public static void updateWaitingHallData()
    {
        List<WebSocketController> list = WebSocketService.webSocketControllerQuerierByStatus(1);
        Map<String, Object> response = new HashMap<>();
        int count = list.size();
        response.put("header", 10);
        response.put("count", count);
        for (WebSocketController wsc : list)
        {
            wsc.sendMessage(response);
        }
    }

    //服务器匹配
    public static void matchTeammate()
    {
        List<WebSocketController> list = WebSocketService.webSocketControllerQuerierByStatus(1);
        if (list.size() < 2) return;
        if (TimedTasksController.gameThreadPool.getActiveCount() >= TimedTasksController.gameThreadPool.getMaximumPoolSize())
        {
            System.out.println("游戏线程池资源无空闲，当前运行线程数：" + TimedTasksController.gameThreadPool.getActiveCount() + ",当前等待队列人数:" + list.size());
            return;
        }
        System.out.println("匹配成功:P1-" + list.get(0).nickName + "  P2-" + list.get(1).nickName);
        GameRoomController gameRoomController = new GameRoomController(list.get(0), list.get(1));
        TimedTasksController.gameThreadPool.execute(gameRoomController);
        list.get(0).gameRoomController = gameRoomController;
        list.get(1).gameRoomController = gameRoomController;
    }

    //断线重连列表清理
    public static List<String> ClearReconnectionSet()
    {
        long time = new Date().getTime();
        List<String> ret = new LinkedList<>();
        for (WebSocketController wsc : WebSocketController.reconnectionSet)
        {
            if (time - wsc.lostConnectionTime > reconnectTimeOut || wsc.gameRoomController == null)
            {
                ret.add(wsc.session.toString());
                WebSocketController.reconnectionSet.remove(wsc);
            }
        }
        return ret;
    }
}
