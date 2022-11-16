package com.tank.networktanksserver.timedTasks;

import java.util.concurrent.*;

//服务器计划任务控制器
public class TimedTasksController extends Thread
{
    //游戏线程池
    public static ThreadPoolExecutor gameThreadPool = new ThreadPoolExecutor(TimedTasksConfig.coreThreadCount, TimedTasksConfig.maxThreadCount,TimedTasksConfig.gameThreadAliveTime, TimeUnit.MINUTES,new ArrayBlockingQueue<Runnable>(TimedTasksConfig.maxThreadCount), Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardPolicy());

    @Override
    public void run()
    {
        super.run();
        long runTime = 0;  //运行时间标记
        while (true)
        {
            try
            {
                Thread.sleep(TimedTasksConfig.updateInterval);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            runTime++;
            TimedTasksService.updateWaitingHallData();
            //匹配
            if (runTime % TimedTasksConfig.matchTeammateInterval == 0)
                TimedTasksService.matchTeammate();
            //清理重连列表
            if (runTime % TimedTasksConfig.clearReconnectionSetInterval == 0)
                TimedTasksService.ClearReconnectionSet();
        }

    }
}
