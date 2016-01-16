package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Battleship extends Ship
{
    protected int getSize() { return 4; }
    public String getName() { return "Battleship"; }

    public Ship_Battleship(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}
