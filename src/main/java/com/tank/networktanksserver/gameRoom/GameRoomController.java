package com.tank.networktanksserver.gameRoom;

import com.tank.networktanksserver.game.animation.Animate;
import com.tank.networktanksserver.game.animation.AnimateFactory;
import com.tank.networktanksserver.game.animation.Shield;
import com.tank.networktanksserver.game.props.Props;
import com.tank.networktanksserver.game.props.PropsProcessor;
import com.tank.networktanksserver.game.shell.Shell;
import com.tank.networktanksserver.game.tank.enemyTank.EnemyTank;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTank;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTankFactory;
import com.tank.networktanksserver.utils.DbService;
import com.tank.networktanksserver.utils.MapUtils;
import com.tank.networktanksserver.webSocket.WebSocketController;
import com.tank.networktanksserver.webSocket.WebSocketService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class GameRoomController implements Runnable
{
    //房间id
    public String id;
    //房间状态
    public boolean status = true;
    //游戏状态
    public boolean gameStatus = false;
    //玩家连接状态
    public boolean[] playerConnectionStatus = {true, true, true};
    //玩家会话
    public List<WebSocketController> playerControllerList = new CopyOnWriteArrayList<WebSocketController>();
    //玩家当局游戏数据
    public List<PlayerScore> playerScoreList = new CopyOnWriteArrayList<PlayerScore>();
    //时间线
    public long gameTime = 0;
    //当前关卡
    public int level = 1;
    //当前地图
    public int[][] map = new int[28][28];
    //当前关卡敌人剩余未出现数量
    public int enemyNum = 5;
    //最大场上敌人数量
    int maxEnemyNum = 3;
    //玩家坦克容器
    public Map<Integer, PlayerTank> playerTanks = new ConcurrentHashMap<>();
    //场上敌人坦克容器
    public Map<String, EnemyTank> enemyTankMap = new ConcurrentHashMap<>();
    //场上炮弹容器
    public Map<String, Shell> shellMap = new ConcurrentHashMap<>();
    //场上特效动画容器
    public Map<String, Animate> animateMap = new ConcurrentHashMap<>();
    //道具容器
    public Map<String, Props> propsMap = new ConcurrentHashMap<>();

    //时间停止道具使用时间
    public long timeStopCreateTime = -0xfffffff;
    //加固基地道具
    public boolean baseReinforce = false;
    public int baseReinforceStatus;
    public long baseReinforceCreateTime = -0xfffffff;
    //地图炸弹
    public boolean mapBomb = false;


    public GameRoomController(WebSocketController p1, WebSocketController p2)
    {
        id = String.valueOf(new Date().getTime());
        p1.status = 2;
        p2.status = 2;
        this.playerControllerList.add(0, null);
        this.playerControllerList.add(1, p1);
        this.playerControllerList.add(2, p2);
        playerScoreList.add(0, null);
        playerScoreList.add(1, new PlayerScore(p1.nickName));
        playerScoreList.add(2, new PlayerScore(p2.nickName));
        WebSocketController.gameRoomControllerMap.put(id, this);
    }

    @Override
    public void run()
    {
        System.out.println("游戏线程 " + Thread.currentThread().getName() + " 正在运行");
        //通知匹配成功
        GameRoomService.matchSuccess(id, playerControllerList);
        System.out.println(id + " 房间游戏开始 " + status);

        //主要游戏循环
        while (status)
        {
            System.out.println(id + " 房间游戏开始新战局 " + level);
            newStart();
            sleep(2000);
            //通知当前关卡
            GameRoomService.gameAudioInfo(1, playerControllerList);
            GameRoomService.startLevel(this.level, playerControllerList);
            sleep(3000);

            for (int i = 0; i < 10; i++)
                PropsProcessor.propsCreater(this);

            //发送房间初始化信息
            GameRoomService.sendGameInitializesData(id, playerControllerList);
            gameStatus = true;
            while (gameStatus)
            {
                gameTime++;
                // 玩家坦克重生
                GameRoomService.playerTankRemaker(this);
                GameRoomService.automaticEnemyCreator(this);
                GameRoomService.tanksMoveActuator(map, playerTanks, enemyTankMap, this);
                GameRoomService.propsCheckActuator(this);
                GameRoomService.shellsMoveActuator(map, shellMap, this);
                GameRoomService.animateActuator(animateMap, this);
                GameRoomService.propsSpecialEffectsActuator(this);
                GameRoomService.sendGameData(gameTime, map, enemyNum, playerScoreList.get(1).lifePoint, playerScoreList.get(2).lifePoint, level, playerTanks, enemyTankMap, shellMap, animateMap, propsMap, playerControllerList);
                sleep(30);
                //判断是否两个玩家状态
                playerStatusCheck();
                nextLevel();
                gameOver();
            }
        }

        for (int i = 0; i <= 30; i++)
        {
            sleep(150);
            GameRoomService.gameOver(i, playerScoreList.get(1), playerScoreList.get(2), level, playerControllerList);
        }

        //游戏结束结算
        for (int i = 1; i <= 2; i++)
        {
            String nickName = playerScoreList.get(i).nickName;
            int score = playerScoreList.get(i).score;
            int destroyLightTank = playerScoreList.get(i).destroyLightTank;
            int destroyWheelTank = playerScoreList.get(i).destroyWheelTank;
            int destroyMediumTank = playerScoreList.get(i).destroyMediumTank;
            int destroyHeavyTank = playerScoreList.get(i).destroyHeavyTank;
            int level = this.level;
            int destroy = destroyLightTank + destroyWheelTank + destroyHeavyTank + destroyMediumTank;
            DbService.updatePlayerInfo(nickName, score, destroyLightTank, destroyWheelTank, destroyMediumTank, destroyHeavyTank, destroy, level);
        }
        //五秒后重新进入
        sleep(5000);
        playerControllerList.get(1).status = 10;
        playerControllerList.get(2).status = 10;
        playerControllerList.get(1).sendMessage(DbService.playerInfoGetter(playerControllerList.get(1).nickName));
        playerControllerList.get(2).sendMessage(DbService.playerInfoGetter(playerControllerList.get(2).nickName));
        WebSocketService.closeGameRoom(id);
    }

    //回合开始
    public void newStart()
    {
        //重置时间
        gameTime = 0;
        //创建关卡地图
        map = MapUtils.createMap();
        //初始化该关卡的敌人数量
        enemyNum = level * 2 + 5;
        maxEnemyNum = (int) Math.sqrt(level) + 2;
        //还原数据状态
        animateMap.clear();
        enemyTankMap.clear();
        shellMap.clear();
        propsMap.clear();
        //还原道具状态
        timeStopCreateTime = -0xfffffff;
        baseReinforce = false;
        baseReinforceStatus = 0;
        baseReinforceCreateTime = -0xfffffff;
        mapBomb = false;
        //生成玩家
        String id;
        for (int i = 1; i <= 2; i++)
        {
            if (playerScoreList.get(i).hasLifePoint())
            {
                playerTanks.put(i, PlayerTankFactory.getPlayerTank(playerScoreList.get(i).tankLevel, gameTime, 600, 216 + (i - 1) * 192, 1));
                id = UUID.randomUUID().toString();
                animateMap.put(id, AnimateFactory.getAnimate(1, id, gameTime, 600, 216 + (i - 1) * 192));
                id = UUID.randomUUID().toString();
                Shield shield = (Shield) AnimateFactory.getAnimate(3, id, gameTime + 48, 600, 216 + (i - 1) * 192);
                shield.playerTank = playerTanks.get(i);
                playerTanks.get(i).shield = shield;
                playerTanks.get(i).hasShield = true;
                animateMap.put(id, shield);
            }
        }
    }

    //下一关
    public void nextLevel()
    {
        if (enemyNum == 0 && enemyTankMap.size() == 0)
        {
            level++;
            playerScoreList.get(1).tankLevel = playerTanks.get(1).getTankType();
            playerScoreList.get(2).tankLevel = playerTanks.get(2).getTankType();
            gameStatus = false;
        }
    }

    //游戏结束
    public void gameOver()
    {
        //基地被摧毁 或者 两名玩家生命值都为零
        if (map[25][13] == 10 || (!playerScoreList.get(1).hasLifePoint() && !playerScoreList.get(2).hasLifePoint()))
        {
            //播放游戏结束音乐
            GameRoomService.gameAudioInfo(2, playerControllerList);
            gameStatus = false;
            status = false;
        }
    }

    //玩家状态检查
    public void playerStatusCheck()
    {
        //两名玩家都断线
        if (playerControllerList.get(1).lostConnectionTime != -1 && playerControllerList.get(2).lostConnectionTime != -1)
        {
            gameStatus = false;
            status = false;
            playerControllerList.get(1).gameRoomController = null;
            playerControllerList.get(2).gameRoomController = null;
            WebSocketService.closeGameRoom(id);
            return;
        }
        //只有其中一个玩家断线
        for (int i = 1; i <= 2; i++)
        {
            if (playerControllerList.get(i).lostConnectionTime != -1)
            {
                //避免重复发送
                if (!playerConnectionStatus[i]) return;
                playerConnectionStatus[i] = false;
                String nickName = playerControllerList.get(i).nickName;
                GameRoomService.playerDisconnectHint(i == 1 ? playerControllerList.get(2) : playerControllerList.get(1), nickName);
                return;
            }
        }
        playerConnectionStatus[1] = playerConnectionStatus[2] = true;
    }

    public void sleep(int time)
    {
        try
        {
            Thread.sleep(time);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}


