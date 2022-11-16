package com.tank.networktanksserver.game.shell;

public class ShellFactory
{
    public static Shell getShell(int shellType, String id, int type, int toward, int speed, int x, int y, int hit)
    {
        switch (shellType)
        {
            case 1:
                return new CommonShell(id, type, toward, speed, x, y, hit);
            case 2:
                return new ApShell(id, type, toward, speed, x, y, hit);
            case 3:
                return new ElectromagneticShell(id, type, toward, speed, x, y, hit);
            default:
                return null;
        }
    }
}
