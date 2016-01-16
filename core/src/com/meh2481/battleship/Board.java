package com.meh2481.battleship;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Mark on 1/15/2016.
 */
public class Board
{
    public static final int BOARD_SIZE = 10;
    //TODO Have variables for ships, methods for drawing
    //What else?

    private Texture m_txBoardBg;
    private Array<Ship> m_lShips;

    public Board(Texture txBg, Texture txCenter, Texture txEdge)
    {
        m_txBoardBg = txBg;
        m_lShips = new Array<Ship>();
        Sprite sCenter = new Sprite(txCenter);
        Sprite sEdge = new Sprite(txEdge);
        Ship_Carrier sCarrier = new Ship_Carrier(sCenter, sEdge);
        Ship_Battleship sBattleship = new Ship_Battleship(sCenter, sEdge);
        Ship_Cruiser sCruiser = new Ship_Cruiser(sCenter, sEdge);
        Ship_Submarine sSubmarine = new Ship_Submarine(sCenter, sEdge);
        Ship_Destroyer sDestroyer = new Ship_Destroyer(sCenter, sEdge);
        m_lShips.add(sCarrier);
        m_lShips.add(sBattleship);
        m_lShips.add(sCruiser);
        m_lShips.add(sSubmarine);
        m_lShips.add(sDestroyer);
    }

    public void draw(boolean bHidden, Batch bBatch)
    {
        bBatch.draw(m_txBoardBg, 0, 0);
        for(Ship s : m_lShips)
            s.draw(bHidden, bBatch);
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
        //Clear all positions
        for(Ship s : m_lShips)
            s.setPosition(-1, -1);

        //for(Ship s : m_lShips)
        for(int i = 0; i < m_lShips.size; i++)
        {
            int xPos = -1, yPos = -1;
            while(xPos < 0 || yPos < 0) //Loop until we find a good spot to place this ship
            {
                m_lShips.get(i).setHorizontal(MathUtils.randomBoolean());

                //Generate a new random position for this ship
                if(m_lShips.get(i).isHorizontal())
                {
                    xPos = MathUtils.random(0, BOARD_SIZE - m_lShips.get(i).getSize());
                    yPos = MathUtils.random(0, BOARD_SIZE - 1);
                }
                else
                {
                    xPos = MathUtils.random(0, BOARD_SIZE - 1);
                    yPos = MathUtils.random(0, BOARD_SIZE - m_lShips.get(i).getSize());
                }
                m_lShips.get(i).setPosition(xPos, yPos);

                //Make sure we're not colliding with any other ships in this location
                //for(Ship sTestCollide : m_lShips)
                for(int j = 0; j < i; j++)
                {
                    if(m_lShips.get(i).checkOverlap(m_lShips.get(j)))    //This ship overlaps another one
                    {
                        xPos = yPos = -1;
                        break;
                    }
                }

            }
        }
    }
}













