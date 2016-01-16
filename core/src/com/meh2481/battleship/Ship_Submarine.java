package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Submarine extends Ship
{
    public int getSize() { return 3; }
    public String getName() { return "Submarine"; }

    public Ship_Submarine(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}