package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Cruiser extends Ship
{
    protected int getSize() { return 3; }
    public String getName() { return "Cruiser"; }

    public Ship_Cruiser(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}
