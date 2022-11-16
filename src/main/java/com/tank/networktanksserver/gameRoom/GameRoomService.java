package com.tank.networktanksserver.gameRoom;

import com.tank.networktanksserver.game.animation.Animate;
import com.tank.networktanksserver.game.animation.AnimateFactory;
import com.tank.networktanksserver.game.animation.Shield;
import com.tank.networktanksserver.game.props.Props;
import com.tank.networktanksserver.game.shell.Shell;
import com.tank.networktanksserver.game.tank.enemyTank.EnemyTank;
import com.tank.networktanksserver.game.tank.enemyTank.EnemyTankFactory;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTank;
import com.tank.networktanksserver.game.tank.playerTank.PlayerTankFactory;
import com.tank.networktanksserver.webSocket.WebSocketController;

import javax.websocket.Session;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import java.util.*;

public class GameRoomService
{
    //region 信息传递

    //匹配成功通知
    public static void matchSuccess(String id, List<WebSocketController> playerControllerList)
    {
        Map<String, Object> response = new HashMap<>();
        response.put("header", 11);
        response.put("id", id);
        response.put("p1", playerControllerList.get(1).nickName);
        response.put("p2", playerControllerList.get(2).nickName);
        playerControllerList.get(1).sendMessage(response);
        playerControllerList.get(2).sendMessage(response);
    }

    //关卡开始通知
    public static void startLevel(int level, List<WebSocketController> playerControllerList)
    {
        Map<String, Object> response = new HashMap<>();
        response.put("header", 100);
        response.put("level", level);
        playerControllerList.get(1).sendMessage(response);
        playerControllerList.get(2).sendMessage(response);
    }

    //游戏关卡初始化数据发送
    public static void sendGameInitializesData(String id, List<WebSocketController> playerControllerList)
    {
        Map<String, Object> response = new HashMap<>();
        response.put("header", 101);
        response.put("id", id);
        playerControllerList.get(1).sendMessage(response);
        playerControllerList.get(2).sendMessage(response);
    }

    //游戏进行时数据发送
    public static void sendGameData(long gameTime, int[][] map, int enemyNum, int p1hp, int p2hp, int level, Map<Integer, PlayerTank> playerTanks, Map<String, EnemyTank> enemyTankMap, Map<String, Shell> shellMap, Map<String, Animate> animateMap, Map<String, Props> propsMap, List<WebSocketController> playerControllerList)
    {
        Map<String, Object> response = new HashMap<>();
        response.put("header", 102);
        response.put("map", map);
        response.put("enemyNum", enemyNum);
        response.put("p1hp", p1hp);
        response.put("p2hp", p2hp);
        response.put("level", level);
        //玩家坦克数据
        List<Map<String, Object>> playerTankData = new LinkedList<Map<String, Object>>();
        if (playerTanks.get(1).HP != 0 && (gameTime - playerTanks.get(1).createTime > 48))
            playerTankData.add(playerTanks.get(1).getTankLocationData());
        else playerTankData.add(null);
        if (playerTanks.get(2).HP != 0 && (gameTime - playerTanks.get(2).createTime > 48))
            playerTankData.add(playerTanks.get(2).getTankLocationData());
        else playerTankData.add(null);
        response.put("playerTankData", playerTankData);
        //敌人坦克数据
        List<Map<String, Object>> enemyTankData = new LinkedList<Map<String, Object>>();
        for (EnemyTank enemyTank : enemyTankMap.values())
        {
            if (gameTime - enemyTank.createTime > 48) enemyTankData.add(enemyTank.getTankLocationData());
        }
        response.put("enemyTankData", enemyTankData);
        //炮弹数据
        List<Map<String, Object>> shellData = new LinkedList<Map<String, Object>>();
        for (Shell shell : shellMap.values())
        {
            shellData.add(shell.getShellLocationData());
        }
        response.put("shellData", shellData);
        //特效数据
        List<Map<String, Object>> animateData = new LinkedList<Map<String, Object>>();
        for (Animate animate : animateMap.values())
        {
            if (animate.createTime <= gameTime) animateData.add(animate.getAnimeteLocationData());
        }
        response.put("animateData", animateData);
        //道具数据
        List<Map<String, Object>> propsData = new LinkedList<Map<String, Object>>();
        for (Props props : propsMap.values())
        {
            if (props.createTime <= gameTime + 100) propsData.add(props.getPropsLocationData());
        }
        response.put("propsData", propsData);
        playerControllerList.get(1).sendMessage(response);
        playerControllerList.get(2).sendMessage(response);
    }

