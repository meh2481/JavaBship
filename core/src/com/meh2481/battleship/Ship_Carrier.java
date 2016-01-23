package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Carrier extends Ship
{
    public int getSize() { return Ship.SIZE_CARRIER; }
    public String getName() { return Ship.NAME_CARRIER; }
    public int getType() { return Ship.TYPE_CARRIER; }

    public Ship_Carrier(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}
