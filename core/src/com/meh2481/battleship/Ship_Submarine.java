package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Submarine extends Ship
{
    public int getSize() { return Ship.SIZE_SUBMARINE; }
    public String getName() { return Ship.NAME_SUBMARINE; }
    public int getType() { return Ship.TYPE_SUBMARINE; }

    public Ship_Submarine(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}