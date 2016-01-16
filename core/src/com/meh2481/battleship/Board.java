package com.meh2481.battleship;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Mark on 1/15/2016.
 */
public class Board
{
    //TODO Have variables for ships, methods for drawing
    //What else?

    private Texture m_txBoardBg;
    private Array<Ship> m_lShips;
    private Sprite m_sCenter, m_sEdge;

    public Board(Texture txBg, Texture txCenter, Texture txEdge)
    {
        m_txBoardBg = txBg;
        m_lShips = new Array<Ship>();
        m_sCenter = new Sprite(txCenter);
        m_sEdge = new Sprite(txEdge);
        Ship_Carrier sCarrier = new Ship_Carrier(m_sCenter, m_sEdge);
        Ship_Battleship sBattleship = new Ship_Battleship(m_sCenter, m_sEdge);
        Ship_Cruiser sCruiser = new Ship_Cruiser(m_sCenter, m_sEdge);
        Ship_Submarine sSubmarine = new Ship_Submarine(m_sCenter, m_sEdge);
        Ship_Destroyer sDestroyer = new Ship_Destroyer(m_sCenter, m_sEdge);
        m_lShips.add(sCarrier);
        m_lShips.add(sBattleship);
        m_lShips.add(sCruiser);
        m_lShips.add(sSubmarine);
        m_lShips.add(sDestroyer);
    }

    public void draw(boolean bHidden, Batch bBatch)
    {
        bBatch.draw(m_txBoardBg, 0, 0);
        bBatch.setColor(0,0,0,1);   //Draw ships black
        for(Ship s : m_lShips)
            s.draw(bHidden, bBatch);
        bBatch.setColor(Color.WHITE);   //Reset color
    }

    public boolean boardCleared()
    {
        for(Ship s : m_lShips)
        {
            if(!s.isSunk())
                return false;
        }
        return true;
    }

    public int shipsLeft()
    {
        int numLeft = 0;
        for(Ship s : m_lShips)
        {
            if(!s.isSunk())
                numLeft++;
        }
        return numLeft;
    }

    public void placeShipsRandom()
    {

    }
    //TODO Ship placement and test for collisions between ships
}
