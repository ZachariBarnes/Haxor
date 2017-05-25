package com.h4x0r.h4x0r;

/**
 * Created by Zachari on 10/11/2016.
 */

public class Coordinate {

    public int x;
    public int y;
    public boolean isValid;
    public Boolean isConnected;

    Coordinate(int x, int y)
    {
        this.x=x;
        this.y=y;
        isConnected=null;
    }


}