    //玩家断线提示(队友掉线向同伴提示)
    public static void playerDisconnectHint(WebSocketController webSocketController, String gameMateNickName)
    {
        Map<String, Object> res = new HashMap<>();
        res.put("header", 103);
        res.put("content", "您的队友 " + gameMateNickName + " 与服务器断开了连接");
        webSocketController.sendMessage(res);
    }

    //游戏结算
    public static void gameOver(int timeStatus, PlayerScore p1, PlayerScore p2, int level, List<WebSocketController> playerControllerList)
    {
        Map<String, Object> res = new HashMap<>();
        res.put("header", 104);
        res.put("timeStatus", timeStatus);
        res.put("level", level);
        res.put("p1Score", p1.score);
        res.put("p1Hit", p1.destroyLightTank + "-" + p1.destroyWheelTank + "-" + p1.destroyMediumTank + "-" + p1.destroyHeavyTank);
        res.put("p2Score", p2.score);
        res.put("p2Hit", p2.destroyLightTank + "-" + p2.destroyWheelTank + "-" + p2.destroyMediumTank + "-" + p2.destroyHeavyTank);
        //
        if (playerControllerList.get(1).session.isOpen())
            playerControllerList.get(1).sendMessage(res);
        if (playerControllerList.get(2).session.isOpen())
            playerControllerList.get(2).sendMessage(res);
   }

    //游戏音频播放控制
    public static void gameAudioInfo(int type, List<WebSocketController> playerControllerList)
    {
        //1 游戏开始
        //10 开炮
        //11 击中墙壁
        //12 击中重型坦克
        //13 击中道具坦克
        //14 击毁坦克
        //15 击毁玩家坦克 基地
        //20 拾取道具
        //21 拾取加生命值道具
        //22 拾取全局炸弹道具
        //2 游戏结束
        //3 游戏统计分数

        Map<String, Object> res = new HashMap<>();
        res.put("header", 105);
        res.put("type", type);
        playerControllerList.get(1).sendMessage(res);
        playerControllerList.get(2).sendMessage(res);
    }

    //endregion

    //region 玩家消息处理

    //用户控制数据处理
    public static void playerControlInfoProcessor(Map<String, Object> msg, Session session)
    {
        String gameRoomId = msg.get("gameRoomId").toString();
        GameRoomController gameRoomController = WebSocketController.gameRoomControllerMap.get(gameRoomId);
        for (int i = 1; i <= 2; i++)
        {
            if (session == gameRoomController.playerControllerList.get(i).session)
            {
                if (gameRoomController.playerTanks.get(i).HP == 0 || gameRoomController.gameTime - gameRoomController.playerTanks.get(i).createTime < 48)
                    return;
                playerControlActuator(gameRoomController, gameRoomController.playerTanks.get(i), msg);
            }
        }
    }

    //用户操作执行器
    public static void playerControlActuator(GameRoomController grc, PlayerTank playerTank, Map<String, Object> msg)
    {
        int toward = Integer.parseInt(msg.get("toward").toString());
        //方向控制器
        if (toward != 0)
        {
            playerTank.toward = toward;
            playerTank.speed = playerTank.defaultSpeed;
        }
        //开火控制
        if (msg.get("fire").toString().equals("true"))
        {
            playerTank.openFire(grc);
        }

    }

    //endregion

    //region 游戏进程主动执行器

    //坦克运动执行器
    public static void tanksMoveActuator(int[][] map, Map<Integer, PlayerTank> playerTanks, Map<String, EnemyTank> enemyTankMap, GameRoomController grc)
    {
        //执行玩家的坦克运动
        for (int i = 1; i <= 2; i++)
            if (playerTanks.get(i).HP != 0 && grc.gameTime - playerTanks.get(i).createTime > 48)
            {
                playerTanks.get(i).move(map, playerTanks, enemyTankMap);
            }

        //执行敌人的坦克运动
        //用来判断当前是否有时间暂停道具生效
        if (grc.gameTime - grc.timeStopCreateTime > 300)
        {
            for (EnemyTank enemyTank : enemyTankMap.values())
            {
                if (grc.gameTime - enemyTank.createTime > 48)
                {
                    enemyTank.openFire(grc);
                    enemyTank.speed = enemyTank.defaultSpeed;
                    enemyTank.ChangeTowordHalfway(grc.gameTime);
                    enemyTank.move(map, playerTanks, enemyTankMap);
                }
            }
        }

    }

    //炮弹运动执行器
    public static void shellsMoveActuator(int[][] map, Map<String, Shell> shellMap, GameRoomController grc)
    {
        for (Shell shell : shellMap.values())
        {
            shell.move(map, grc);
            shell.checkHit(grc);
        }
    }

