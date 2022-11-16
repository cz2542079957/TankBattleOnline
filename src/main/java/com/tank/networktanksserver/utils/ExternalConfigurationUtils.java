package com.tank.networktanksserver.utils;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ExternalConfigurationUtils
{
    public static void init()
    {
        try
        {
            String url = new File("").getAbsolutePath() + File.separator + "config.properties";
            properties.load(new FileInputStream(url));
        } catch (Exception e)
        {
            System.out.println("外部配置文件工具类初始化失败");
        }
    }

    public static Properties properties = new Properties();

    public static String getValue(String key)
    {
        if (properties.containsKey(key))
            return properties.getProperty(key);
        else return null;
    }


}
