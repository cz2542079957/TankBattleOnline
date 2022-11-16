package com.tank.networktanksserver.utils;

import java.util.Random;

public class MapUtils
{
    //    0 空地
//    1 砖墙
//    2 草丛
//    3 河流
//    4 铁墙
//    5 光滑砖
//    9 老巢
//    10 死亡老巢
    public static int brickCell, grassCell, riverCell, ironCell, tileCell;

    public static int hasCreateCell = 0;


    //创建地图
    public static int[][] createMap()
    {
        int[][] map = new int[28][28];
        Random random = new Random();
        int blockCell = 500;    //防止其他地块不够出现创建死循环
        hasCreateCell = 0;
        brickCell = random.nextInt(80) + 10;
        grassCell = random.nextInt(50);
        riverCell = random.nextInt(20) * random.nextInt(2);
        ironCell = random.nextInt(50) * random.nextInt(2);
        tileCell = random.nextInt(30) * random.nextInt(2);
        int type = 0;
        boolean flag = true;    //如果地块没被用完则继续创建
        int x, y;  //随机生成位置
        //地图基础生成
        while (flag)
        {
            create:
            {
                x = random.nextInt(13) + 1;
                y = random.nextInt(13) + 1;
                if (map[x * 2 - 1][y * 2 - 1] != 0) continue;
                type = random.nextInt(6);
                switch (type)
                {
                    case 0:
                        break create;
                    case 1:
                        if (brickCell != 0)
                        {
                            brickCell--;
                            hasCreateCell++;
                        } else break create;
                        break;
                    case 2:
                        if (grassCell != 0)
                        {
                            grassCell--;
                            hasCreateCell++;
                        } else break create;
                        break;
                    case 3:
                        if (riverCell != 0)
                        {
                            riverCell--;
                            hasCreateCell++;
                        } else break create;
                        break;
                    case 4:
                        if (ironCell != 0)
                        {
                            ironCell--;
                            hasCreateCell++;
                        } else break create;
                        break;
                    case 5:
                        if (tileCell != 0)
                        {
                            createContinuousTile(map, x, y);
                        }
                        break create;
                }
                createMapCell(map, x * 2 - 1, y * 2 - 1, type);
                if (hasCreateCell == 13 * 13 || (brickCell == 0 && grassCell == 0 && riverCell == 0 && ironCell == 0 && tileCell == 0))
                    flag = false; //代表地块都已经用完了

            }

        }
        //清除出生点的障碍
        createMapRect(map, 1, 1, 2, 2, 0);
        createMapRect(map, 1, 13, 2, 14, 0);
        createMapRect(map, 1, 25, 2, 26, 0);
        createMapRect(map, 21, 9, 26, 18, 0);
        createMapCell(map, 1, 3, 0);
        createMapCell(map, 3, 1, 0);
        createMapCell(map, 1, 23, 0);
        createMapCell(map, 3, 25, 0);

        //创建基地
        createMapRect(map, 24, 12, 26, 15, 1);
        createMapRect(map, 25, 13, 26, 14, 9);

        return map;
    }

    //建造地块
    public static void createMapRect(int[][] map, int x1, int y1, int x2, int y2, int type)
    {
        for (int i = x1; i <= x2; i++)
            for (int j = y1; j <= y2; j++)
            {
                map[i][j] = type;
            }
    }

    //建造单元格 2*2 左上角为(x,y)
    public static void createMapCell(int[][] map, int x, int y, int type)
    {
        map[x][y] = map[x + 1][y] = map[x][y + 1] = map[x + 1][y + 1] = type;
    }

    public static void createContinuousTile(int[][] map, int x, int y)
    {
        if (tileCell <= 0) return;
        int[] offsetX = {-1, 0, 1, 0};
        int[] offsetY = {0, 1, 0, -1};
        for (int i = 0; i < 4; i++)
        {
            int nx = x + offsetX[i];
            int ny = y + offsetY[i];
            if (tileCell <= 0 || nx < 1 || nx > 13 || ny < 1 || ny > 13 || map[nx * 2 - 1][ny * 2 - 1] != 0) continue;
            if (new Random().nextInt(3) == 1)
            {
                tileCell--;
                hasCreateCell++;
                createMapCell(map, nx * 2 - 1, ny * 2 - 1, 5);
                createContinuousTile(map, nx, ny);
            }
        }
    }


}
