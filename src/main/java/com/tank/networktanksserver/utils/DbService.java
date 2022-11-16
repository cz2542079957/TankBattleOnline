package com.tank.networktanksserver.utils;

import com.tank.networktanksserver.webSocket.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//数据库服务类
@Service
public class DbService
{
    public static JdbcTemplate jdbcTemplate;

    @Autowired
    public DbService(JdbcTemplate jdbc)
    {
        jdbcTemplate = jdbc;
    }

    public static Date date = new Date();

    //用户名验证 true->存在
    public static boolean playerNicknameValidator(String nickName)
    {
        String sql = "select count(*) from player where nickName = ?";
        int count = jdbcTemplate.queryForObject(sql, int.class, nickName);
        return count != 0;
    }

    //用户名、密码验证
    public static void playerLoginValidator(String nickName, String password, WebSocketController webSocketController)
    {
        String sql = "select count(*) from player where nickName = ? and password =  ?";
        Map<String, Object> req = new HashMap<>();
        if (jdbcTemplate.queryForObject(sql, int.class, nickName, password) != 0)
        {
            req.put("header", 0);
            req.put("res", 0);
            webSocketController.sendMessage(req);
            webSocketController.status = 10;
            webSocketController.nickName = nickName;
            webSocketController.sendMessage(playerInfoGetter(nickName));
        } else
        {   //用户名或者密码错误
            req.put("header", 0);
            req.put("res", 2);
            webSocketController.sendMessage(req);
        }

    }

    //注册
    public static void playerSigninValidator(String nickName, String password, WebSocketController webSocketController)
    {
        Map<String, Object> req = new HashMap<>();
        if (playerNicknameValidator(nickName))
        {   //用户名已存在
            req.put("header", 2);
            req.put("res", 2);
            webSocketController.sendMessage(req);
            return;
        }
        String sql = "insert into player(nickName,password,create_time) values(?,?,?)";
        if (jdbcTemplate.update(sql, nickName, password, date.getTime()) != 0)
        {
            req.put("header", 2);
            req.put("res", 0);
            webSocketController.status = 10;
            webSocketController.nickName = nickName;
            webSocketController.sendMessage(playerInfoGetter(nickName));
        } else
        {
            req.put("header", 2);
            req.put("res", 3);
        }
        webSocketController.sendMessage(req);
    }

    //用户资料获取
    public static Map<String, Object> playerInfoGetter(String nickName)
    {
        String sql = "select * from player where nickName = ?";
        Map<String, Object> req = jdbcTemplate.queryForMap(sql, nickName);
        req.put("header", 3);
        return req;
    }

    public static void updatePlayerInfo(String nickName, int score, int destroyLightTank, int destroyWheelTank, int destroyMediumTank, int destroyHeavyTank, int destroy, int level)
    {
        String sql = "update player set total_battels = total_battels + 1, total_score = total_score + ? ," +
                "destroyLightTank = destroyLightTank + ? ,destroyWheelTank = destroyWheelTank  + ? ," +
                "destroyMediumTank = destroyMediumTank + ?,destroyHeavyTank = destroyHeavyTank + ?, " +
                " max_level = GREATEST(max_level,?),max_score = GREATEST(max_score , ?), max_destroy = GREATEST(max_destroy, ?)" +
                " where nickName = ? ";
        jdbcTemplate.update(sql, score, destroyLightTank, destroyWheelTank, destroyMediumTank, destroyHeavyTank, level, score, destroy, nickName);
    }
}
