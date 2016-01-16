package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Destroyer extends Ship
{
    public int getSize() { return 2; }
    public String getName() { return "Destroyer"; }

    public Ship_Destroyer(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}
