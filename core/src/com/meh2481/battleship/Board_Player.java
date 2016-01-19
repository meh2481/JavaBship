package com.meh2481.battleship;

import com.badlogic.gdx.graphics.Texture;

import java.awt.*;

/**
 * Created by Mark on 1/18/2016.
 */
public class Board_Player extends Board
{
    private Point m_ptCurPos;   //Hold onto the current position of the ship we're placing
    private int m_iPlacing;    //For handling ship placement - current ship we're placing

    public Board_Player(Texture txBg, Texture txMiss, Texture txCenter, Texture txEdge)
    {
        super(txBg, txMiss, txCenter, txEdge);
        m_ptCurPos = new Point(-1,-1);
        m_iPlacing = -1;
    }

    public void startPlacingShips()
    {
        m_iPlacing = 0;
    }

    //Return true if done placing all ships, false otherwise
    public boolean placeShip(int xPos, int yPos)
    {
        if(m_iPlacing >= 0 && m_iPlacing < m_lShips.size)
        {
            moveShip(xPos, yPos);
            boolean bOKPlace = true;
            //Make sure this isn't overlapping other ships
            for(int i = 0; i < m_lShips.size; i++)
            {
                if(i == m_iPlacing)
                    continue;
                if(m_lShips.get(i).checkOverlap(m_lShips.get(m_iPlacing)))
                {
                    bOKPlace = false;
                    break;
                }
            }
            //We're good
            if(bOKPlace)
                m_iPlacing++;   //Go to next ship
        }
        if(m_iPlacing >= m_lShips.size)
            return true;
        return false;
    }

    //Move the current ship we're placing
    public void moveShip(int xPos, int yPos)
    {
        m_ptCurPos.x = xPos;
        m_ptCurPos.y = yPos;
        if(m_iPlacing >= 0 && m_iPlacing < m_lShips.size)
        {
            //Check and be sure we're not off the edge of the map.
            Ship sPlace = m_lShips.get(m_iPlacing);
            if(sPlace.isHorizontal())
            {
                if(sPlace.getSize() + xPos > BOARD_SIZE)
                    xPos = BOARD_SIZE - sPlace.getSize();   //Set position in the map if we're off it
            }
            else
            {
                if(sPlace.getSize() + yPos > BOARD_SIZE)
                    yPos = BOARD_SIZE - sPlace.getSize();
            }
            m_lShips.get(m_iPlacing).setPosition(xPos, yPos);
        }
    }

    public void rotateShip()    //Rotate the current ship we're placing
    {
        if(m_iPlacing >= 0 && m_iPlacing < m_lShips.size)
        {
            Ship sPlace = m_lShips.get(m_iPlacing);
            sPlace.setHorizontal(sPlace.isVertical());
            //Make sure we're not off the map after rotating by moving to the current position
            moveShip(m_ptCurPos.x, m_ptCurPos.y);
        }
    }
}
