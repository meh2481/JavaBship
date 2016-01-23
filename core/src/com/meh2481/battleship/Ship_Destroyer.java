package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Ship_Destroyer extends Ship
{
    public int getSize() { return Ship.SIZE_DESTROYER; }
    public String getName() { return Ship.NAME_DESTROYER; }
    public int getType() { return Ship.TYPE_DESTROYER; }

    public Ship_Destroyer(Sprite sCenter, Sprite sEdge)
    {
        super(sCenter, sEdge);
    }
}
