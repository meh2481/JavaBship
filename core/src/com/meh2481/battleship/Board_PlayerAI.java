package com.meh2481.battleship;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Mark on 1/18/2016.
 */
public class Board_PlayerAI extends Board
{
    private int m_iPlacing;    //For handling ship placement - current ship we're placing

    public Board_PlayerAI(Texture txBg, Texture txMiss, Texture txCenter, Texture txEdge)
    {
        super(txBg, txMiss, txCenter, txEdge);

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
            //Make sure we're not off the screen
            int iShipSize = m_lShips.get(m_iPlacing).getSize();
            if(m_lShips.get(m_iPlacing).isHorizontal())
            {
                if(iShipSize + xPos > BOARD_SIZE)
                    bOKPlace = false;
            }
            else
            {
                if(iShipSize + yPos > BOARD_SIZE)
                    bOKPlace = false;
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
        if(m_iPlacing >= 0 && m_iPlacing < m_lShips.size)
            m_lShips.get(m_iPlacing).setPosition(xPos, yPos);
    }

    public void rotateShip()    //Rotate the current ship we're placing
    {
        if(m_iPlacing >= 0 && m_iPlacing < m_lShips.size)
            m_lShips.get(m_iPlacing).setHorizontal(m_lShips.get(m_iPlacing).isVertical());
    }
}
