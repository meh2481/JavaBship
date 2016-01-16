package com.meh2481.battleship;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Mark on 1/15/2016.
 */
public class Board
{
    //TODO Have variables for ships, methods for drawing
    //What else?

    private Texture m_txBoardBg;
    private Ship_Carrier m_sCarrier;
    private Ship_Battleship m_sBattleship;
    private Ship_Cruiser m_sCruiser;
    private Ship_Submarine m_sSubmarine;
    private Ship_Destroyer m_sDestroyer;

    public Board(Texture txBg, Sprite sCenter, Sprite sEdge)
    {
        m_txBoardBg = txBg;
        m_sCarrier = new Ship_Carrier(sCenter, sEdge);
        m_sBattleship = new Ship_Battleship(sCenter, sEdge);
        m_sCruiser = new Ship_Cruiser(sCenter, sEdge);
        m_sSubmarine = new Ship_Submarine(sCenter, sEdge);
        m_sDestroyer = new Ship_Destroyer(sCenter, sEdge);
    }

    public void draw(boolean bHidden, Batch bBatch)
    {
        bBatch.draw(m_txBoardBg, 0, 0);
        bBatch.setColor(0,0,0,1);   //Draw ships black
        m_sCarrier.draw(bHidden, bBatch);
        m_sBattleship.draw(bHidden, bBatch);
        m_sCruiser.draw(bHidden, bBatch);
        m_sSubmarine.draw(bHidden, bBatch);
        m_sDestroyer.draw(bHidden, bBatch);
        bBatch.setColor(Color.WHITE);
    }

    public boolean boardCleared()
    {
        return (m_sCarrier.isSunk() &&
                m_sBattleship.isSunk() &&
                m_sCruiser.isSunk() &&
                m_sSubmarine.isSunk() &&
                m_sDestroyer.isSunk());
    }

    public int shipsLeft()
    {
        int numLeft = 0;
        if(!m_sCarrier.isSunk()) numLeft++;
        if(!m_sBattleship.isSunk()) numLeft++;
        if(!m_sCruiser.isSunk()) numLeft++;
        if(!m_sSubmarine.isSunk()) numLeft++;
        if(!m_sDestroyer.isSunk()) numLeft++;
        return numLeft;
    }

    //TODO Ship placement and test for collisions between ships
}
