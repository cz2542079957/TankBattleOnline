package sample.utils;

import sample.webSocket.WebSocketController;
import sample.webSocket.WebSocketService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// 客户端定时任务处理类
public class TimedTasksProcessor extends Thread
{
    //定时任务结束符号
    public static boolean isRunning = true;
    //定时任务循环时间间隔
    public static long timeStepLength = 1 * 1000;
    //系统消息失效时间设置
    public static long hintTimeOut = 30 * 1000;

    @Override
    public void run()
    {
        while (isRunning)
        {
            try
            {
                Thread.sleep(timeStepLength);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            //清空生效的系统消息
            deleteTimeOutGameHints();
            detectionDelay();
        }

    }

    //删除超时消息
    public static void deleteTimeOutGameHints()
    {
        if (WebSocketService.gameHintsList.size() == 0) return;
        long now = new Date().getTime();
        if (now - WebSocketService.gameHintsList.get(0).createTime > hintTimeOut)
        {
            System.out.println(WebSocketService.gameHintsList.get(0).content + " 信息被移除");
            WebSocketService.gameHintsList.remove(0);
        }
    }

    //获取延迟
    public static void detectionDelay()
    {
        WebSocketController.delay = new Date().getTime();
        Map<String, Object> map = new HashMap<>();
        map.put("header", 1);
        WebSocketController.SendMessage(map);
    }
}
