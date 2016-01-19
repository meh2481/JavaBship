package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import java.awt.*;

/**
 * Created by Mark on 1/15/2016.
 */
public abstract class Ship
{
    private Sprite m_sShipHitSprite;
    private Sprite m_sShipOKSprite;
    private boolean m_bHorizontal;
    private int m_iXPos, m_iYPos;
    private Array<Integer> m_iHitPositions;

    abstract public String getName();
    abstract public int getSize();

    public void rotateHorizontal() { m_bHorizontal = true; }
    public void rotateVertical() { m_bHorizontal = false; }
    public void setHorizontal(boolean b) { m_bHorizontal = b; }
    public boolean isHorizontal() { return m_bHorizontal; }
    public boolean isVertical() { return !m_bHorizontal; }
    public void setPosition(int x, int y) { m_iXPos = x; m_iYPos = y; }
    public Point getPosition() { return new Point(m_iXPos, m_iYPos); }
    public void setCenterSprite(Sprite sSpr) { m_sShipHitSprite = sSpr; }
    public void setEdgeSprite(Sprite sSpr) { m_sShipOKSprite = sSpr; }
    public boolean isSunk() { return m_iHitPositions.size == getSize(); }

    public Ship(Sprite sShipHit, Sprite sShipOK)
    {
        m_sShipHitSprite = sShipHit;
        m_sShipOKSprite = sShipOK;
        reset();
    }

    public void reset()
    {
        m_iXPos = m_iYPos = -1;
        m_bHorizontal = true;
        m_iHitPositions = new Array<Integer>();
    }

    //Returns true and marks as hit if hit, returns false on miss
    public boolean fireAtShip(int iXpos, int iYpos)
    {
        if(isHit(iXpos, iYpos))
        {
            if(m_bHorizontal)
                m_iHitPositions.add(iXpos - m_iXPos);
            else
                m_iHitPositions.add(iYpos - m_iYPos);
            return true;
        }
        return false;
    }

    //Test if iXpos, iYpos is a position the ship has already been hit on. Return true if so, false if not
    public boolean alreadyHit(int iXpos, int iYpos)
    {
        if(isHit(iXpos, iYpos))
        {
            for(int i : m_iHitPositions)
            {
                if(m_bHorizontal)
                {
                    if(i == iXpos - m_iXPos)
                        return true;
                }
                else
                {
                    if(i == iYpos - m_iYPos)
                        return true;
                }
            }
        }
        return false;
    }

    //Test if iXpos, iYpos is a location on the ship. Return true if it is, false if not
    public boolean isHit(int iXpos, int iYpos)
    {
        if(m_bHorizontal)
        {
            if(iYpos == m_iYPos &&
               iXpos >= m_iXPos &&
               iXpos < m_iXPos + getSize())
                return true;
        }
        else
        {
            if(iXpos == m_iXPos &&
               iYpos >= m_iYPos &&
               iYpos < m_iYPos + getSize())
                return true;
        }
        return false;
    }

    public void draw(boolean bHidden, Batch bBatch)
    {
        if(m_sShipHitSprite == null || m_sShipOKSprite == null) return;
        if(m_iXPos < 0 || m_iYPos < 0) return;

        if(bHidden)
        {
            //Only draw tiles that have been hit
            for(int i : m_iHitPositions)
            {
                //Draw both center and edge for hit tiles
                float x = (m_iXPos + ((m_bHorizontal)?(i):(0)))* m_sShipHitSprite.getWidth();
                float y = (m_iYPos + ((m_bHorizontal)?(0):(i)))* m_sShipHitSprite.getHeight();
                m_sShipOKSprite.setPosition(x, y);
                m_sShipOKSprite.draw(bBatch);
                m_sShipHitSprite.setPosition(x, y);
                m_sShipHitSprite.draw(bBatch);
            }
        }
        else
        {
            //Draw all ship tiles first
            for (int i = 0; i < getSize(); i++)
            {
                //Draw horizontally or vertically depending on our rotation
                m_sShipOKSprite.setPosition((m_iXPos + ((m_bHorizontal)?(i):(0)))* m_sShipOKSprite.getWidth(), (m_iYPos + ((m_bHorizontal)?(0):(i)))* m_sShipOKSprite.getHeight());
                m_sShipOKSprite.draw(bBatch);
            }
            //Draw image for tiles on the ship that have been hit
            for(int i : m_iHitPositions)
            {
                m_sShipHitSprite.setPosition((m_iXPos + ((m_bHorizontal)?(i):(0)))* m_sShipHitSprite.getWidth(), (m_iYPos + ((m_bHorizontal)?(0):(i)))* m_sShipHitSprite.getHeight());
                m_sShipHitSprite.draw(bBatch);
            }
        }
    }

    //Return true if these ships overlap, false otherwise
    //TODO Check and see if ship pos 1 = 0 is causing this to return true when it shouldn't...
    public boolean checkOverlap(Ship sOther)
    {
        boolean bOverlapping = false;
        //Both ships horizontal
        if(m_bHorizontal && sOther.m_bHorizontal)
        {
            //Only colliding if on the same row
            if(m_iYPos == sOther.m_iYPos)
            {
                if((m_iXPos <= sOther.m_iXPos && m_iXPos + getSize() > sOther.m_iXPos) || //To the left of other ship and overlapping
                   (sOther.m_iXPos + sOther.getSize() > m_iXPos && sOther.m_iXPos <= m_iXPos)) //To the right of other ship and overlapping
                    bOverlapping = true;
            }
        }
        //Both ships vertical
        else if(!m_bHorizontal && !sOther.m_bHorizontal)
        {
            //Only colliding if in the same column
            if(m_iXPos == sOther.m_iXPos)
            {
                if((m_iYPos <= sOther.m_iYPos && m_iYPos + getSize() > sOther.m_iYPos) || //Above other ship and overlapping
                   (sOther.m_iYPos + sOther.getSize() > m_iYPos && sOther.m_iYPos <= m_iYPos)) //Below other ship and overlapping
                    bOverlapping = true;
            }
        }
        //This ship horizontal, other ship vertical
        else if(m_bHorizontal)
        {
            //Test to see if any square of both ships are colliding
            if(m_iXPos <= sOther.m_iXPos && //Our left side has to be to the left of or colliding with the other ship
               m_iXPos + getSize() > sOther.m_iXPos && //Our right side has to be to the right of or colliding with the other ship
               sOther.m_iYPos <= m_iYPos && //The other ship's top side has to be above or colliding with our ship
               sOther.m_iYPos + sOther.getSize() > m_iYPos) //And the other ship's bottom side has to be below or colliding with our ship
                bOverlapping = true;
        }
        //This ship vertical, other ship horizontal
        else
        {
            //Same as above test
            if(sOther.m_iXPos <= m_iXPos &&
               sOther.m_iXPos + sOther.getSize() > m_iXPos &&
               m_iYPos <= sOther.m_iYPos &&
               m_iYPos + getSize() > sOther.m_iYPos)
                bOverlapping = true;
        }

        return bOverlapping;
    }
}


























