package sample.webSocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import sample.dialog.GameDialog;
import sample.dialog.Login;
import sample.utils.ExternalConfigurationUtils;
import sample.utils.TimedTasksProcessor;

public class WebSocketController implements Runnable
{
    //    public static String ws = "ws://www.channelcz.top:1234/websocket";
    public static String ws = "ws://localhost:1234/websocket";
    public static WebSocketClient client = null;
    public static WebSocketController webSocketController = new WebSocketController();
    public static Login login;
    public static GameDialog gameDialog;
    public static TimedTasksProcessor timedTasksProcessor = new TimedTasksProcessor();
    //服务器延迟
    public static long delay = 0;
    //当前游玩的房间钥匙
    public static String gameRoomId;
    //玩家按键情况 1上 2右 3下 4左 开火
    public static boolean w = false, d = false, s = false, a = false, space = false;


    @Override
    public void run()
    {
        //从外部文件读取websocket连接地址
        if (ExternalConfigurationUtils.getValue("websocket") != null)
            ws = ExternalConfigurationUtils.getValue("websocket");
        timedTasksProcessor.start();
        clientConnectInit();
    }

    //获取单例
    public static WebSocketController getInstance()
    {
        return webSocketController;
    }

    //建立连接
    public static void clientConnectInit()
    {
        try
        {
            login.setStatusText("正在建立连接...");
            login.setReconnetButtonShow(false);
            client = new WebSocketClient(new URI(ws), new Draft_6455())
            {
                @Override
                public void onOpen(ServerHandshake arg0)
                {
                    System.out.println("连接成功");
                    Platform.runLater(() ->
                    {
                        login.setStatusText("连接成功");
                        login.setReconnetButtonShow(false);
                        Login.status = 1;
                    });
                }

                @Override
                public void onMessage(String message)
                {
                    JSONObject jsonObject = JSONObject.parseObject(message);
                    Map<String, Object> msg = (Map<String, Object>) jsonObject;
                    WebSocketService.getInstance().SocketMessageProcessor(msg);
                }

                @Override
                public void onError(Exception arg0)
                {
                    arg0.printStackTrace();
                    System.out.println("发生错误已关闭" + arg0.toString());
                    Platform.runLater(() ->
                    {
                        login.setStatusText("与游戏服务器连接失败，请重新连接");
                        login.setReconnetButtonShow(true);
                        Login.status = 0;
                    });

                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2)
                {
                    System.out.println("连接已关闭" + arg0 + " " + arg1);
                    Platform.runLater(() ->
                    {
                        login.closeDelay();
                        login.setReconnetButtonShow(true);
                        login.setStatusText("连接中断");
                        Login.status = 0;
                    });

                }

            };
            client.connect();//连接
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    //向服务器发送消息
    public static void SendMessage(Map<String, Object> msg)
    {
        if (!client.getReadyState().equals(ReadyState.OPEN))
        {
            System.out.println("连接中断");
            return;
        }
        String jsonString = JSON.toJSONString(msg);
        client.send(jsonString);
    }


}


