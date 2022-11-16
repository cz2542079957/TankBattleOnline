package com.tank.networktanksserver;

import com.tank.networktanksserver.timedTasks.TimedTasksController;
import com.tank.networktanksserver.utils.DbService;
import com.tank.networktanksserver.utils.ExternalConfigurationUtils;
import com.tank.networktanksserver.webSocket.WebSocketController;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.tank.networktanksserver"})
public class NetworkTanksServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(NetworkTanksServerApplication.class, args);
        ExternalConfigurationUtils.init();
        new TimedTasksController().start();
    }



}
