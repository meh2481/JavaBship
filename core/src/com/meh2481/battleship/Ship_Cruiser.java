package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Cruiser extends Ship
{
    public int getSize() { return Ship.SIZE_CRUISER; }
    public String getName() { return Ship.NAME_CRUISER; }
    public int getType() { return Ship.TYPE_CRUISER; }

    public Ship_Cruiser(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}
