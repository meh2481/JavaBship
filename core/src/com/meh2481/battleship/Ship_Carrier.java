package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Carrier extends Ship
{
    protected int getSize() { return 5; }
    public String getName() { return "Carrier"; }

    public Ship_Carrier(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}