    //特效动画执行器
    public static void animateActuator(Map<String, Animate> animateMap, GameRoomController grc)
    {
        for (Animate animate : animateMap.values())
        {
            animate.play(grc);
        }

    }

    //敌人坦克自动生成器
    public static void automaticEnemyCreator(GameRoomController grc)
    {
        if (grc.enemyNum <= 0) return;
        if (grc.enemyTankMap.size() >= grc.maxEnemyNum) return;
        long createTime = grc.gameTime;
        String id = UUID.randomUUID().toString();
        int positionX[] = {24, 24, 24};
        int positionY[] = {24, 13 * 24, 25 * 24};
        int positionIndex = new Random().nextInt(3);
        int tankType = new Random().nextInt(4) + 1;
        grc.animateMap.put(id, AnimateFactory.getAnimate(1, id, createTime, positionX[positionIndex], positionY[positionIndex]));
        grc.enemyTankMap.put(id, EnemyTankFactory.getEnemyTank(tankType, id, createTime, positionX[positionIndex], positionY[positionIndex], 3));
        grc.enemyNum--;
    }

    //玩家重生执行器
    public static void playerTankRemaker(GameRoomController grc)
    {
        String id;
        for (int i = 1; i <= 2; i++)
        {
            if (grc.playerTanks.get(i).HP == 0 && grc.gameTime - grc.playerTanks.get(i).destroyTime > 50 && grc.playerScoreList.get(i).hasLifePoint())
            {
                id = UUID.randomUUID().toString();
                grc.animateMap.put(id, AnimateFactory.getAnimate(1, id, grc.gameTime, 600, 216 + (i - 1) * 192));
                grc.playerTanks.put(i, PlayerTankFactory.getPlayerTank(1, grc.gameTime, 600, 216 + (i - 1) * 192, 1));
                //重生重置玩家血量
                grc.playerScoreList.get(i).tankLevel = 1;
                id = UUID.randomUUID().toString();
                Shield shield = (Shield) AnimateFactory.getAnimate(3, id, grc.gameTime + 48, 600, 216 + (i - 1) * 192);
                shield.playerTank = grc.playerTanks.get(i);
                grc.playerTanks.get(i).shield = shield;
                grc.playerTanks.get(i).hasShield = true;
                grc.animateMap.put(id, shield);
            }
        }
    }

    //道具检查与拾取执行器
    public static void propsCheckActuator(GameRoomController grc)
    {
        for (Props props : grc.propsMap.values())
        {
            if (props.createTime < grc.gameTime - 500) props.destroy(grc);
            else if (props.createTime < grc.gameTime - 400)
            {
                props.blinkStatus++;
                if (props.blinkStatus == 8) props.blinkStatus = 0;
            }
        }
        grc.playerTanks.get(1).getPropsChecker(grc);
        grc.playerTanks.get(2).getPropsChecker(grc);
    }

    //道具特殊效果执行器
    public static void propsSpecialEffectsActuator(GameRoomController grc)
    {
        //加固基地效果
        if (grc.baseReinforce)
        {
            grc.map[26][12] = grc.map[25][12] = grc.map[24][12] = grc.map[24][13] = grc.map[24][14] = grc.map[24][15] = grc.map[25][15] = grc.map[26][15] = 4;
            grc.baseReinforceStatus = 0;
            grc.baseReinforce = false;
            grc.baseReinforceCreateTime = grc.gameTime;
        }
        if (grc.gameTime - grc.baseReinforceCreateTime == 1000)
        {
            grc.map[26][12] = grc.map[25][12] = grc.map[24][12] = grc.map[24][13] = grc.map[24][14] = grc.map[24][15] = grc.map[25][15] = grc.map[26][15] = 1;
        } else if (grc.gameTime - grc.baseReinforceCreateTime < 1000 && grc.gameTime - grc.baseReinforceCreateTime > 800)
        {
            grc.baseReinforceStatus++;
            if (grc.baseReinforceStatus == 16) grc.baseReinforceStatus = 0;
            if (grc.baseReinforceStatus > 8)
                grc.map[26][12] = grc.map[25][12] = grc.map[24][12] = grc.map[24][13] = grc.map[24][14] = grc.map[24][15] = grc.map[25][15] = grc.map[26][15] = 4;
            else
                grc.map[26][12] = grc.map[25][12] = grc.map[24][12] = grc.map[24][13] = grc.map[24][14] = grc.map[24][15] = grc.map[25][15] = grc.map[26][15] = 1;
        }

        //地图炸弹效果
        if (grc.mapBomb)
        {
            grc.mapBomb = false;
            for (EnemyTank enemyTank : grc.enemyTankMap.values())
            {
                enemyTank.destroy(grc);
            }
        }
    }

    //endregion

}
