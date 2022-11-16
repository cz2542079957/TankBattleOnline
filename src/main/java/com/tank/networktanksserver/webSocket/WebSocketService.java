package com.tank.networktanksserver.webSocket;

import com.tank.networktanksserver.gameRoom.GameRoomController;
import com.tank.networktanksserver.gameRoom.GameRoomService;
import com.tank.networktanksserver.timedTasks.TimedTasksController;
import com.tank.networktanksserver.utils.DbService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.util.*;

@Service
public class WebSocketService
{
    //客户端请求处理器
    public static void SocketMessageProcessor(Map<String, Object> msg, Session session)
    {
        int header = (int) msg.get("header");
        //用户登录
        if (header == 0)
        {
            String nickName = msg.get("userNickName").toString();
            String password = msg.get("userPassword").toString();
            Map<String, Object> req = new HashMap<>();
            WebSocketController webSocketController = webSocketControllerQuerier(session);
            if (StringUtils.isEmpty(nickName) || StringUtils.isEmpty(password))
            {
                req.put("header", 0);
                req.put("res", 1);
                webSocketController.sendMessage(req);
                return;
            }
            DbService.playerLoginValidator(nickName, password, webSocketController);
        }
        //延迟测算
        else if (header == 1)
        {
            Map<String, Object> response = new HashMap<>();
            response.put("header", 1);
            WebSocketController webSocketController = webSocketControllerQuerier(session);
            if (webSocketController != null)
            {
                webSocketController.sendMessage(response);
            }
        }
        //用户注册
        else if (header == 2)
        {
            String nickName = msg.get("userNickName").toString();
            String password = msg.get("userPassword").toString();
            Map<String, Object> req = new HashMap<>();
            WebSocketController webSocketController = webSocketControllerQuerier(session);
            if (StringUtils.isEmpty(nickName) || StringUtils.isEmpty(password))
            {
                req.put("header", 2);
                req.put("res", 1);
                webSocketController.sendMessage(req);
                return;
            }
            DbService.playerSigninValidator(nickName, password, webSocketController);
        }
        //开始匹配
        else if (header == 4)
        {
            WebSocketController webSocketController = webSocketControllerQuerier(session);
            webSocketController.status = 1;
            reconnect(webSocketController.nickName, webSocketController);
        }
        //用户操作处理
        else if (header == 102)
        {
            GameRoomService.playerControlInfoProcessor(msg, session);
        }
    }

    //webSocketController查询器
    public static WebSocketController webSocketControllerQuerier(Session session)
    {
        for (WebSocketController webSocketController : WebSocketController.webSocketControllerSet)
        {
            if (webSocketController.session == session)
            {
                return webSocketController;
            }
        }
        return null;
    }

    //根据状态查询 webSocketController 列表
    public static List<WebSocketController> webSocketControllerQuerierByStatus(int status)
    {
        List<WebSocketController> list = new ArrayList<>();
        for (WebSocketController wsc : WebSocketController.webSocketControllerSet)
        {
            if (wsc.status == status) list.add(wsc);
        }
        return list;
    }

    //重连
    public static boolean reconnect(String nickName, WebSocketController wsc)
    {
        if (WebSocketController.reconnectionSet.size() == 0) return false;
        for (WebSocketController webSocketController : WebSocketController.reconnectionSet)
        {
            if (webSocketController.nickName.equals(nickName) && WebSocketController.gameRoomControllerMap.containsKey(webSocketController.gameRoomController.id))
            {
                for (int i = 1; i <= 2; i++)
                {
                    if (webSocketController.gameRoomController.playerControllerList.get(i) == webSocketController)
                    {
                        webSocketController.gameRoomController.playerControllerList.set(i, wsc);
                    }
                }
                wsc.nickName = nickName;
                wsc.status = 2;
                wsc.gameRoomController = webSocketController.gameRoomController;
                WebSocketController.reconnectionSet.remove(webSocketController);
                Map<String, Object> response = new HashMap<>();
                response.put("header", 101);
                response.put("id", wsc.gameRoomController.id);
                wsc.sendMessage(response);
                return true;
            }
        }
        return false;
    }

    //游戏终止
    public static void closeGameRoom(String roomId)
    {
        GameRoomController gameRoomController = WebSocketController.gameRoomControllerMap.get(roomId);
        TimedTasksController.gameThreadPool.remove(gameRoomController);
        WebSocketController.gameRoomControllerMap.remove(roomId);
    }

}
