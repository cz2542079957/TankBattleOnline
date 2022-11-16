package com.tank.networktanksserver.webSocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.tank.networktanksserver.gameRoom.GameRoomController;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket")
@Component
public class WebSocketController
{
    //当前总人数
    public static int onlineCount = 0;
    //用户会话列表
    public static CopyOnWriteArraySet<WebSocketController> webSocketControllerSet = new CopyOnWriteArraySet<WebSocketController>();
    //用户会话重连列表
    public static CopyOnWriteArraySet<WebSocketController> reconnectionSet = new CopyOnWriteArraySet<WebSocketController>();
    //游戏房间列表
    public static Map<String, GameRoomController> gameRoomControllerMap = new HashMap<>();


    //用户会话
    public Session session = null;
    //用户昵称 (具有唯一性,主要用于游戏重连)
    public String nickName;
    //用户当前状态 0未登录  10正在大厅  1正在匹配  2游戏中  3结算
    public int status = 0;
    //用户断线时间
    public long lostConnectionTime = -1;
    //用户断线前的游戏房间
    public GameRoomController gameRoomController = null;


    @OnOpen
    public void onOpen(Session session)
    {
        this.session = session;
        webSocketControllerSet.add(this);
        System.out.println("A new connection has been established！The current number of online users is:" + getOnlineCount());
    }

    @OnClose
    public void onClose()
    {
        webSocketControllerSet.remove(this);
        System.out.println("A connection has been closed！The current number of online users is:" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session)
    {
        Map<String, Object> msg = (Map<String, Object>) JSONObject.parseObject(message, Map.class);
        WebSocketService.SocketMessageProcessor(msg, session);
    }

    @OnError
    public void onError(Session session, Throwable error)
    {
        System.out.println("An error occurred, the remote host forcibly closed a connection" + error);
    }

    //发送数据
    public void sendMessage(Map<String, Object> response)
    {
        synchronized (session)
        {
            if (this.lostConnectionTime != -1) return;
            if (!session.isOpen())
            {
                onConnectLost();
                return;
            }
            String jsonString = JSON.toJSONString(response);
            try
            {
                session.getBasicRemote().sendText(jsonString);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    }

    //断线方案
    public void onConnectLost()
    {
        this.lostConnectionTime = new Date().getTime();
        status = 0;
        reconnectionSet.add(this);
    }

    public static synchronized int getOnlineCount()
    {
        return webSocketControllerSet.size();
    }


}