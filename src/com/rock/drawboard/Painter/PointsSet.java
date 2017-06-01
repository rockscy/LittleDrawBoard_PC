/*
 软件作者： 熊锡君，时守刚
 软件版权归作者所有，其他人可以对软件进行修改，可以使用软件代码，（按类使用请保留作者信息）
*/
package com.rock.drawboard.Painter;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;


public class PointsSet implements Serializable{
    
    private ArrayList points;
    
    public PointsSet() {
        points = new ArrayList();
    }
    
    public PointsSet(int initCap) {
        points = new ArrayList(initCap);
    }
    
    public void addPoint(int x, int y) {
        int size = points.size();
        if (size > 0) {
            Point point = (Point) points.get(size-1);
            if (point.x == x && point.y == y)
                return;
        }
        Point p = new Point();
        p.x = x;
        p.y = y;
        points.add(p);
    }
    
    public int[][] getPoints() {
        int size = points.size();
        if (size == 0)
            return null;
        int[][] result = new int[2][size];
        for (int i=0; i<size; i++) {
            Point p = (Point) points.get(i);
            result[0][i] = p.x;
            result[1][i] = p.y;
        }
        return result;
    }
    
    public int[][] getPoints(int x, int y) {
        int size = points.size();
        if (size == 0)
            return null;
        int[][] result = new int[2][size+1];
        int i;
        for (i=0; i<size; i++) {
            Point p = (Point) points.get(i);
            result[0][i] = p.x;
            result[1][i] = p.y;
        }
        result[0][i] = x;
        result[1][i] = y;
        return result;
    }
    
}
