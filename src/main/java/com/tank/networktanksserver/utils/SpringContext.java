package com.tank.networktanksserver.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContext implements ApplicationContextAware
{
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

}