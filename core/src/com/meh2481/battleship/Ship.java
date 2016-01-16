package com.meh2481.battleship;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Mark on 1/15/2016.
 */
public abstract class Ship
{
    private Sprite m_sCenterSprite;
    private Sprite m_sEdgeSprite;
    private boolean m_bHorizontal;
    private int m_iXPos, m_iYPos;
    private Array<Integer> m_iHitPositions;

    abstract public String getName();
    abstract protected int getSize();

    public void rotateHorizontal() { m_bHorizontal = true; }
    public void rotateVertical() { m_bHorizontal = false; }
    public boolean isHorizontal() { return m_bHorizontal; }
    public boolean isVertical() { return !m_bHorizontal; }
    public void setCenterSprite(Sprite sSpr) { m_sCenterSprite = sSpr; }
    public void setEdgeSprite(Sprite sSpr) { m_sEdgeSprite = sSpr; }
    public boolean isSunk() { return m_iHitPositions.size == getSize(); }

    public boolean hitShip(int iXpos, int iYpos)
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

    public boolean isHit(int iXpos, int iYpos)
    {
        if(m_bHorizontal)
        {
            if(iYpos == m_iYPos &&
               iXpos >= m_iXPos &&
               iXpos <= m_iXPos + getSize())
                return true;
        }
        else
        {
            if(iXpos == m_iXPos &&
               iYpos >= m_iYPos &&
               iYpos <= m_iYPos + getSize())
                return true;
        }
        return false;
    }

    public void draw(boolean bHidden, Batch bBatch)
    {
        if(m_sCenterSprite == null || m_sEdgeSprite == null) return;

        if(bHidden)
        {
            //Only draw tiles that have been hit
            for(int i : m_iHitPositions)
            {
                float x = m_iXPos + ((m_bHorizontal)?(i):(0))*m_sCenterSprite.getWidth();
                float y = m_iYPos + ((m_bHorizontal)?(0):(i))*m_sCenterSprite.getHeight();
                m_sEdgeSprite.setPosition(x, y);
                m_sEdgeSprite.draw(bBatch);
                m_sCenterSprite.setPosition(x, y);
                m_sCenterSprite.draw(bBatch);
            }
        }
        else
        {
            //Draw all ship tile edges first
            for (int i = 0; i < getSize(); i++)
            {
                //Draw horizontally or vertically depending on our rotation
                m_sEdgeSprite.setPosition(m_iXPos + ((m_bHorizontal)?(i):(0))*m_sEdgeSprite.getWidth(), m_iYPos + ((m_bHorizontal)?(0):(i))*m_sEdgeSprite.getHeight());
                m_sEdgeSprite.draw(bBatch);
            }
            //Draw center piece for tiles that have been hit
            for(int i : m_iHitPositions)
            {
                m_sCenterSprite.setPosition(m_iXPos + ((m_bHorizontal)?(i):(0))*m_sCenterSprite.getWidth(), m_iYPos + ((m_bHorizontal)?(0):(i))*m_sCenterSprite.getHeight());
                m_sCenterSprite.draw(bBatch);
            }
        }
    }
}


























