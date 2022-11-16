package com.tank.networktanksserver.timedTasks;

public class TimedTasksConfig
{
    //服务器计划任务更新间隔
    public static long updateInterval = 1000;
    //服务器匹配时间周期间隔
    public static int matchTeammateInterval = 5;
    //服务器清理重连列表时间周期
    public static int clearReconnectionSetInterval = 1;
    //服务器核心游戏线程数量
    public static int coreThreadCount = 5;
    //服务器最大游戏线程数量
    public static int maxThreadCount = 10;
    //服务器游戏线程存活时间
    public static int gameThreadAliveTime = 10; //单位分钟

}
